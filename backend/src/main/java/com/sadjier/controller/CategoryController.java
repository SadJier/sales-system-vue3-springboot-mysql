package com.sadjier.controller;

import com.sadjier.annotations.RequireRole;
import com.sadjier.common.Result;
import com.sadjier.enums.UserRolesEnum;
import com.sadjier.model.dto.category.CategoryCreateDTO;
import com.sadjier.model.dto.category.CategoryUpdateDTO;
import com.sadjier.model.vo.category.CategoryListVO;
import com.sadjier.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/// <summary>商品分类管理控制器</summary>
@RestController
@Slf4j
@RequestMapping("/api/categories")
@Tag(name = "商品分类管理")
public class CategoryController {
    /// <summary>商品分类管理</summary>
    @Autowired
    private CategoryService category_service;

    /// <summary>获取所有商品分类</summary>
    @GetMapping("/list")
    @Operation(summary = "获取所有商品分类")
    @RequireRole(all = true)
    public Result<CategoryListVO> getCategoryList() {
        return category_service.getCategoryList();
    }

    /// <summary>新增商品分类</summary>
    @PostMapping("/add")
    @Operation(summary = "新增商品分类")
    @RequireRole(roles = {UserRolesEnum.ADMIN})
    public Result<String> addCategory(@Valid @RequestBody CategoryCreateDTO category_create) {
        return category_service.addCategory(category_create);
    }

    /// <summary>更新商品分类</summary>
    @PutMapping("/update")
    @Operation(summary = "更新商品分类")
    @RequireRole(roles = {UserRolesEnum.ADMIN})
    public Result<String> updateCategory(@Valid @RequestBody CategoryUpdateDTO category_update) {
        return category_service.updateCategory(category_update);
    }

    /// <summary>删除商品分类</summary>
    @DeleteMapping("/delete/{category_id}")
    @Operation(summary = "删除商品分类")
    @RequireRole(roles = {UserRolesEnum.ADMIN})
    public Result<String> deleteCategory(@PathVariable("category_id") Long category_id) {
        return category_service.deleteCategory(category_id);
    }
}
