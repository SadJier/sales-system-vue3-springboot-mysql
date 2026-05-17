package com.sadjier.model.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/// <summary>订单页获取DTO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "订单页获取DTO")
public class OrderGetPageDTO {
    /// <summary>页码</summary>
    @Schema(description = "页码",requiredMode = Schema.RequiredMode.NOT_REQUIRED,defaultValue = "1")
    private Integer pageIndex;
    /// <summary>页大小</summary>
    @Schema(description = "页大小",requiredMode = Schema.RequiredMode.NOT_REQUIRED,defaultValue = "10")
    private Integer pageSize;
}
