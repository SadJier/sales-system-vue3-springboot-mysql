package com.sadjier.model.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/// <summary>销售记录项VO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "销售记录项VO")
public class SaleRecordVO {
    /// <summary>订单ID</summary>
    @Schema(description = "订单ID")
    private Long orderId;
    /// <summary>数量</summary>
    @Schema(description = "数量")
    private Integer quantity;
    /// <summary>创建时间</summary>
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
