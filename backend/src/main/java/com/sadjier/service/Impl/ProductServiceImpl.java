package com.sadjier.service.Impl;

import com.sadjier.dao.ProductRepository;
import com.sadjier.dao.SysUserRepository;
import com.sadjier.enums.UserRole;
import com.sadjier.model.dto.ProductCreateDTO;
import com.sadjier.model.dto.ProductGetPageDTO;
import com.sadjier.model.dto.ProductUpdateDTO;
import com.sadjier.model.dto.ProductUploadImageDTO;
import com.sadjier.model.entity.Product;
import com.sadjier.common.Result;
import com.sadjier.model.entity.SysUser;
import com.sadjier.model.vo.ProductGetPageVO;
import com.sadjier.model.vo.ProductListItemVO;
import com.sadjier.service.ProductService;
import com.sadjier.util.CommonUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

/// <summary>商品服务实现</summary>
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    /// <summary>商品仓储</summary>
    @Autowired
    private ProductRepository product_repo;
    @Autowired
    private SysUserRepository sys_user_repo;

    /// <summary>分页查询商品</summary>
    public Result<ProductGetPageVO> getProductPage(ProductGetPageDTO dto) {
        log.info("分页查询商品");
        //页码默认值
        int page_index = dto.getPageIndex() <= 0 ? 1 : dto.getPageIndex();
        //每页数量默认值
        int page_size = dto.getPageIndex() <= 0 ? 10 : dto.getPageIndex();
        //分页对象
        PageRequest page_request = PageRequest.of(page_index, page_size);
        //查询条件构造器
        Specification<Product> product_specification = (root, query, criteria_builder) -> {
            //条件集合
            List<Predicate> predicate_list = new ArrayList<>();
            if (StringUtils.hasText(dto.getProductName())) {
                //名称条件
                Predicate name_predicate = criteria_builder.like(root.get("Name"), "%" + dto.getProductName() + "%");
                predicate_list.add(name_predicate);
            }
            if (StringUtils.hasText(dto.getCategory())) {
                //分类条件
                Predicate category_predicate = criteria_builder.like(root.get("Category"), "%" + dto.getCategory() + "%");
                predicate_list.add(category_predicate);
            }
            return criteria_builder.and(predicate_list.toArray(new Predicate[0]));
        };
        //分页结果
        Page<Product> product_page = product_repo.findAll(product_specification, page_request);
        //返回数据
        ProductGetPageVO vo = new ProductGetPageVO();
        vo.setTotal(product_page.getTotalElements());
        vo.setItems(product_page.getContent().stream().map(ProductListItemVO::new).toList());
        return Result.success(vo);
    }
    /// <summary>根据ID获取商品</summary>
    public Result getProductById(Long product_id) {
        /// <summary>商品信息</summary>
        Product product = product_repo.findById(product_id).orElse(null);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }
    /// <summary>新增商品</summary>
    public Result addProduct(ProductCreateDTO product_create) {
        /// <summary>商品对象</summary>
        Product product = new Product();
        product.setName(product_create.getName());
        product.setCategory(product_create.getCategory());
        product.setPurchasePrice(product_create.getPurchasePrice());
        product.setSalePrice(product_create.getSalePrice());
        product.setStock(product_create.getStock());
        product.setCreateTime(LocalDateTime.now());
        product_repo.save(product);
        return Result.success("新增商品成功");
    }
    /// <summary>更新商品</summary>
    public Result updateProduct(Long product_id, ProductUpdateDTO product_update) {
        /// <summary>商品对象</summary>
        Product product = product_repo.findById(product_id).orElse(null);
        if (product == null) {
            return Result.error("商品不存在");
        }
        if (StringUtils.hasText(product_update.getName())) {
            product.setName(product_update.getName());
        }
        if (StringUtils.hasText(product_update.getCategory())) {
            product.setCategory(product_update.getCategory());
        }
        if (product_update.getPurchasePrice() != null) {
            product.setPurchasePrice(product_update.getPurchasePrice());
        }
        if (product_update.getSalePrice() != null) {
            product.setSalePrice(product_update.getSalePrice());
        }
        if (product_update.getStock() != null) {
            product.setStock(product_update.getStock());
        }
        product_repo.save(product);
        return Result.success("更新商品成功");
    }
    /// <summary>删除商品</summary>
    public Result deleteProduct(Long product_id) {
        /// <summary>商品对象</summary>
        Product product = product_repo.findById(product_id).orElse(null);
        if (product == null) {
            return Result.error("商品不存在");
        }
        product_repo.deleteById(product_id);
        return Result.success("删除商品成功");
    }
    /// <summary>上传商品图片</summary>
    public Result<String> uploadProductImage(ProductUploadImageDTO dto){
        String absolute_path = CommonUtil.getProductImageFolderPath();
        log.info("用户ID({})上传头像", dto.getMerchantId());
        File folder = new File(absolute_path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        //检验是否为商家、商品是否为该商家的
        SysUser user = sys_user_repo.findByUserId(dto.getMerchantId());
        if(user==null || user.getRole() != UserRole.MERCHANT){
            Result.error("只有商家才能上传商品图片");
        }
        Product product = product_repo.findByProductId(dto.getProductId());
        if(product==null || !Objects.equals(product.getMerchant().getUserId(), dto.getMerchantId())){
            Result.error("只能上传自己商品的图片");
        }

        String file_name = dto.getProductId() + ".jpg";
        File dest_file = new File(absolute_path + "/" + file_name);
        try{
            dto.getFile().transferTo(dest_file);
        }catch (Exception e){
            log.error("商品图片上传失败:{}",e.getMessage());
            return Result.error("商品图片上传失败");
        }
        return Result.success("商品图片上传成功");
    }
    /// <summary>获取商品图片</summary>
    public Resource getProductImage(Long productId) {
        String absolute_path = CommonUtil.getProductImageFolderPath();
        String file_name = productId + ".jpg";
        File avatar_file = new File(absolute_path + "/" + file_name);
        if (!avatar_file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到商品图片");
        }
        return new FileSystemResource(avatar_file);
    }
}

