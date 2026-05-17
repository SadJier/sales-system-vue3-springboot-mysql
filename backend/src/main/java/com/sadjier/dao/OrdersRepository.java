package com.sadjier.dao;

import com.sadjier.model.entity.Orders;
import com.sadjier.enums.OrderStatusEnum;
import com.sadjier.model.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/// <summary>订单数据访问层接口</summary>
@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {
    /// <summary>根据订单ID查询订单</summary>
    Orders findByOrderId(Long orderId);
    /// <summary>按状态统计订单数量</summary>
    long countByStatus(OrderStatusEnum status);
    /// <summary>按商家ID和状态统计订单数量</summary>
    long countByMerchantAndStatus(SysUser merchant, OrderStatusEnum status);
    /// <summary>按商家ID统计订单数量</summary>
    long countByMerchant(SysUser merchant);
    /// <summary>统计时间范围内销售金额</summary>
    @Query(value = "SELECT DATE(create_time) AS stat_date, SUM(total_amount) AS total_amount " +
            "FROM orders " +
            "WHERE create_time BETWEEN :start_time AND :end_time " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY stat_date", nativeQuery = true)
    List<Object[]> sumTotalAmountByDate(@Param("start_time") LocalDateTime start_time, @Param("end_time") LocalDateTime end_time);
    /// <summary>按商家统计已完成订单的总收入</summary>
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Orders o WHERE o.merchant = :merchant AND o.status = :status")
    BigDecimal sumTotalAmountByMerchantAndStatus(@Param("merchant") SysUser merchant, @Param("status") OrderStatusEnum status);
    /// <summary>按商家分组统计各商品的销售数量和销售额</summary>
    @Query("SELECT o.product.productId, SUM(o.quantity), SUM(o.totalAmount) FROM Orders o WHERE o.merchant = :merchant GROUP BY o.product.productId")
    List<Object[]> groupProductSalesByMerchant(@Param("merchant") SysUser merchant);
    /// <summary>按商品统计总销售数量和总销售额</summary>
    @Query("SELECT COALESCE(SUM(o.quantity), 0), COALESCE(SUM(o.totalAmount), 0) FROM Orders o WHERE o.product.productId = :productId")
    List<Object[]> sumSalesByProductId(@Param("productId") Long productId);
    /// <summary>按商品查询最近的订单记录</summary>
    List<Orders> findTop10ByProductProductIdOrderByCreateTimeDesc(Long productId);
}
