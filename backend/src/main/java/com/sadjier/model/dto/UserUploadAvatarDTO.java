package com.sadjier.model.dto;

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
    @NotNull(message = "头像文件不能为空")
    @Schema(description = "头像文件")
    private MultipartFile file;
    /// <summary>用户ID</summary>
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;
}