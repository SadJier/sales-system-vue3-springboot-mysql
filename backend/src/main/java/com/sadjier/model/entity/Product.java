package com.sadjier.model.entity;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/// <summary>商品信息实体类</summary>
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "product")
public class Product {
    /// <summary>商品唯一标识</summary>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    /// <summary>商家唯一Id</summary>
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SysUser merchant;
    /// <summary>商品名称</summary>
    @Column(name = "name")
    private String name;
    /// <summary>商品分类</summary>
    @ManyToOne
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Category category;
    /// <summary>商品进价</summary>
    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;
    /// <summary>商品售价</summary>
    @Column(name = "sale_price")
    private BigDecimal salePrice;
    /// <summary>库存数量</summary>
    @Column(name = "stock")
    private Integer stock;
    /// <summary>创建时间</summary>
    @Column(name = "create_time")
    private LocalDateTime createTime;
}
