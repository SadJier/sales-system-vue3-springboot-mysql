package com.sadjier.state;

import com.sadjier.enums.OrderStatusEnum;

import java.util.List;

/// <summary>已发货状态</summary>
/// <remarks>可转为已完成</remarks>
class ShippedState extends OrderState {
    @Override
    OrderStatusEnum getStatus() {
        return OrderStatusEnum.SHIPPED;
    }
    @Override
    List<OrderStatusEnum> getAllowedTransitions() {
        return List.of(OrderStatusEnum.COMPLETED);
    }
}
