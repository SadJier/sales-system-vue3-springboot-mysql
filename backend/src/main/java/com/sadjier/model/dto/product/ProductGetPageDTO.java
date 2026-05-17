package com.sadjier.model.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/// <summary>商品页获取DTO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "商品页获取DTO")
public class ProductGetPageDTO {
    /// <summary>商品名称</summary>
    @Schema(description = "商品名称,用于进行模糊搜索",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String productName;
    /// <summary>商品分类</summary>
    @Schema(description = "商品分类ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long categoryId;
    /// <summary>页码</summary>
    @Schema(description = "页码",requiredMode = Schema.RequiredMode.NOT_REQUIRED,defaultValue = "1")
    private Integer pageIndex;
    /// <summary>页大小</summary>
    @Schema(description = "页大小",requiredMode = Schema.RequiredMode.NOT_REQUIRED,defaultValue = "10")
    private Integer pageSize;
}
