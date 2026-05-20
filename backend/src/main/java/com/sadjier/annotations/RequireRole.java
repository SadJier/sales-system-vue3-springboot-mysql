package com.sadjier.annotations;

import com.sadjier.enums.UserRolesEnum;
import java.lang.annotation.*;

/// <summary>接口权限校验注解</summary>
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    /// <summary>允许调用api的身份</summary>
    UserRolesEnum[] roles() default {};
    /// <summary>是否向所有身份开放</summary>
    boolean all() default false;
}
