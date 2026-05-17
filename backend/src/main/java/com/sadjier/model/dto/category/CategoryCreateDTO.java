package com.sadjier.model.dto.category;

import com.sadjier.constant.ResultMsgConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/// <summary>商品分类创建DTO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "商品分类创建DTO")
public class CategoryCreateDTO{
    /// <summary>商品分类名称</summary>
    @NotBlank(message = ResultMsgConstant.CATEGORY_NAME_REQUIRED)
    @Schema(description = "分类名称",requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}

