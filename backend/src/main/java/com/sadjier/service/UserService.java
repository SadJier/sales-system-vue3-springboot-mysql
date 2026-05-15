package com.sadjier.service;

import com.sadjier.model.dto.UserLoginDTO;
import com.sadjier.model.dto.UserRegisterDTO;
import com.sadjier.common.Result;
import com.sadjier.model.dto.UserUpdatePasswordDTO;
import com.sadjier.model.dto.UserUploadAvatarDTO;
import com.sadjier.model.vo.SysUserVO;
import com.sadjier.model.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.hibernate.engine.spi.Resolution;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/// <summary>用户业务接口</summary>
public interface UserService {
    /// <summary>登录验证</summary>
    Result<UserLoginVO> login(UserLoginDTO dto);
    /// <summary>注册</summary>
    Result<String> register(UserRegisterDTO dto);
    /// <summary>退出登录</summary>
    Result<String> logout(String token);
    /// <summary>用户名模糊查询</summary>
    Result<List<SysUserVO>> searchByUserName(String user_name);
    /// <summary>更新密码</summary>
    Result<String> updatePassword(UserUpdatePasswordDTO dto);
    /// <summary>上传头像</summary>
    Result<String> uploadAvatar(UserUploadAvatarDTO dto);
    /// <summary>获取头像</summary>
    Resource getAvatar(Long user_id);
}
