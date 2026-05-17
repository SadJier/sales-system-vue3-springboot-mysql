package com.sadjier.model.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/// <summary>订单分页信息VO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "订单分页信息VO")
public class OrderGetPageVO {
    /// <summary>订单总数</summary>
    @Schema(description = "订单总数")
    private Long total;
    /// <summary>订单项显示内容</summary>
    @Schema(description = "订单项显示内容")
    private List<OrderListItemVO> items;
}
