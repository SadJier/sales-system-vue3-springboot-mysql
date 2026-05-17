package com.sadjier.model.vo.user;

import com.sadjier.enums.UserRolesEnum;
import com.sadjier.model.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "列表用户项显示信息VO")
public class UserListItemVO {
    /// <summary>用户唯一标识</summary>
    @Schema(description = "用户唯一标识")
    private Long userId;
    /// <summary>用户名</summary>
    @Schema(description = "用户名")
    private String userName;
    /// <summary>用户角色</summary>
    @Schema(description = "用户角色")
    private UserRolesEnum role;
    /// <summary>创建时间</summary>
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    /// <summary>最后登录时间</summary>
    @Schema(description = "最后登录时间")
    private LocalDateTime loginTime;

    public UserListItemVO(SysUser user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.role = user.getRole();
        this.createTime = user.getCreateTime();
        this.loginTime = user.getLoginTime();
    }
}
