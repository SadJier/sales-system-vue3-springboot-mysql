package com.sadjier.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/// <summary>店铺统计实体类</summary>
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "store_stats")
public class StoreStats {
    /// <summary>主键ID</summary>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /// <summary>商家ID</summary>
    @Column(name = "merchant_id", unique = true)
    private Long merchantId;
    /// <summary>总订单数</summary>
    @Column(name = "total_orders")
    private Long totalOrders;
    /// <summary>未支付订单数</summary>
    @Column(name = "unpaid_orders")
    private Long unpaidOrders;
    /// <summary>已支付订单数</summary>
    @Column(name = "paid_orders")
    private Long paidOrders;
    /// <summary>已发货订单数</summary>
    @Column(name = "shipped_orders")
    private Long shippedOrders;
    /// <summary>已完成订单数</summary>
    @Column(name = "completed_orders")
    private Long completedOrders;
    /// <summary>已取消订单数</summary>
    @Column(name = "cancelled_orders")
    private Long cancelledOrders;
    /// <summary>总收入</summary>
    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;
    /// <summary>商品销售统计JSON</summary>
    @Column(name = "product_sales_json", columnDefinition = "TEXT")
    private String productSalesJson;
    /// <summary>最后更新时间</summary>
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
