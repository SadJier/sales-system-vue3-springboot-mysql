package com.sadjier.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/// <summary>订单明细实体类</summary>
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_detail")
public class OrderDetail {
    /// <summary>明细唯一标识</summary>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long detailId;
    /// <summary>订单编号</summary>
    @Column(name = "order_id")
    private Long orderId;
    /// <summary>商品编号</summary>
    @Column(name = "product_id")
    private Long productId;
    /// <summary>商品数量</summary>
    @Column(name = "quantity")
    private Integer quantity;
    /// <summary>单价</summary>
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
}

