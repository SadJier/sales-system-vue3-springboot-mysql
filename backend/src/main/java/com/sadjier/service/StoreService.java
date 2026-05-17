package com.sadjier.service;

import com.sadjier.common.Result;
import com.sadjier.model.vo.store.StoreStatsVO;

/// <summary>店铺服务接口</summary>
public interface StoreService {
    /// <summary>获取店铺统计</summary>
    Result<StoreStatsVO> getStoreStats(Long merchant_id);
}
