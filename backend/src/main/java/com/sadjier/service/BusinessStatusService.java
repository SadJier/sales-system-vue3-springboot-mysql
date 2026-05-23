package com.sadjier.service;

import com.sadjier.common.Result;

/// <summary>业务状态服务接口</summary>
public interface BusinessStatusService {
    /// <summary>根据业务ID查询业务是否完成</summary>
    Result<Boolean> checkBusinessCompleted(String business_id);
}
