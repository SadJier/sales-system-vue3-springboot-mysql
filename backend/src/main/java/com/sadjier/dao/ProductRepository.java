package com.sadjier.dao;

import com.sadjier.model.entity.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// <summary>商品数据访问层接口</summary>
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    /// <summary>根据商品ID查询商品</summary>
    Product findByProductId(Long id);
    /// <summary>根据名称模糊查询商品</summary>
    java.util.List<Product> findByNameContaining(String name);
}

