package com.sadjier.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/// <summary>自定义返回数据状态枚举</summary>
@AllArgsConstructor
@Getter
public enum ResultStatusEnum {
    SUCCESS(200,"操作成功"),
    // 400起为参数、数据异常，进行提示
    ERROR(400,"操作出错"),
    /// <summary>后端找不到该数据</summary>
    NO_DATA(401,"数据不存在"),
    DATA_DUPLICATE(402, "数据重复,请勿重复提交"),
    DATA_NO_PERMISSION(403,"无数据操作权限"),
    /// <summary>前端未传入必须数据</summary>
    DATA_MISSING(404,"数据缺失"),
    DATA_INVALID(404,"数据无效"),
    // 500起为系统、服务异常，提示系统错误
    SERVER_BUSY(501,"服务器繁忙,请稍后重试"),
    // 600~700为图片、文件异常，上传相关组件提示
    // 1000起的错误一般进行跳转
    // 1000起为Token异常，一般跳转到登录
    TOKEN_INVALID(1001,"登陆凭证过期或无效,请重新登录"),
    TOKEN_MISSING(1002,"登录凭证缺失"),
    // 1100起表示误操作权限错误,比如无页面跳转权限，需跳转到无权限提示页面
    NO_PERMISSION(1101, "无操作权限");

    @Schema(description = "自定义返回数据状态码")
    final int code;
    @Schema(description = "自定义返回数据状态信息")
    final String msg;

    @Override
    public String toString() {
        return String.format("{name(%s), code(%s), msg(%s)}", name(), code, msg);
    }
}

