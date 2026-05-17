package com.sadjier.model.dto.order;

import com.sadjier.constant.ResultMsgConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/// <summary>订单创建DTO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "订单创建DTO")
public class OrderCreateDTO {
    /// <summary>买家姓名</summary>
    @NotBlank(message = ResultMsgConstant.ORDER_BUYER_NAME_REQUIRED)
    @Schema(description = "买家姓名",requiredMode = Schema.RequiredMode.REQUIRED)
    private String buyerName;
    /// <summary>买家电话</summary>
    @NotBlank(message = ResultMsgConstant.ORDER_BUYER_PHONE_REQUIRED)
    @Schema(description = "买家电话",requiredMode = Schema.RequiredMode.REQUIRED)
    private String buyerPhone;
    /// <summary>商品ID</summary>
    @NotNull(message = ResultMsgConstant.ORDER_PRODUCT_ID_REQUIRED)
    @Schema(description = "商品ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;
    /// <summary>购买数量</summary>
    @NotNull(message = ResultMsgConstant.ORDER_QUANTITY_REQUIRED)
    @Schema(description = "购买数量",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
    /// <summary>备注</summary>
    @Schema(description = "备注",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;
}
