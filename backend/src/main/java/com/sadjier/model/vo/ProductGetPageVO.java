package com.sadjier.model.vo;

import com.sadjier.model.entity.Product;
import com.sadjier.model.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "商品页信息VO")
public class ProductGetPageVO {
    /// <summary>商品总数</summary>
    @Schema(description = "商品总数")
    private Long total;
    /// <summary>具体商品内容</summary>
    @Schema(description = "具体商品内容")
    private List<ProductListItemVO> items;
}
