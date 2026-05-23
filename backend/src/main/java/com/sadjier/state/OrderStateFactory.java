package com.sadjier.state;

import com.sadjier.enums.OrderStatusEnum;

import java.util.EnumMap;
import java.util.Map;

/// <summary>订单状态工厂</summary>
/// <remarks>根据枚举获取对应状态实例</remarks>
public class OrderStateFactory {
    /// <summary>枚举到状态实例的映射</summary>
    private static final Map<OrderStatusEnum, OrderState> STATE_MAP = new EnumMap<>(OrderStatusEnum.class);

    static {
        STATE_MAP.put(OrderStatusEnum.UNPAID, new UnpaidState());
        STATE_MAP.put(OrderStatusEnum.PAID, new PaidState());
        STATE_MAP.put(OrderStatusEnum.SHIPPED, new ShippedState());
        STATE_MAP.put(OrderStatusEnum.COMPLETED, new CompletedState());
        STATE_MAP.put(OrderStatusEnum.CANCELLED, new CancelledState());
    }
    /// <summary>根据订单状态枚举获取对应状态实例</summary>
    public static OrderState getState(OrderStatusEnum status) {
        return STATE_MAP.get(status);
    }
}
