package com.sadjier.service.Impl;

import com.sadjier.common.Result;
import com.sadjier.constant.CommonMsgConstant;
import com.sadjier.service.BusinessStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/// <summary>业务状态服务实现</summary>
@Service
public class BusinessStatusServiceImpl implements BusinessStatusService {
    /// <summary>Redis</summary>
    @Autowired
    private RedisTemplate<Object, Object> redis_template;

    /// <summary>根据业务ID查询业务是否完成</summary>
    public Result<Boolean> checkBusinessCompleted(String business_id) {
        String key = CommonMsgConstant.PRE_BUSINESS_COMPLETED + business_id;
        Object value = redis_template.opsForValue().get(key);
        return Result.success(value != null);
    }
}
