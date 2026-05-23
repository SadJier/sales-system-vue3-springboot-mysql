package com.sadjier.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/// <summary>上传的图片的类型</summary>
@Schema(description = "上传的图片的类型")
public enum UploadImageType {
    /// <summary>商品图片</summary>
    @Schema(description = "商品图片")
    PRODUCT,
    /// <summary>用户头像</summary>
    @Schema(description = "用户头像")
    AVATAR,
}
