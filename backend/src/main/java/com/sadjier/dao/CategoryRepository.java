package com.sadjier.dao;

import com.sadjier.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/// <summary>商品分类数据访问层接口</summary>
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /// <summary>根据商品分类ID获取实体类</summary>
    Category findByCategoryId(long id);
}
