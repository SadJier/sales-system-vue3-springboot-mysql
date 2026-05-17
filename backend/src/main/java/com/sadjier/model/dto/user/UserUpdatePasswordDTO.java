package com.sadjier.model.dto.user;

import com.sadjier.constant.ResultMsgConstant;
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
    /// <summary>旧密码</summary>
    @NotBlank(message = ResultMsgConstant.USER_OLD_PASSWORD_REQUIRED)
    @Schema(description = "旧密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;
    /// <summary>新密码</summary>
    @NotBlank(message = ResultMsgConstant.USER_NEW_PASSWORD_REQUIRED)
    @Schema(description = "新密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
