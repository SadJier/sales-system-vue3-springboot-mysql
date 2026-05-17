package com.sadjier.model.vo.user;

import com.sadjier.enums.UserRolesEnum;
import com.sadjier.model.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "用户信息VO")
public class UserVO {
    /// <summary>身份</summary>
    @Schema(description = "身份：admin/user")
    private UserRolesEnum role;
    /// <summary>用户名</summary>
    @Schema(description = "用户名")
    private String username;

    public UserVO(SysUser user){
        this.role = user.getRole();
        this.username = user.getUserName();
    }
}