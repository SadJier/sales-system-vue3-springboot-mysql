package com.sadjier.model.vo.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/// <summary>商品销售占比项VO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "商品销售占比项VO")
public class ProductSaleVO {
    /// <summary>商品ID</summary>
    @Schema(description = "商品ID")
    private Long productId;
    /// <summary>商品名称</summary>
    @Schema(description = "商品名称")
    private String productName;
    /// <summary>销售数量</summary>
    @Schema(description = "销售数量")
    private Integer quantity;
    /// <summary>销售额</summary>
    @Schema(description = "销售额")
    private BigDecimal revenue;
}
