package com.sadjier.state;

import com.sadjier.enums.OrderStatusEnum;

import java.util.Collections;
import java.util.List;

/// <summary>已完成状态</summary>
/// <remarks>终态，不可转换</remarks>
class CompletedState extends OrderState {
    @Override
    OrderStatusEnum getStatus() {
        return OrderStatusEnum.COMPLETED;
    }
    @Override
    List<OrderStatusEnum> getAllowedTransitions() {
        return Collections.emptyList();
    }
}
