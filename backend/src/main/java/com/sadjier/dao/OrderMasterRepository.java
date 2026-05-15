package com.sadjier.dao;

import com.sadjier.model.entity.OrderMaster;
import com.sadjier.model.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/// <summary>订单主表数据访问层接口</summary>
@Repository
public interface OrderMasterRepository extends JpaRepository<OrderMaster, Long> {
    /// <summary>根据客户ID查询订单</summary>
    List<OrderMaster> findByCustomerId(Long customer_id);
    /// <summary>按状态统计订单数量</summary>
    long countByStatus(OrderStatus status);
    /// <summary>统计时间范围内销售金额</summary>
    @Query(value = "SELECT DATE(create_time) AS stat_date, SUM(total_amount) AS total_amount " +
            "FROM order_master " +
            "WHERE create_time BETWEEN :start_time AND :end_time " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY stat_date", nativeQuery = true)
    List<Object[]> sumTotalAmountByDate(@Param("start_time") LocalDateTime start_time, @Param("end_time") LocalDateTime end_time);
}

