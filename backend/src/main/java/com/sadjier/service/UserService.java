package com.sadjier.service;

import com.sadjier.common.Result;
import com.sadjier.model.dto.user.*;
import com.sadjier.model.vo.user.UserGetPageVO;
import com.sadjier.model.vo.user.UserLoginVO;
import org.springframework.core.io.Resource;

/// <summary>用户业务接口</summary>
public interface UserService {
    /// <summary>登录验证</summary>
    Result<UserLoginVO> login(UserLoginDTO dto);
    /// <summary>注册</summary>
    Result<String> register(UserRegisterDTO dto);
    /// <summary>退出登录</summary>
    Result<String> logout(String token);
    /// <summary>用户分页查询</summary>
    Result<UserGetPageVO> getUserPage(UserGetPageDTO dto);
    /// <summary>更新密码</summary>
    Result<String> updatePassword(UserUpdatePasswordDTO dto);
    /// <summary>上传头像</summary>
    Result<String> uploadAvatar(UserUploadAvatarDTO dto);
    /// <summary>获取头像</summary>
    Resource getAvatar(Long user_id);
    /// <summary>删除用户</summary>
    Result<String> deleteUser(Long user_id);
}
