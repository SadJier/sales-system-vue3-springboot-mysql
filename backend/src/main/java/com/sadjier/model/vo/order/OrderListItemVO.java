package com.sadjier.model.vo.order;

import com.sadjier.model.entity.Orders;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/// <summary>订单列表项显示信息VO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "订单列表项显示信息VO")
public class OrderListItemVO {
    /// <summary>订单唯一标识</summary>
    @Schema(description = "订单唯一标识")
    private Long orderId;
    /// <summary>商家ID</summary>
    @Schema(description = "商家名称")
    private String merchantName;
    /// <summary>买家姓名</summary>
    @Schema(description = "买家姓名")
    private String buyerName;
    /// <summary>买家电话</summary>
    @Schema(description = "买家电话")
    private String buyerPhone;
    /// <summary>商品名称</summary>
    @Schema(description = "商品名称")
    private String productName;
    /// <summary>购买数量</summary>
    @Schema(description = "购买数量")
    private Integer quantity;
    /// <summary>订单总价</summary>
    @Schema(description = "订单总价")
    private BigDecimal totalPrice;
    /// <summary>订单状态</summary>
    @Schema(description = "订单状态(枚举本身字符串)")
    private String status;
    /// <summary>订单备注</summary>
    @Schema(description = "订单备注")
    private String remark;
    /// <summary>创建时间</summary>
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public OrderListItemVO(Orders order) {
        this.orderId = order.getOrderId();
        this.merchantName = order.getMerchant().getUserName();
        this.buyerName = order.getBuyerName();
        this.buyerPhone = order.getBuyerPhone();
        this.productName = order.getProduct() != null ? order.getProduct().getName() : null;
        this.quantity = order.getQuantity();
        this.totalPrice = order.getTotalAmount();
        this.status = order.getStatus() != null ? order.getStatus().name() : null;
        this.remark = order.getRemark();
        this.createTime = order.getCreateTime();
    }
}
