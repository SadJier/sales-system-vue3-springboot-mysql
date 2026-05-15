package com.sadjier.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户身份")
public enum UserRole {
    @Schema(description = "商家")
    MERCHANT,
    @Schema(description = "管理员")
    ADMIN
}
