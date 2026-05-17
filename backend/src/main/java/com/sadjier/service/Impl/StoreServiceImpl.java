package com.sadjier.service.Impl;

import com.sadjier.common.Result;
import com.sadjier.constant.ResultMsgConstant;
import com.sadjier.dao.OrdersRepository;
import com.sadjier.dao.ProductRepository;
import com.sadjier.dao.SysUserRepository;
import com.sadjier.enums.OrderStatusEnum;
import com.sadjier.enums.ResultStatusEnum;
import com.sadjier.enums.UserRolesEnum;
import com.sadjier.model.entity.Orders;
import com.sadjier.model.entity.Product;
import com.sadjier.model.entity.SysUser;
import com.sadjier.model.vo.store.ProductSaleVO;
import com.sadjier.model.vo.store.StoreStatsVO;
import com.sadjier.service.StoreService;
import com.sadjier.util.CommonUtil;
import com.sadjier.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// <summary>店铺服务实现</summary>
@Service
@Slf4j
public class StoreServiceImpl implements StoreService {
    /// <summary>订单仓储</summary>
    @Autowired
    private OrdersRepository order_repo;
    /// <summary>用户仓储</summary>
    @Autowired
    private SysUserRepository user_repo;
    /// <summary>商品仓储</summary>
    @Autowired
    private ProductRepository product_repo;

    /// <summary>获取店铺统计</summary>
    public Result<StoreStatsVO> getStoreStats(Long merchant_id) {
        var claims = JwtUtil.parseToken(CommonUtil.getToken());
        /// 当前用户角色
        var role = JwtUtil.getUserRole(claims);
        // 当前用户ID
        var user_id = JwtUtil.getUserId(claims);
        /// 目标商家实体
        SysUser merchant = null;
        //数据检验
        if (merchant_id == null) {
            return Result.result(ResultStatusEnum.DATA_MISSING, ResultMsgConstant.STORE_STATS_ID_REQUIRED);
        }
        if(role == UserRolesEnum.MERCHANT && !Objects.equals(merchant_id, user_id)){
            return Result.result(ResultStatusEnum.DATA_NO_PERMISSION, ResultMsgConstant.STORE_STATS_ONLY_OWN);
        }
        merchant = user_repo.findByUserId(merchant_id);
        if (merchant == null) {
            return Result.result(ResultStatusEnum.NO_DATA, ResultMsgConstant.STORE_STATS_MERCHANT_NOT_FOUND);
        }
        //数据计算
        long total_orders = order_repo.countByMerchant(merchant);
        long completed_orders = order_repo.countByMerchantAndStatus(merchant, OrderStatusEnum.COMPLETED);
        long unpaid_orders = order_repo.countByMerchantAndStatus(merchant, OrderStatusEnum.UNPAID);
        long paid_orders = order_repo.countByMerchantAndStatus(merchant, OrderStatusEnum.PAID);
        long cancelled_orders = order_repo.countByMerchantAndStatus(merchant, OrderStatusEnum.CANCELLED);
        BigDecimal total_revenue = order_repo.sumTotalAmountByMerchantAndStatus(merchant, OrderStatusEnum.COMPLETED);
        List<Object[]> sales_data = order_repo.groupProductSalesByMerchant(merchant);
        List<ProductSaleVO> product_sales = new ArrayList<>();

        for (Object[] row : sales_data) {
            ProductSaleVO sale_vo = new ProductSaleVO();
            Long pid = ((Number) row[0]).longValue();
            sale_vo.setProductId(pid);
            Product product = product_repo.findByProductId(pid);
            sale_vo.setProductName(product != null ? product.getName() : null);
            sale_vo.setQuantity(((Number) row[1]).intValue());
            sale_vo.setRevenue((BigDecimal) row[2]);
            product_sales.add(sale_vo);
        }

        StoreStatsVO vo = new StoreStatsVO();
        vo.setTotalOrders(total_orders);
        vo.setCompletedOrders(completed_orders);
        vo.setUnpaidOrders(unpaid_orders);
        vo.setPaidOrders(paid_orders);
        vo.setCancelledOrders(cancelled_orders);
        vo.setTotalRevenue(total_revenue);
        vo.setProductSales(product_sales);
        return Result.success(vo);
    }
}
