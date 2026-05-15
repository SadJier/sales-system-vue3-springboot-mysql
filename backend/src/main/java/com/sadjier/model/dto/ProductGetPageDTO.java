package com.sadjier.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "商品页获取DTO")
public class ProductGetPageDTO {
    /// <summary>商品名称</summary>
    @Schema(description = "商品名称")
    private String productName;
    /// <summary>商品分类</summary>
    @Schema(description = "商品分类")
    private String category;
    /// <summary>页码</summary>
    @Schema(description = "页码")
    private Integer pageIndex;
    /// <summary>页大小</summary>
    @Schema(description = "页大小")
    private Integer pageSize;
}
