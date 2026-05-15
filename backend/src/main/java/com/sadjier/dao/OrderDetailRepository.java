package com.sadjier.dao;

import com.sadjier.model.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/// <summary>订单明细数据访问层接口</summary>
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    /// <summary>根据订单ID查询明细</summary>
    List<OrderDetail> findByOrderId(Long order_id);
    /// <summary>统计商品销量</summary>
    @Query(value = "SELECT d.product_id AS product_id, SUM(d.quantity) AS total_quantity " +
            "FROM order_detail d " +
            "JOIN order_master m ON d.order_id = m.order_id " +
            "WHERE m.create_time BETWEEN :start_time AND :end_time " +
            "GROUP BY d.product_id " +
            "ORDER BY total_quantity DESC", nativeQuery = true)
    List<Object[]> sumProductSales(@Param("start_time") LocalDateTime start_time, @Param("end_time") LocalDateTime end_time);
}

