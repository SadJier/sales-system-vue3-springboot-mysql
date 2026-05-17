package com.sadjier.model.entity;

import com.sadjier.enums.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/// <summary>订单实体类</summary>
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Orders {
    /// <summary>订单编号</summary>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;
    /// <summary>商家编号</summary>
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SysUser merchant;
    /// <summary>买家姓名</summary>
    @Column(name = "buyer_name")
    private String buyerName;
    /// <summary>买家电话</summary>
    @Column(name = "buyer_phone")
    private String buyerPhone;
    /// <summary>商品编号</summary>
    @ManyToOne
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;
    /// <summary>购买数量</summary>
    @Column(name = "quantity")
    private Integer quantity;
    /// <summary>订单总价</summary>
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    /// <summary>订单状态</summary>
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatusEnum status;
    /// <summary>订单备注</summary>
    @Column(name = "remark")
    private String remark;
    /// <summary>创建时间</summary>
    @Column(name = "create_time")
    private LocalDateTime createTime;
}
