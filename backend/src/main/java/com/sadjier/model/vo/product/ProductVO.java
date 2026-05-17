package com.sadjier.model.vo.product;

import com.sadjier.model.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "商品详细信息VO")
public class ProductVO {
    /// <summary>商品唯一标识</summary>
    @Schema(description = "唯一标识")
    private Long productId;
    /// <summary>商品名称</summary>
    @Schema(description = "名称")
    private String name;
    /// <summary>商品分类Id</summary>
    @Schema(description = "商品分类Id")
    private Long categoryId;
    /// <summary>商品进价</summary>
    @Schema(description = "进价")
    private BigDecimal purchasePrice;
    /// <summary>商品售价</summary>
    @Schema(description = "售价")
    private BigDecimal salePrice;
    /// <summary>库存数量</summary>
    @Schema(description = "库存数量")
    private Integer stock;
    /// <summary>创建时间</summary>
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public ProductVO(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.categoryId =  product.getProductId();
        this.purchasePrice = product.getPurchasePrice();
        this.salePrice = product.getSalePrice();
        this.stock = product.getStock();
        this.createTime = product.getCreateTime();
    }
}
