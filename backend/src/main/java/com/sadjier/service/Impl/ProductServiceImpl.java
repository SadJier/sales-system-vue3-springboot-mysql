package com.sadjier.service.Impl;

import com.sadjier.dao.CategoryRepository;
import com.sadjier.dao.OrdersRepository;
import com.sadjier.dao.ProductRepository;
import com.sadjier.dao.SysUserRepository;
import com.sadjier.enums.ResultStatusEnum;
import com.sadjier.enums.UploadImageType;
import com.sadjier.enums.UserRolesEnum;
import com.sadjier.model.dto.product.ProductCreateDTO;
import com.sadjier.model.dto.product.ProductGetPageDTO;
import com.sadjier.model.dto.product.ProductUpdateDTO;
import com.sadjier.model.dto.product.ProductUploadImageDTO;
import com.sadjier.model.entity.Orders;
import com.sadjier.model.entity.Product;
import com.sadjier.common.Result;
import com.sadjier.constant.ResultMsgConstant;
import com.sadjier.model.vo.product.ProductGetPageVO;
import com.sadjier.model.vo.product.ProductListItemVO;
import com.sadjier.model.vo.product.ProductStatsVO;
import com.sadjier.model.vo.product.ProductVO;
import com.sadjier.model.vo.product.SaleRecordVO;
import com.sadjier.mq.model.ImageUploadMessage;
import com.sadjier.mq.producer.BusinessMessageProducer;
import com.sadjier.service.ProductService;
import com.sadjier.util.CommonUtil;
import com.sadjier.util.JwtUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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
    @Autowired
    private CategoryRepository category_repo;
    /// <summary>订单仓储</summary>
    @Autowired
    private OrdersRepository order_repo;
    /// <summary>业务消息生产者</summary>
    @Autowired
    private BusinessMessageProducer business_message_producer;

    /// <summary>分页查询商品</summary>
    public Result<ProductGetPageVO> getProductPage(ProductGetPageDTO dto) {
//        log.info("分页查询商品");
        //页码默认值
        int page_index = dto.getPageIndex() <= 0 ? 0 : dto.getPageIndex()-1;
        //每页数量默认值
        int page_size = dto.getPageSize() <= 0 ? 10 : dto.getPageSize();
        //分页对象
        PageRequest page_request = PageRequest.of(page_index, page_size);
        //查询条件构造器
        Specification<Product> product_specification = (root, query, criteria_builder) -> {
            //条件集合
            List<Predicate> predicate_list = new ArrayList<>();
            if (StringUtils.hasText(dto.getProductName())) {
                //名称条件
                Predicate name_predicate = criteria_builder.like(root.get("name"), "%" + dto.getProductName() + "%");
                predicate_list.add(name_predicate);
            }
            if (dto.getCategoryId()!=null) {
                //分类条件
                Predicate category_predicate = criteria_builder.equal(root.get("category").get("categoryId"), dto.getCategoryId());
                predicate_list.add(category_predicate);
            }
            String token = CommonUtil.getToken();
            if(StringUtils.hasText(token)){
                var claim = JwtUtil.parseToken(token);
                if(claim != null){
                    var user_id = JwtUtil.getUserId(claim);
                    var role = JwtUtil.getUserRole(claim);
                    if(user_id != null && role == UserRolesEnum.MERCHANT){
                        //仅查询自己的商品
                        Predicate merchant_predicate = criteria_builder.equal(root.get("merchant").get("userId"), user_id);
                        predicate_list.add(merchant_predicate);
                    }
                }
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
    /// <summary>新增商品</summary>
    public Result<String> addProduct(ProductCreateDTO product_create) {
        var claims = JwtUtil.parseToken(CommonUtil.getToken());
        Product product = new Product();
        product.setName(product_create.getName());
        var category = category_repo.findByCategoryId(product_create.getCategoryId());
        if(category == null) return Result.result(ResultStatusEnum.DATA_INVALID,ResultMsgConstant.PRODUCT_CATEGORY_INVALID);
        product.setCategory(category);
        product.setPurchasePrice(product_create.getPurchasePrice());
        product.setSalePrice(product_create.getSalePrice());
        product.setStock(product_create.getStock());
        product.setMerchant(sys_user_repo.findByUserId(JwtUtil.getUserId(claims)));
        product.setCreateTime(LocalDateTime.now());
        product_repo.save(product);
        return Result.success(ResultMsgConstant.PRODUCT_ADD_SUCCESS);
    }
    /// <summary>上传商品图片</summary>
    public Result<String> uploadProductImage(ProductUploadImageDTO dto){
        var claims = JwtUtil.parseToken(CommonUtil.getToken());
        Product product = product_repo.findByProductId(dto.getProductId());
        if(product == null)
            return Result.result(ResultStatusEnum.NO_DATA,ResultMsgConstant.PRODUCT_NOT_FOUND);
        if(!Objects.equals(product.getMerchant().getUserId(), JwtUtil.getUserId(claims)))
            return Result.result(ResultStatusEnum.DATA_NO_PERMISSION,ResultMsgConstant.PRODUCT_IMAGE_UPLOAD_ONLY_OWN);
        //校验通过后封装消息推入MQ
        String business_id = java.util.UUID.randomUUID().toString();
        ImageUploadMessage upload_message = new ImageUploadMessage();
        upload_message.setBusinessId(business_id);
        upload_message.setImageType(UploadImageType.PRODUCT);
        upload_message.setTargetId(dto.getProductId());
        try {
            upload_message.setFileData(dto.getFile().getBytes());
        } catch (Exception e) {
            return Result.result(ResultStatusEnum.ERROR, ResultMsgConstant.PRODUCT_IMAGE_UPLOAD_FAILED);
        }
        business_message_producer.sendImageUploadMessage(upload_message);
        return Result.success(business_id, ResultMsgConstant.PRODUCT_IMAGE_UPLOADING);
    }
    /// <summary>获取商品图片</summary>
    public Resource getProductImage(Long productId) {
        return CommonUtil.getProductImage(productId);
    }
    /// <summary>根据ID获取商品</summary>
    public Result<ProductVO> getProductById(Long product_id) {
        //商品存在性检测
        Product product = product_repo.findByProductId(product_id);
        if (product == null) {
            return Result.result(ResultStatusEnum.NO_DATA,ResultMsgConstant.PRODUCT_NOT_FOUND);
        }
        var claim = JwtUtil.parseToken(CommonUtil.getToken());
        var user_id = JwtUtil.getUserId(claim);
        var role = JwtUtil.getUserRole(claim);
        //商家只能查询自己的商品
        if (role == UserRolesEnum.MERCHANT && !Objects.equals(product.getMerchant().getUserId(), user_id)) {
            return Result.result(ResultStatusEnum.DATA_NO_PERMISSION,ResultMsgConstant.PRODUCT_GET_ONLY_OWN);
        }
        return Result.success(new ProductVO(product));
    }
    /// <summary>更新商品</summary>
    public Result<String> updateProduct(ProductUpdateDTO product_update) {
        Product product = product_repo.findByProductId(product_update.getProductId());
        if (product == null) return Result.result(ResultStatusEnum.NO_DATA,ResultMsgConstant.PRODUCT_NOT_FOUND);
        var claims = JwtUtil.parseToken(CommonUtil.getToken());
        var user_id = JwtUtil.getUserId(claims);
        if(!Objects.equals(product.getMerchant().getUserId(), user_id)) return Result.result(ResultStatusEnum.DATA_NO_PERMISSION,ResultMsgConstant.PRODUCT_UPDATE_ONLY_OWN);
        if (StringUtils.hasText(product_update.getName())) {
            product.setName(product_update.getName());
        }
        if (product_update.getCategoryId()!=null) {
            var category = category_repo.findByCategoryId(product_update.getCategoryId());
            if(category!=null){
                product.setCategory(category);
            }else{
                return Result.result(ResultStatusEnum.DATA_INVALID,ResultMsgConstant.PRODUCT_CATEGORY_INVALID);
            }
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
        return Result.success(ResultMsgConstant.PRODUCT_UPDATE_SUCCESS);
    }
    /// <summary>删除商品</summary>
    public Result<String> deleteProduct(Long product_id) {
        Product product = product_repo.findByProductId(product_id);
        if (product == null) return Result.result(ResultStatusEnum.NO_DATA,ResultMsgConstant.PRODUCT_NOT_FOUND);
        var claims = JwtUtil.parseToken(CommonUtil.getToken());
        var user_id = JwtUtil.getUserId(claims);
        if(!Objects.equals(product.getMerchant().getUserId(), user_id)) return Result.result(ResultStatusEnum.DATA_NO_PERMISSION,ResultMsgConstant.PRODUCT_DELETE_ONLY_OWN);
        CommonUtil.deleteProductImage(product_id);
        product_repo.deleteById(product_id);
        return Result.success(ResultMsgConstant.PRODUCT_DELETE_SUCCESS);
    }
    /// <summary>获取商品详情统计</summary>
    public Result<ProductStatsVO> getProductStats(Long product_id) {
        Product product = product_repo.findByProductId(product_id);
        if (product == null) return Result.result(ResultStatusEnum.NO_DATA, ResultMsgConstant.PRODUCT_NOT_FOUND);
        var claim = JwtUtil.parseToken(CommonUtil.getToken());
        var user_id = JwtUtil.getUserId(claim);
        var role = JwtUtil.getUserRole(claim);
        if (role == UserRolesEnum.MERCHANT && !Objects.equals(product.getMerchant().getUserId(), user_id)) {
            return Result.result(ResultStatusEnum.DATA_NO_PERMISSION, ResultMsgConstant.PRODUCT_GET_ONLY_OWN);
        }

        ProductStatsVO vo = new ProductStatsVO();
        vo.setProductId(product.getProductId());
        vo.setName(product.getName());
        vo.setProductId(product.getCategory() != null ? product.getCategory().getCategoryId() : null);
        vo.setPurchasePrice(product.getPurchasePrice());
        vo.setSalePrice(product.getSalePrice());
        vo.setStock(product.getStock());
        vo.setCreateTime(product.getCreateTime());

        // 商品销售汇总数据
        Object[] sales_data = order_repo.sumSalesByProductId(product_id).get(0);
        if (sales_data != null && sales_data.length >= 2) {
            vo.setTotalSales(((Number) sales_data[0]).intValue());
            vo.setTotalRevenue((BigDecimal) sales_data[1]);
        } else {
            vo.setTotalSales(0);
            vo.setTotalRevenue(BigDecimal.ZERO);
        }

        // 近期销售记录
        List<Orders> recent_orders = order_repo.findTop10ByProductProductIdOrderByCreateTimeDesc(product_id);
//        log.info("查询商品ID({})的近期销售记录，记录数: {}", product_id, recent_orders.size());
        List<SaleRecordVO> recent_sales = recent_orders.stream().map(order -> {
            SaleRecordVO record = new SaleRecordVO();
            record.setOrderId(order.getOrderId());
            record.setQuantity(order.getQuantity());
            record.setCreateTime(order.getCreateTime());
            return record;
        }).toList();
        vo.setRecentSales(recent_sales);

        return Result.success(vo);
    }
}

