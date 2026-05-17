package com.sadjier.model.dto.product;

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
@Schema(name = "商品上传图片DTO")
public class ProductUploadImageDTO {
    /// <summary>商品图片文件</summary>
    @NotNull(message = ResultMsgConstant.PRODUCT_IMAGE_REQUIRED)
    @Schema(description = "商品图片文件",requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile file;
    /// <summary>商品ID</summary>
    @NotNull(message = ResultMsgConstant.PRODUCT_ID_REQUIRED)
    @Schema(description = "商品ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;
}
