package com.sadjier.model.dto.user;

import com.sadjier.constant.ResultMsgConstant;
import com.sadjier.enums.UserRolesEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "用户登录DTO")
public class UserLoginDTO {
    /// <summary>身份</summary>
    @NotBlank(message = ResultMsgConstant.USER_ROLE_REQUIRED)
    @Schema(description = "身份",requiredMode = Schema.RequiredMode.REQUIRED)
    private UserRolesEnum role;
    /// <summary>账号</summary>
    @NotBlank(message = ResultMsgConstant.USER_NAME_REQUIRED)
    @Schema(description = "账号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    /// <summary>密码</summary>
    @NotBlank(message = ResultMsgConstant.USER_PASSWORD_REQUIRED)
    @Schema(description = "密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}