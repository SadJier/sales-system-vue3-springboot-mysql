package com.sadjier.service.Impl;

import com.sadjier.common.Result;
import com.sadjier.constant.ResultMsgConstant;
import com.sadjier.dao.CategoryRepository;
import com.sadjier.enums.ResultStatusEnum;
import com.sadjier.model.dto.category.CategoryCreateDTO;
import com.sadjier.model.dto.category.CategoryUpdateDTO;
import com.sadjier.model.entity.Category;
import com.sadjier.model.vo.category.CategoryListVO;
import com.sadjier.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/// <summary>商品分类业务实现</summary>
@Service
@Slf4j
@Tag(name = "商品分类业务实现")
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository category_repo;

    /// <summary>获取所有商品分类</summary>
    public Result<CategoryListVO> getCategoryList() {
        return Result.success(CategoryListVO.create(category_repo.findAll()));
    }
    /// <summary>新增商品分类</summary>
    public Result<String> addCategory(CategoryCreateDTO category_create) {
        Category category = new Category();
        category.setName(category_create.getName());
        category_repo.save(category);
        return Result.success(ResultMsgConstant.CATEGORY_ADD_SUCCESS);
    }
    /// <summary>更新商品分类</summary>
    public Result<String> updateCategory(CategoryUpdateDTO category_update) {
        Category category = category_repo.findByCategoryId(category_update.getCategoryId());
        if (category == null) return Result.result(ResultStatusEnum.NO_DATA,ResultMsgConstant.CATEGORY_NOT_FOUND);
        category.setName(category_update.getName());
        category_repo.save(category);
        return Result.success(ResultMsgConstant.CATEGORY_UPDATE_SUCCESS);
    }
    /// <summary>删除商品分类</summary>
    public Result<String> deleteCategory(Long category_id) {
        Category category = category_repo.findByCategoryId(category_id);
        if (category == null) return Result.result(ResultStatusEnum.NO_DATA,ResultMsgConstant.CATEGORY_NOT_FOUND);
        category_repo.deleteById(category_id);
        return Result.success(ResultMsgConstant.CATEGORY_DELETE_SUCCESS);
    }
}
