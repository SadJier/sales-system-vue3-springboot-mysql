package com.sadjier.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "用户分页信息VO")
public class UserGetPageVO {
    /// <summary>用户总数</summary>
    @Schema(description = "用户总数")
    private Long total;
    /// <summary>用户项显示内容</summary>
    @Schema(description = "用户项显示内内容")
    private List<UserListItemVO> items;
}
