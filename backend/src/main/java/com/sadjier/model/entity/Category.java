package com.sadjier.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/// <summary>商品分类实体类</summary>
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "category")
public class Category {
    /// <summary>商品分类唯一id</summary>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;
    /// <summary>分类名称</summary>
    @Column(name = "name")
    private String name;
}
