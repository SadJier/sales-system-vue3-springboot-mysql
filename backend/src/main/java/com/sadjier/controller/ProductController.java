package com.sadjier.controller;

import com.sadjier.annotations.RequireRole;
import com.sadjier.common.Result;
import com.sadjier.enums.UserRolesEnum;
import com.sadjier.model.dto.product.ProductCreateDTO;
import com.sadjier.model.dto.product.ProductGetPageDTO;
import com.sadjier.model.dto.product.ProductUpdateDTO;
import com.sadjier.model.dto.product.ProductUploadImageDTO;
import com.sadjier.model.vo.product.ProductGetPageVO;
import com.sadjier.model.vo.product.ProductStatsVO;
import com.sadjier.model.vo.product.ProductVO;
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
    @RequireRole(all = true)
    public Result<ProductGetPageVO> getProductPage(@Valid @ModelAttribute ProductGetPageDTO dto) {
        return product_service.getProductPage(dto);
    }
    /// <summary>新增商品</summary>
    @PostMapping
    @Operation(summary = "新增商品")
    @RequireRole(roles = {UserRolesEnum.MERCHANT})
    public Result<String> addProduct(@Valid @RequestBody ProductCreateDTO product_create) {
        return product_service.addProduct(product_create);
    }
    /// <summary>上传商品图片</summary>
    @PostMapping("/upload/products")
    @Operation(summary = "上传商品图片")
    @RequireRole(roles = {UserRolesEnum.MERCHANT})
    public Result<String> uploadProductImage(@Valid ProductUploadImageDTO form_data) {
        return product_service.uploadProductImage(form_data);
    }
    /// <summary>获取商品图片</summary>
    @GetMapping("/image/{product_id}")
    @Operation(summary = "获取商品图片")
    public Resource getProductImage(@PathVariable("product_id") Long product_id) {
        return product_service.getProductImage(product_id);
    }
    /// <summary>根据ID获取商品</summary>
    @GetMapping("/get/{product_id}")
    @Operation(summary = "根据ID获取商品详细信息")
    @RequireRole(all = true)
    public Result<ProductVO> getProductById(@PathVariable("product_id") Long product_id) {
        return product_service.getProductById(product_id);
    }
    /// <summary>更新商品</summary>
    @PutMapping("/update")
    @Operation(summary = "更新商品")
    @RequireRole(all = true)
    public Result<String> updateProduct(@Valid @RequestBody ProductUpdateDTO product_update) {
        return product_service.updateProduct(product_update);
    }
    /// <summary>删除商品</summary>
    @DeleteMapping("/delete/{product_id}")
    @Operation(summary = "删除商品")
    @RequireRole(all = true)
    public Result<String> deleteProduct(@PathVariable("product_id") Long product_id) {
        return product_service.deleteProduct(product_id);
    }
    /// <summary>获取商品详情统计</summary>
    @GetMapping("/stats/{product_id}")
    @Operation(summary = "获取商品详情统计")
    @RequireRole(all = true)
    public Result<ProductStatsVO> getProductStats(@PathVariable("product_id") Long product_id) {
        return product_service.getProductStats(product_id);
    }
}
