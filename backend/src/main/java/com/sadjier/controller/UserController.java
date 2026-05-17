package com.sadjier.controller;

import com.sadjier.common.Result;
import com.sadjier.model.dto.user.*;
import com.sadjier.model.vo.user.UserGetPageVO;
import com.sadjier.model.vo.user.UserLoginVO;
import com.sadjier.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/// <summary>用户管理控制器</summary>
@RestController
@Slf4j
@RequestMapping("/api/users")
@Tag(name = "用户管理")
public class UserController {
    /// <summary>用户服务</summary>
    @Autowired
    private UserService user_service;

    /// <summary>登录</summary>
    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        return user_service.login(dto);
    }
    /// <summary>注册</summary>
    @PostMapping("/register")
    @Operation(summary = "注册")
    public Result<String> register(@Valid @RequestBody UserRegisterDTO dto) {
        return user_service.register(dto);
    }
    /// <summary>退出登录</summary>
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<String> logout(@RequestHeader("Authorization") String token){
        return user_service.logout(token);
    }
    /// <summary>用户分页查询</summary>
    @GetMapping
    @Operation(summary = "用户分页查询")
    public Result<UserGetPageVO> getUserPage(@Valid @ModelAttribute UserGetPageDTO dto) {
        return user_service.getUserPage(dto);
    }
    /// <summary>更新密码</summary>
    @PutMapping("/update/password")
    @Operation(summary = "更新密码")
    public Result<String> updatePassword(@Valid @RequestBody UserUpdatePasswordDTO dto) {
        return user_service.updatePassword(dto);
    }
    /// <summary>上传头像</summary>
    @PostMapping("/upload/avatar")
    @Operation(summary = "上传头像")
    public Result<String> uploadAvatar(@Valid UserUploadAvatarDTO dto) {
        return user_service.uploadAvatar(dto);
    }
    /// <summary>获取头像</summary>
    @GetMapping("/avatars/{user_id}")
    @Operation(summary = "获取用户头像")
    public Resource getAvatar(@PathVariable("user_id") Long user_id) {
        return user_service.getAvatar(user_id);
    }
    /// <summary>删除用户</summary>
    @DeleteMapping("/delete/{user_id}")
    @Operation(summary = "删除用户")
    public Result<String> deleteUser(@PathVariable("user_id") Long user_id) {
        return user_service.deleteUser(user_id);
    }
}
