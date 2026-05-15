package com.sadjier.model.dto;

import com.sadjier.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "用户注册DTO")
public class UserRegisterDTO {
    /// <summary>身份</summary>
    @NotBlank(message = "身份不能为空")
    @Schema(description = "身份",examples = {"admin","user"})
    private UserRole role;
    /// <summary>账号</summary>
    @NotBlank(message = "账号不能为空")
    @Schema(description = "账号")
    private String username;
    /// <summary>密码</summary>
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
}
