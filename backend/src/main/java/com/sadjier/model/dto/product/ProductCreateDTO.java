package com.sadjier.model.dto.product;

import com.sadjier.constant.ResultMsgConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/// <summary>商品创建DTO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "商品创建DTO")
public class ProductCreateDTO{
    @NotBlank(message = ResultMsgConstant.PRODUCT_NAME_REQUIRED)
    @Schema(description = "名称",requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
    @NotNull(message = ResultMsgConstant.PRODUCT_CATEGORY_REQUIRED)
    @Schema(description = "分类ID",requiredMode = Schema.RequiredMode.REQUIRED)
	private Long categoryId;
    @NotNull(message = ResultMsgConstant.PRODUCT_PURCHASE_PRICE_REQUIRED)
    @Schema(description = "进价",requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal purchasePrice;
    @NotNull(message = ResultMsgConstant.PRODUCT_SALE_PRICE_REQUIRED)
    @Schema(description = "售价",requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal salePrice;
    @NotNull(message = ResultMsgConstant.PRODUCT_STOCK_REQUIRED)
    @Schema(description = "库存数量",requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer stock;
}