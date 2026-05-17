package com.sadjier.model.vo.category;

import com.sadjier.model.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "商品分类列表VO")
public class CategoryListVO {
    /// <summary>商品分类列表</summary>
    @Schema(description = "商品分类列表")
    private List<CategoryListItemVO> categories;

    public static CategoryListVO create(List<Category> categories) {
        List<CategoryListItemVO> list = new ArrayList<>();
        for (Category category : categories) {
            list.add(new CategoryListItemVO(category));
        }
        return new CategoryListVO(list);
    }
}
