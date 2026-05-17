package com.sadjier.model.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/// <summary>商品详情统计VO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "商品详情统计VO")
public class ProductStatsVO {
    /// <summary>商品ID</summary>
    @Schema(description = "商品ID")
    private Long productId;
    /// <summary>名称</summary>
    @Schema(description = "名称")
    private String name;
    /// <summary>分类名称</summary>
    @Schema(description = "分类名称Id")
    private String categoryId;
    /// <summary>进价</summary>
    @Schema(description = "进价")
    private BigDecimal purchasePrice;
    /// <summary>售价</summary>
    @Schema(description = "售价")
    private BigDecimal salePrice;
    /// <summary>库存</summary>
    @Schema(description = "库存")
    private Integer stock;
    /// <summary>创建时间</summary>
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    /// <summary>总销售量</summary>
    @Schema(description = "总销售量")
    private Integer totalSales;
    /// <summary>总销售额</summary>
    @Schema(description = "总销售额")
    private BigDecimal totalRevenue;
    /// <summary>近期销售记录</summary>
    @Schema(description = "近期销售记录")
    private List<SaleRecordVO> recentSales;
}
