package com.sadjier.model.vo.user;

import com.sadjier.enums.UserRolesEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "用户登录VO")
public class UserLoginVO {
    /// <summary>用户id</summary>
    @Schema(description = "用户id")
    private Long userId;
    /// <summary>身份</summary>
    @Schema(description = "身份：admin/user")
    private UserRolesEnum role;
    /// <summary>账号</summary>
    @Schema(description = "账号")
    private String username;
    /// <summary>Token</summary>
    @Schema(description = "Token")
    private String token;
}