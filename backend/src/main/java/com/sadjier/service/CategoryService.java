package com.sadjier.service;

import com.sadjier.common.Result;
import com.sadjier.model.dto.category.CategoryCreateDTO;
import com.sadjier.model.dto.category.CategoryUpdateDTO;
import com.sadjier.model.vo.category.CategoryListVO;

/// <summary>商品分类服务接口</summary>
public interface CategoryService {
    /// <summary>获取商品</summary>
    Result<CategoryListVO> getCategoryList();
    /// <summary>新增商品分类</summary>
    Result<String> addCategory(CategoryCreateDTO category_create);
    /// <summary>更新商品分类</summary>
    Result<String> updateCategory(CategoryUpdateDTO category_update);
    /// <summary>删除商品分类</summary>
    Result<String> deleteCategory(Long category_id);
}
