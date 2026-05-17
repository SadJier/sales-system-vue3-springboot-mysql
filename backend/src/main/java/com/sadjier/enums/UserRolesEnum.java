package com.sadjier.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/// <summary>用户身份枚举</summary>
@Schema(description = "用户身份枚举")
public enum UserRolesEnum {
    /// <summary>商家</summary>
    @Schema(description = "商家")
    MERCHANT,
    /// <summary>管理员</summary>
    @Schema(description = "管理员")
    ADMIN
}
