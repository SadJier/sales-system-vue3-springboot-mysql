package com.sadjier.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "用户密码更新DTO")
public class UserUpdatePasswordDTO {
    /// <summary>账号</summary>
    @NotBlank(message = "账号不能为空")
    @Schema(description = "账号")
    private String username;
    /// <summary>旧密码</summary>
    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码")
    private String oldPassword;
    /// <summary>新密码</summary>
    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码")
    private String newPassword;
}
