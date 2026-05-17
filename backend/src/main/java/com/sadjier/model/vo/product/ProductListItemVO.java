package com.sadjier.model.vo.product;

import com.sadjier.model.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "列表商品项显示信息VO")
public class ProductListItemVO {
    /// <summary>商品唯一标识</summary>
    @Schema(description = "商品唯一标识")
    private Long productId;
    /// <summary>商品名称</summary>
    @Schema(description = "商品名称")
    private String name;
    /// <summary>商品所属商家名称</summary>
    @Schema(description = "商品所属商家名称")
    private String merchantName;
    /// <summary>商品分类Id</summary>
    @Schema(description = "商品分类Id")
    private Long categoryId;
    /// <summary>商品进价</summary>
    @Schema(description = "商品进价")
    private BigDecimal purchasePrice;
    /// <summary>商品售价</summary>
    @Schema(description = "商品售价")
    private BigDecimal salePrice;
    /// <summary>库存数量</summary>
    @Schema(description = "库存数量")
    private Integer stock;

    public ProductListItemVO(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.merchantName = product.getMerchant().getUserName();
        this.categoryId =  product.getCategory().getCategoryId();
        this.purchasePrice = product.getPurchasePrice();
        this.salePrice = product.getSalePrice();
        this.stock = product.getStock();
    }
}
