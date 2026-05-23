package com.sadjier.state;

import com.sadjier.enums.OrderStatusEnum;

import java.util.List;

/// <summary>订单状态机</summary>
/// <remarks>封装状态实例，对外提供主动式转换和校验方法</remarks>
public class OrderStateMachine {
    /// <summary>当前持有的状态实例</summary>
    private OrderState current_state;

    /// <summary>根据订单状态枚举构造状态机</summary>
    public OrderStateMachine(OrderStatusEnum status) {
        this.current_state = OrderStateFactory.getState(status);
    }
    /// <summary>获取当前状态枚举</summary>
    public OrderStatusEnum getCurrentStatus() {
        return current_state != null ? current_state.getStatus() : null;
    }
    /// <summary>获取当前状态允许转换的目标状态列表</summary>
    public List<OrderStatusEnum> getAllowedTransitions() {
        return current_state != null ? current_state.getAllowedTransitions() : List.of();
    }

    /// <summary>根据目标状态枚举执行转换，成功返回true</summary>
    public boolean transitTo(OrderStatusEnum target) {
        if (current_state == null || !current_state.canTransitTo(target)) {
            return false;
        }
        current_state = OrderStateFactory.getState(target);
        return true;
    }
    /// <summary>判断是否能转换到指定状态</summary>
    public boolean canTransitTo(OrderStatusEnum target) {
        return current_state != null && current_state.canTransitTo(target);
    }
}
