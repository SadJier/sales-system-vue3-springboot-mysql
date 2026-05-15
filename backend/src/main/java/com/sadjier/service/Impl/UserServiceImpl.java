package com.sadjier.service.Impl;

import com.sadjier.dao.SysUserRepository;
import com.sadjier.enums.UserRole;
import com.sadjier.model.dto.UserLoginDTO;
import com.sadjier.model.dto.UserRegisterDTO;
import com.sadjier.common.Result;
import com.sadjier.model.dto.UserUpdatePasswordDTO;
import com.sadjier.model.dto.UserUploadAvatarDTO;
import com.sadjier.model.entity.SysUser;
import com.sadjier.model.vo.SysUserVO;
import com.sadjier.model.vo.UserLoginVO;
import com.sadjier.service.UserService;
import com.sadjier.util.CommonUtil;
import com.sadjier.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

/// <summary>用户业务实现</summary>
@Service
@Slf4j
@Tag(name = "用户业务实现")
public class UserServiceImpl implements UserService {
    /// <summary>系统用户仓储</summary>
    @Autowired
    @Schema(description = "系统用户仓储")
    private SysUserRepository sys_user_repository;
    /// <summary>密码加密器</summary>
    @Autowired
    @Schema(description = "密码加密器")
    private PasswordEncoder password_encoder;
    /// <summary>令牌生成器</summary>
    @Autowired
    @Schema(description = "令牌生成器")
    private JwtUtil jwt_util;
    /// <summary>redis</summary>
    @Autowired
    private RedisTemplate<Object, Object> redis_template;

    /// <summary>登录验证</summary>
    @Operation(summary = "登录验证")
    public Result<UserLoginVO> login(UserLoginDTO dto) {
        /// 用户名
        String user_name = dto.getUsername();
        /// 密码
        String password = dto.getPassword();
        UserRole role = dto.getRole();
        log.info("用户登录请求:用户名({}),密码({}),身份({})", user_name,password,role);
        if (!StringUtils.hasText(user_name) || !StringUtils.hasText(password)) {
            return Result.error("用户名或密码不能为空");
        }
        /// 用户信息
        SysUser sys_user = sys_user_repository.findByUserName(user_name);
        if (sys_user == null || !sys_user.getRole().equals(role)) {
            return Result.error("账号不存在");
        }
//        if (!"ACTIVE".equals(sys_user.getStatus())) {
//            return Result.error("账号已停用");
//        }
        if (!password_encoder.matches(password, sys_user.getPassword())) {
            return Result.error("密码错误");
        }
        sys_user.setLoginTime(LocalDateTime.now());
        sys_user_repository.save(sys_user);
        /// 返回数据
        UserLoginVO result_data = new UserLoginVO();
        result_data.setUserId(sys_user.getUserId());//可用于前端显示uid
        result_data.setUsername(sys_user.getUserName());
        result_data.setRole(sys_user.getRole());
        String token = jwt_util.generateToken(user_name);
        result_data.setToken(token);
        return Result.success(result_data);
    }
    /// <summary>注册</summary>
    public Result<String> register(UserRegisterDTO dto) {
        //用户名
        String user_name = (String) dto.getUsername();
        //密码
        String password = (String) dto.getPassword();
        //角色
        UserRole role = dto.getRole();
        log.info("用户注册请求:用户名({}),密码({}),身份({})", user_name,password,role);

        if (!StringUtils.hasText(user_name) || !StringUtils.hasText(password)) {
            return Result.error("用户名或密码不能为空");
        }
        //用户信息
        SysUser exist_user = sys_user_repository.findByUserName(user_name);
        if (exist_user != null) {
            return Result.error("用户名已存在");
        }
        //新用户
        SysUser new_user = new SysUser();
        new_user.setUserName(user_name);
        new_user.setPassword(password_encoder.encode(password));
        new_user.setStatus("ACTIVE");//暂未启用
        new_user.setRole(role);
        new_user.setCreateTime(LocalDateTime.now());
        sys_user_repository.save(new_user);
        return Result.success("注册成功");
    }
    /// <summary>退出登录</summary>
    public Result<String> logout(String token) {
        if (token == null) {
            return Result.success("退出登录成功");
        }

        var claims = jwt_util.parseToken(token);
        var username = jwt_util.getUsername(claims);
        log.info("用户({})登出",username);
        // TODO:让token过期的操作
        redis_template.opsForValue().set(token,"logout", jwt_util.getRemainingTime(claims), java.util.concurrent.TimeUnit.MILLISECONDS);
        return Result.success("退出登录成功");
    }
    /// <summary>用户名模糊查询</summary>
    public Result<List<SysUserVO>> searchByUserName(String user_name) {
        if (!StringUtils.hasText(user_name)) {
            return Result.error("用户名不能为空");
        }
        /// <summary>用户列表</summary>
        List<SysUserVO> user_list = sys_user_repository.findByUserNameContaining(user_name).stream().map(SysUserVO::new).toList();
        return Result.success(user_list);
    }
    /// <summary>更改密码</summary>
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updatePassword(UserUpdatePasswordDTO dto) {
        var username = dto.getUsername();
        String old_password = dto.getOldPassword();
        String new_password = dto.getNewPassword();
        log.info("用户更新密码请求:用户名({}),旧密码({}),新密码({})", username,old_password,new_password);
        SysUser sys_user = sys_user_repository.findByUserName(username);
        if (sys_user == null) {
            return Result.error("账号不存在");
        }
        if (!password_encoder.matches(old_password, sys_user.getPassword())) {
            return Result.error("旧密码错误");
        }
        if(!password_encoder.matches(new_password, sys_user.getPassword())) {
            return Result.error("新密码不能与旧密码相同");
        }
        sys_user.setPassword(password_encoder.encode(new_password));
        sys_user_repository.save(sys_user);
        return Result.success("密码更新成功");
    }
    /// <summary>上传头像</summary>
    public Result<String> uploadAvatar(UserUploadAvatarDTO dto) {
        String absolute_path = CommonUtil.getAvatarFolderPath();
        log.info("用户ID({})上传头像", dto.getUserId());
        File folder = new File(absolute_path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String file_name = dto.getUserId() + ".jpg";
        File dest_file = new File(absolute_path + "/" + file_name);
        try{
            dto.getFile().transferTo(dest_file);
        }catch (Exception e){
            log.error("上传头像失败:{}",e.getMessage());
            return Result.error("头像上传失败");
        }
        return Result.success("头像上传成功");
    }
    /// <summary>获取头像</summary>
    public Resource getAvatar(Long user_id) {
        String absolute_path = CommonUtil.getAvatarFolderPath();
        String file_name = user_id + ".jpg";//当前只有Jpg
        File avatar_file = new File(absolute_path + "/" + file_name);
        if (!avatar_file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到头像");
        }
        return new FileSystemResource(avatar_file);
    }
}
