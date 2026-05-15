package com.sadjier.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/// <summary>客户信息实体类</summary>
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class Customer {
    /// <summary>客户唯一标识</summary>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;
    /// <summary>客户姓名</summary>
    @Column(name = "name")
    private String name;
    /// <summary>联系电话</summary>
    @Column(name = "phone")
    private String phone;
    /// <summary>联系地址</summary>
    @Column(name = "address")
    private String address;
    /// <summary>创建时间</summary>
    @Column(name = "create_time")
    private LocalDateTime createTime;
}

