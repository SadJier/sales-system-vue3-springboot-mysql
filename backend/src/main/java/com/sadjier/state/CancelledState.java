package com.sadjier.state;

import com.sadjier.enums.OrderStatusEnum;

import java.util.Collections;
import java.util.List;

/// <summary>已取消状态</summary>
/// <remarks>终态，不可转换</remarks>
class CancelledState extends OrderState {
    @Override
    OrderStatusEnum getStatus() {
        return OrderStatusEnum.CANCELLED;
    }
    @Override
    List<OrderStatusEnum> getAllowedTransitions() {
        return Collections.emptyList();
    }
}
