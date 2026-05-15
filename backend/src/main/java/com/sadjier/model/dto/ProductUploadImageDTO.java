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
@Schema(name = "商品上传图片DTO")
public class ProductUploadImageDTO {
    /// <summary>商品图片文件</summary>
    @NotNull(message = "商品图片文件不能为空")
    @Schema(description = "商品图片文件")
    private MultipartFile file;
    /// <summary>商品ID</summary>
    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID")
    private Long productId;
    /// <summary>商家ID</summary>
    @NotNull(message = "商家ID不能为空")
    @Schema(description = "商家ID")
    private Long merchantId;
}
