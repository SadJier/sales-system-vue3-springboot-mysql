package com.sadjier.controller;

import com.sadjier.model.dto.*;
import com.sadjier.common.Result;
import com.sadjier.model.vo.ProductGetPageVO;
import com.sadjier.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

/// <summary>商品管理控制器</summary>
@RestController
@RequestMapping("/api/products")
@Tag(name = "商品管理")
public class ProductController {
    /// <summary>商品服务</summary>
    @Autowired
    private ProductService product_service;

    /// <summary>分页查询商品</summary>
    @GetMapping
    @Operation(summary = "分页查询商品")
    public Result<ProductGetPageVO> getProductPage(@ModelAttribute ProductGetPageDTO dto) {
        return product_service.getProductPage(dto);
    }
    /// <summary>根据ID获取商品</summary>
    @GetMapping("/{product_id}")
    @Operation(summary = "根据ID获取商品")
    public Result getProductById(@PathVariable("product_id") Long product_id) {
        return product_service.getProductById(product_id);
    }
    /// <summary>新增商品</summary>
    @PostMapping
    @Operation(summary = "新增商品")
    public Result addProduct(@RequestBody ProductCreateDTO product_create) {
        return product_service.addProduct(product_create);
    }
    /// <summary>更新商品</summary>
    @PutMapping("/{product_id}")
    @Operation(summary = "更新商品")
    public Result updateProduct(@PathVariable("product_id") Long product_id,
                                @RequestBody ProductUpdateDTO product_update) {
        return product_service.updateProduct(product_id, product_update);
    }
    /// <summary>删除商品</summary>
    @DeleteMapping("/{product_id}")
    @Operation(summary = "删除商品")
    public Result deleteProduct(@PathVariable("product_id") Long product_id) {
        return product_service.deleteProduct(product_id);
    }
    /// <summary>上传商品图片</summary>
    @PostMapping("/upload/products")
    @Operation(summary = "上传商品图片",description = "若成功,success携带成功信息;若失败,data中携带失败信息")
    public Result<String> uploadProductImage(@Valid ProductUploadImageDTO form_data) {
        return product_service.uploadProductImage(form_data);
    }
    /// <summary>获取商品图片</summary>
    @GetMapping("/image/{product_id}")
    @Operation(summary = "获取商品图片")
    public Resource getProductImage(@PathVariable("product_id") Long product_id) {
        return product_service.getProductImage(product_id);
    }
}

