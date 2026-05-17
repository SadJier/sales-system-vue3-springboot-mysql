package com.sadjier.model.dto.product;

import com.sadjier.constant.ResultMsgConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "商品更新DTO")
public class ProductUpdateDTO{
    /// <summary>商品ID</summary>
    @Schema(description = "商品ID,用于确定修改的商品",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = ResultMsgConstant.PRODUCT_ID_REQUIRED)
    private Long productId;
    /// <summary>名称</summary>
    @Schema(description = "名称",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String name;
    /// <summary>分类</summary>
    @Schema(description = "商品分类ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private Long categoryId;
    /// <summary>进价</summary>
    @Schema(description = "进价",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private BigDecimal purchasePrice;
    /// <summary>售价</summary>
    @Schema(description = "售价",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private BigDecimal salePrice;
    /// <summary>库存</summary>
    @Schema(description = "库存",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private Integer stock;
}