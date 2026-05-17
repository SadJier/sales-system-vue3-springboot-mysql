package com.sadjier.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/// <summary>用户页获取DTO</summary>
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "用户页获取DTO")
public class UserGetPageDTO {
    /// <summary>用户名称</summary>
    @Schema(description = "用户名称,用户进行模糊搜索",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;
    /// <summary>页码</summary>
    @Schema(description = "页码",requiredMode = Schema.RequiredMode.NOT_REQUIRED,defaultValue = "1")
    private Integer pageIndex;
    /// <summary>页大小</summary>
    @Schema(description = "页大小",requiredMode = Schema.RequiredMode.NOT_REQUIRED,defaultValue = "10")
    private Integer pageSize;
}
