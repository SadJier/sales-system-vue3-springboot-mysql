package com.sadjier.service.Impl;

import com.sadjier.dao.SysUserRepository;
import com.sadjier.enums.ResultStatusEnum;
import com.sadjier.enums.UserRolesEnum;
import com.sadjier.common.Result;
import com.sadjier.constant.ResultMsgConstant;
import com.sadjier.model.dto.user.*;
import com.sadjier.model.entity.SysUser;
import com.sadjier.model.vo.user.UserGetPageVO;
import com.sadjier.model.vo.user.UserListItemVO;
import com.sadjier.model.vo.user.UserLoginVO;
import com.sadjier.service.UserService;
import com.sadjier.util.CommonUtil;
import com.sadjier.util.JwtUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.Predicate;
import org.springframework.core.io.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// <summary>用户业务实现</summary>
@Service
@Slf4j
@Tag(name = "用户业务实现")
public class UserServiceImpl implements UserService {
    /// <summary>系统用户仓储</summary>
    @Autowired
    @Schema(description = "系统用户仓储")
    private SysUserRepository sys_user_repo;
    /// <summary>密码加密器</summary>
    @Autowired
    @Schema(description = "密码加密器")
    private PasswordEncoder password_encoder;
    /// <summary>redis</summary>
    @Autowired
    private RedisTemplate<Object, Object> redis_template;

