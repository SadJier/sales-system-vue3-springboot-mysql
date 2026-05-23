package com.sadjier.dao;

import com.sadjier.model.entity.StoreStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/// <summary>店铺统计数据访问层接口</summary>
@Repository
public interface StoreStatsRepository extends JpaRepository<StoreStats, Long> {
    /// <summary>根据商家ID查询店铺统计</summary>
    StoreStats findByMerchantId(Long merchantId);
}
