package com.sadjier.model.entity;

import com.sadjier.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/// <summary>系统用户实体类</summary>
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "sys_user")
public class SysUser {
    /// <summary>用户唯一标识</summary>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    /// <summary>用户名</summary>
    @Column(name = "user_name")
    private String userName;
    /// <summary>登录密码</summary>
    @Column(name = "password")
    private String password;
    /// <summary>用户状态</summary>
    @Column(name = "status")
    private String status;
    /// <summary>用户角色</summary>
    @Column(name = "role")
    private UserRole role;
    /// <summary>创建时间</summary>
    @Column(name = "create_time")
    private LocalDateTime createTime;
    /// <summary>最后登录时间</summary>
    @Column(name = "login_time")
    private LocalDateTime loginTime;
}