    /// <summary>登录验证</summary>
    public Result<UserLoginVO> login(UserLoginDTO dto) {
        // 用户名
        String user_name = dto.getUsername();
        // 密码
        String password = dto.getPassword();
        UserRolesEnum role = dto.getRole();
//        log.info("用户登录请求:用户名({}),密码({}),身份({})", user_name,password,role);
        // 用户信息
        SysUser sys_user = sys_user_repo.findByUserName(user_name);
        if (sys_user == null || !sys_user.getRole().equals(role)) {
            return Result.result(ResultStatusEnum.NO_DATA,ResultMsgConstant.USER_NOT_FOUND);
        }
//        if (!"ACTIVE".equals(sys_user.getStatus())) {
//            return Result.error("账号已停用");
//        }
        if (!password_encoder.matches(password, sys_user.getPassword())) {
            return Result.result(ResultStatusEnum.ERROR,ResultMsgConstant.USER_PASSWORD_ERROR);
        }
        sys_user.setLoginTime(LocalDateTime.now());
        sys_user_repo.save(sys_user);
        // 返回数据
        UserLoginVO result_data = new UserLoginVO();
        result_data.setUserId(sys_user.getUserId());//可用于前端显示uid
        result_data.setUsername(sys_user.getUserName());
        result_data.setRole(sys_user.getRole());
        String token = JwtUtil.generateToken(sys_user.getUserId(), sys_user.getRole());
        result_data.setToken(token);
        return Result.success(result_data, ResultMsgConstant.USER_LOGIN_SUCCESS);
    }
    /// <summary>注册</summary>
    public Result<String> register(UserRegisterDTO dto) {
        //用户名
        String user_name = (String) dto.getUsername();
        //密码
        String password = (String) dto.getPassword();
        //角色
        UserRolesEnum role = dto.getRole();
//        log.info("用户注册请求:用户名({}),密码({}),身份({})", user_name,password,role);
        //用户信息
        SysUser exist_user = sys_user_repo.findByUserName(user_name);
        if (exist_user != null) {
            return Result.result(ResultStatusEnum.ERROR,ResultMsgConstant.USER_NAME_ALREADY_EXISTS);
        }
        //新用户
        SysUser new_user = new SysUser();
        new_user.setUserName(user_name);
        new_user.setPassword(password_encoder.encode(password));
        new_user.setStatus("ACTIVE");//暂未启用
        new_user.setRole(role);
        new_user.setCreateTime(LocalDateTime.now());
        sys_user_repo.save(new_user);
        return Result.success(ResultMsgConstant.USER_REGISTER_SUCCESS);
    }
    /// <summary>退出登录</summary>
    public Result<String> logout(String token) {
        if (token == null) {
            return Result.success(ResultMsgConstant.USER_LOGOUT_SUCCESS);
        }

        var claims = JwtUtil.parseToken(token);
        var username = JwtUtil.getUsername(claims);
//        log.info("用户({})登出",username);
        redis_template.opsForValue().set(token,"logout", JwtUtil.getRemainingTime(claims), java.util.concurrent.TimeUnit.MILLISECONDS);
        return Result.success(ResultMsgConstant.USER_LOGOUT_SUCCESS);
    }
    /// <summary>用户分页查询</summary>
    public Result<UserGetPageVO> getUserPage(UserGetPageDTO dto) {
//        log.info("分页查询用户");
        //验证权限
        var claims = JwtUtil.parseToken(CommonUtil.getToken());
        if(!Objects.equals(JwtUtil.getUserRole(claims), UserRolesEnum.ADMIN))
            return Result.result(ResultStatusEnum.DATA_NO_PERMISSION,ResultMsgConstant.USER_SEARCH_ONLY_ADMIN);
        //页码默认值
        int page_index = dto.getPageIndex() <= 0 ? 0 : dto.getPageIndex()-1;
        //每页数量默认值
        int page_size = dto.getPageSize() <= 0 ? 10 : dto.getPageSize();
        //分页对象
        PageRequest page_request = PageRequest.of(page_index, page_size);
        //查询条件构造器
        Specification<SysUser> user_specification = (root, query, criteria_builder) -> {
            //条件集合
            List<Predicate> predicate_list = new ArrayList<>();
            if (StringUtils.hasText(dto.getUsername())) {
                //名称条件
//                log.info("查询条件:用户名包含({})", dto.getUsername());
                Predicate name_predicate = criteria_builder.like(root.get("userName"), "%" + dto.getUsername() + "%");
                predicate_list.add(name_predicate);
            }
            return criteria_builder.and(predicate_list.toArray(new Predicate[0]));
        };
        //查询结果
        Page<SysUser> users_page = sys_user_repo.findAll(user_specification, page_request);
        UserGetPageVO result_data = new UserGetPageVO();
        result_data.setTotal(users_page.getTotalElements());
        result_data.setItems(users_page.stream().map(UserListItemVO::new).toList());
        return Result.success(result_data);
    }
    /// <summary>更改密码</summary>
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updatePassword(UserUpdatePasswordDTO dto) {
        var user_id = JwtUtil.getUserId(CommonUtil.getToken());
        if(user_id == null) return Result.result(ResultStatusEnum.DATA_MISSING,ResultMsgConstant.TOKEN_LOCAL_INVALID);
        String old_password = dto.getOldPassword();
        String new_password = dto.getNewPassword();
//        log.info("用户更新密码请求:userid({}),旧密码({}),新密码({})", user_id,old_password,new_password);
        SysUser sys_user = sys_user_repo.findByUserId(user_id);
        if (sys_user == null) {
            return Result.result(ResultStatusEnum.NO_DATA,ResultMsgConstant.USER_NOT_FOUND);
        }
        if (!password_encoder.matches(old_password, sys_user.getPassword())) {
            return Result.result(ResultStatusEnum.DATA_INVALID,ResultMsgConstant.USER_OLD_PASSWORD_ERROR);
        }
        if(!password_encoder.matches(new_password, sys_user.getPassword())) {
            return Result.result(ResultStatusEnum.DATA_INVALID,ResultMsgConstant.USER_NEW_PASSWORD_SAME_AS_OLD);
        }
        sys_user.setPassword(password_encoder.encode(new_password));
        sys_user_repo.save(sys_user);
        return Result.success(ResultMsgConstant.USER_PASSWORD_UPDATE_SUCCESS);
    }
    /// <summary>上传头像</summary>
    public Result<String> uploadAvatar(UserUploadAvatarDTO dto) {
        var user_id = JwtUtil.getUserId();
//        log.info("用户ID({})上传头像", user_id);

        if(!CommonUtil.uploadUserAvatar(user_id, dto.getFile()))
            return Result.result(ResultStatusEnum.ERROR,ResultMsgConstant.USER_AVATAR_UPLOAD_FAILED);
        return Result.success(ResultMsgConstant.USER_AVATAR_UPLOAD_SUCCESS);
    }
    /// <summary>获取头像</summary>
    public Resource getAvatar(Long user_id) {
        return CommonUtil.getUserAvatar(user_id);
    }
    /// <summary>删除用户</summary>
    public Result<String> deleteUser(Long user_id){
        //验证权限
        var claims = JwtUtil.parseToken(CommonUtil.getToken());
        var role = JwtUtil.getUserRole(claims);
        if(role != UserRolesEnum.ADMIN) return Result.result(ResultStatusEnum.DATA_NO_PERMISSION,ResultMsgConstant.USER_DELETE_ONLY_ADMIN);
        if(user_id == null) return Result.result(ResultStatusEnum.DATA_INVALID,ResultMsgConstant.USER_INVALID);
        if(sys_user_repo.findByUserId(user_id) == null) return Result.result(ResultStatusEnum.NO_DATA,ResultMsgConstant.USER_NOT_FOUND);
        sys_user_repo.deleteById(user_id);
        return Result.success(ResultMsgConstant.USER_DELETE_SUCCESS);
    }
}
