package com.sadjier.model.vo.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/// <summary>店铺统计VO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "店铺统计VO")
public class StoreStatsVO {
    /// <summary>总订单数</summary>
    @Schema(description = "总订单数")
    private Long totalOrders;
    /// <summary>未支付订单数</summary>
    @Schema(description = "未支付订单数")
    private Long unpaidOrders;
    /// <summary>已支付订单数</summary>
    @Schema(description = "已支付订单数")
    private Long paidOrders;
    /// <summary>已发货订单数</summary>
    @Schema(description = "已发货订单数")
    private Long shippedOrders;
    /// <summary>已完成订单数</summary>
    @Schema(description = "已完成订单数")
    private Long completedOrders;
    /// <summary>已取消订单数</summary>
    @Schema(description = "已取消订单数")
    private Long cancelledOrders;
    /// <summary>总收入</summary>
    @Schema(description = "总收入")
    private BigDecimal totalRevenue;
    /// <summary>商品销售占比列表</summary>
    @Schema(description = "商品销售占比列表")
    private List<ProductSaleVO> productSales;
}
