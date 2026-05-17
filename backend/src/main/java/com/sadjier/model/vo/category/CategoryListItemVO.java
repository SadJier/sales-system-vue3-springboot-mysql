package com.sadjier.model.vo.category;

import com.sadjier.model.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "商品分类列表项VO")
public class CategoryListItemVO {
    /// <summary>商品分类唯一id</summary>
    @Schema(description = "商品分类唯一id")
    private Long categoryId;
    /// <summary>分类名称</summary>
    @Schema(description = "分类名称")
    private String name;

    public CategoryListItemVO(Category category) {
        this.categoryId = category.getCategoryId();
        this.name = category.getName();
    }
}
