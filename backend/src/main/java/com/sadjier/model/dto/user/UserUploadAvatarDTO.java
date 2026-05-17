package com.sadjier.model.dto.user;

import com.sadjier.constant.ResultMsgConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "头像上传DTO")
public class UserUploadAvatarDTO {
    /// <summary>头像文件</summary>
    @NotNull(message = ResultMsgConstant.USER_AVATAR_REQUIRED)
    @Schema(description = "头像文件",requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile file;
}