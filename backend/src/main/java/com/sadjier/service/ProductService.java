package com.sadjier.service;

import com.sadjier.model.dto.ProductCreateDTO;
import com.sadjier.model.dto.ProductGetPageDTO;
import com.sadjier.model.dto.ProductUpdateDTO;
import com.sadjier.common.Result;
import com.sadjier.model.dto.ProductUploadImageDTO;
import com.sadjier.model.vo.ProductGetPageVO;
import org.springframework.core.io.Resource;

/// <summary>商品服务接口</summary>
public interface ProductService {
    /// <summary>分页查询商品</summary>
    Result<ProductGetPageVO> getProductPage(ProductGetPageDTO dto);
    /// <summary>根据ID获取商品</summary>
    Result getProductById(Long product_id);
    /// <summary>新增商品</summary>
    Result addProduct(ProductCreateDTO product_create);
    /// <summary>更新商品</summary>
    Result updateProduct(Long product_id, ProductUpdateDTO product_update);
    /// <summary>删除商品</summary>
    Result deleteProduct(Long product_id);
    /// <summary>上传商品图片</summary>
    Result<String> uploadProductImage(ProductUploadImageDTO dto);
    /// <summary>获取商品图片</summary>
    Resource getProductImage(Long productId);
}

