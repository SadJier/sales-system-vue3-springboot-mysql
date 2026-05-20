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
import com.sadjier.model.vo.user.TokenRefreshVO;
import com.sadjier.service.UserService;
import com.sadjier.util.CommonUtil;
import com.sadjier.util.JwtUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.concurrent.TimeUnit;

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
    /// <summary>HTTP请求</summary>
    @Autowired
    private HttpServletRequest http_request;
    /// <summary>HTTP响应</summary>
    @Autowired
    private HttpServletResponse http_response;

    /// <summary>登录验证</summary>
    public Result<UserLoginVO> login(UserLoginDTO dto) {
        String user_name = dto.getUsername();
        String password = dto.getPassword();
        UserRolesEnum role = dto.getRole();
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
        //生成双令牌
        String access_token = JwtUtil.generateToken(sys_user.getUserId(), sys_user.getRole());
        String refresh_token = JwtUtil.generateRefreshToken(sys_user.getUserId(), sys_user.getRole());
        //存储access token到redis白名单
        String access_key = JwtUtil.REDIS_ACCESS_PREFIX + sys_user.getUserId();
        redis_template.opsForValue().set(access_key, access_token, 10, TimeUnit.MINUTES);
        //存储refresht token到redis白名单并附带客户端信息
        String refresh_key = JwtUtil.REDIS_REFRESH_PREFIX + sys_user.getUserId();
        String client_info = buildClientInfo();
        redis_template.opsForValue().set(refresh_key, refresh_token + "|" + client_info, 7, TimeUnit.DAYS);
        //将refresh token写入cookie
        setRefreshTokenCookie(refresh_token);
        //返回数据
        UserLoginVO result_data = new UserLoginVO();
        result_data.setUserId(sys_user.getUserId());//可用于前端显示uid
        result_data.setUsername(sys_user.getUserName());
        result_data.setRole(sys_user.getRole());
        result_data.setToken(access_token);
        return Result.success(result_data, ResultMsgConstant.USER_LOGIN_SUCCESS);
    }
    /// <summary>注册</summary>
    public Result<String> register(UserRegisterDTO dto) {
        String user_name = (String) dto.getUsername();
        String password = (String) dto.getPassword();
        UserRolesEnum role = dto.getRole();
        SysUser exist_user = sys_user_repo.findByUserName(user_name);
        if (exist_user != null) {
            return Result.result(ResultStatusEnum.ERROR,ResultMsgConstant.USER_NAME_ALREADY_EXISTS);
        }
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
        var user_id = JwtUtil.getUserId(claims);
        if(user_id != null){
            //从白名单移除access token
            String access_key = JwtUtil.REDIS_ACCESS_PREFIX + user_id;
            redis_template.delete(access_key);
            //清除refresh token
            String refresh_key = JwtUtil.REDIS_REFRESH_PREFIX + user_id;
            redis_template.delete(refresh_key);
        }
        //清除Cookie
        clearRefreshTokenCookie();
        return Result.success(ResultMsgConstant.USER_LOGOUT_SUCCESS);
    }
    /// <summary>用户分页查询</summary>
    public Result<UserGetPageVO> getUserPage(UserGetPageDTO dto) {
        int page_index = dto.getPageIndex() <= 0 ? 0 : dto.getPageIndex()-1;
        int page_size = dto.getPageSize() <= 0 ? 10 : dto.getPageSize();
        //分页对象
        PageRequest page_request = PageRequest.of(page_index, page_size);
        //查询条件构造器
        Specification<SysUser> user_specification = (root, query, criteria_builder) -> {
            //条件集合
            List<Predicate> predicate_list = new ArrayList<>();
            if (StringUtils.hasText(dto.getUsername())) {
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
        if(password_encoder.matches(new_password, sys_user.getPassword())) {
            return Result.result(ResultStatusEnum.DATA_INVALID,ResultMsgConstant.USER_NEW_PASSWORD_SAME_AS_OLD);
        }
        sys_user.setPassword(password_encoder.encode(new_password));
        sys_user_repo.save(sys_user);
        return Result.success(ResultMsgConstant.USER_PASSWORD_UPDATE_SUCCESS);
    }
    /// <summary>上传头像</summary>
    public Result<String> uploadAvatar(UserUploadAvatarDTO dto) {
        var user_id = JwtUtil.getUserId();
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
        if(user_id == null) return Result.result(ResultStatusEnum.DATA_INVALID,ResultMsgConstant.USER_INVALID);
        if(sys_user_repo.findByUserId(user_id) == null) return Result.result(ResultStatusEnum.NO_DATA,ResultMsgConstant.USER_NOT_FOUND);
        sys_user_repo.deleteById(user_id);
        return Result.success(ResultMsgConstant.USER_DELETE_SUCCESS);
    }
    /// <summary>刷新访问令牌</summary>
    public Result<TokenRefreshVO> refreshToken(String refresh_token) {
        //校验刷新令牌格式和有效期
        if(!JwtUtil.validateRefreshToken(refresh_token)){
            return Result.result(ResultStatusEnum.TOKEN_INVALID, ResultMsgConstant.TOKEN_LOCAL_INVALID);
        }
        var claims = JwtUtil.parseToken(refresh_token);
        var user_id = JwtUtil.getUserId(claims);
        var role = JwtUtil.getUserRole(claims);
        if(user_id == null || role == null){
            return Result.result(ResultStatusEnum.TOKEN_INVALID, ResultMsgConstant.TOKEN_LOCAL_INVALID);
        }
        //校验redis中的刷新令牌
        String refresh_key = JwtUtil.REDIS_REFRESH_PREFIX + user_id;
        Object stored_value = redis_template.opsForValue().get(refresh_key);
        if(stored_value == null){
            return Result.result(ResultStatusEnum.TOKEN_INVALID, ResultMsgConstant.TOKEN_LOCAL_INVALID);
        }
        String stored_data = (String) stored_value;
        String current_data = refresh_token + "|" +  buildClientInfo();
        //验证refresh token和信息是否相同
        if(!current_data.equals(stored_data)){
            return Result.result(ResultStatusEnum.TOKEN_INVALID, ResultMsgConstant.TOKEN_LOCAL_INVALID);
        }
        //生成新的双令牌
        String new_access_token = JwtUtil.generateToken(user_id, role);
        String new_refresh_token = JwtUtil.generateRefreshToken(user_id, role);
        //更新access\refresh token白名单
        String access_key = JwtUtil.REDIS_ACCESS_PREFIX + user_id;
        redis_template.opsForValue().set(access_key, new_access_token, 10, TimeUnit.MINUTES);
        String new_client_info = buildClientInfo();
        redis_template.opsForValue().set(refresh_key, new_refresh_token + "|" + new_client_info, 7, TimeUnit.DAYS);
        //更新Cookie
        setRefreshTokenCookie(new_refresh_token);
        //返回新令牌
        TokenRefreshVO vo = new TokenRefreshVO();
        vo.setAccessToken(new_access_token);
        vo.setRefreshToken(new_refresh_token);
        return Result.success(vo, ResultMsgConstant.USER_TOKEN_REFRESH_SUCCESS);
    }
    /// <summary>构建客户端识别信息</summary>
    private String buildClientInfo() {
        String ip = http_request.getRemoteAddr();
        String user_agent = http_request.getHeader("User-Agent");
        return ip + "|" + (user_agent != null ? user_agent : "");
    }
    /// <summary>将refresh token写入cookie</summary>
    private void setRefreshTokenCookie(String refresh_token) {
        Cookie cookie = new Cookie(JwtUtil.COOKIE_REFRESH_NAME, refresh_token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        http_response.addCookie(cookie);
    }
    /// <summary>清除cookie的refresh token</summary>
    private void clearRefreshTokenCookie() {
        Cookie cookie = new Cookie(JwtUtil.COOKIE_REFRESH_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        http_response.addCookie(cookie);
    }
}
