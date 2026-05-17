package com.sadjier.model.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "商品分页信息VO")
public class ProductGetPageVO {
    /// <summary>商品总数</summary>
    @Schema(description = "商品总数")
    private Long total;
    /// <summary>商品项显示内容</summary>
    @Schema(description = "商品项显示内容")
    private List<ProductListItemVO> items;
}
