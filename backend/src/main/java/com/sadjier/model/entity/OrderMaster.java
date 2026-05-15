package com.sadjier.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/// <summary>订单主表实体类</summary>
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_master")
public class OrderMaster {
    /// <summary>订单编号</summary>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;
    /// <summary>客户编号</summary>
    @Column(name = "customer_id")
    private Long customerId;
    /// <summary>订单总价</summary>
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    /// <summary>创建时间</summary>
    @Column(name = "create_time")
    private LocalDateTime createTime;
    /// <summary>订单状态</summary>
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;
}


