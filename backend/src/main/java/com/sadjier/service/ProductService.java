package com.sadjier.service;

import com.sadjier.model.dto.product.ProductCreateDTO;
import com.sadjier.model.dto.product.ProductGetPageDTO;
import com.sadjier.model.dto.product.ProductUpdateDTO;
import com.sadjier.common.Result;
import com.sadjier.model.dto.product.ProductUploadImageDTO;
import com.sadjier.model.vo.product.ProductGetPageVO;
import com.sadjier.model.vo.product.ProductStatsVO;
import com.sadjier.model.vo.product.ProductVO;
import org.springframework.core.io.Resource;

/// <summary>商品服务接口</summary>
public interface ProductService {
    /// <summary>分页查询商品</summary>
    Result<ProductGetPageVO> getProductPage(ProductGetPageDTO dto);
    /// <summary>新增商品</summary>
    Result<String> addProduct(ProductCreateDTO product_create);
    /// <summary>上传商品图片</summary>
    Result<String> uploadProductImage(ProductUploadImageDTO dto);
    /// <summary>获取商品图片</summary>
    Resource getProductImage(Long productId);
    /// <summary>根据ID获取商品</summary>
    Result<ProductVO> getProductById(Long product_id);
    /// <summary>更新商品</summary>
    Result<String> updateProduct(ProductUpdateDTO product_update);
    /// <summary>删除商品</summary>
    Result<String> deleteProduct(Long product_id);
    /// <summary>获取商品详情统计</summary>
    Result<ProductStatsVO> getProductStats(Long product_id);
}

