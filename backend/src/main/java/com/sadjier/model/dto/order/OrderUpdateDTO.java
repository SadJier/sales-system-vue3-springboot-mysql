package com.sadjier.model.dto.order;

import com.sadjier.constant.ResultMsgConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/// <summary>订单更新DTO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "订单更新DTO")
public class OrderUpdateDTO {
    /// <summary>订单ID</summary>
    @NotNull(message = ResultMsgConstant.ORDER_ID_REQUIRED)
    @Schema(description = "订单ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;
    /// <summary>订单状态</summary>
    @Schema(description = "订单状态(枚举本身字符串)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String status;
    /// <summary>备注</summary>
    @Schema(description = "备注",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;
}
