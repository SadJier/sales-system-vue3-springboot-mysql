package com.sadjier.constant;

/// <summary>请求返回的提示的字符串常量定义</summary>
public final class ResultMsgConstant{
    public static final String SERVER_BUSY = "服务器繁忙，请稍后再试";
    public static final String TOKEN_LOCAL_INVALID = "登录凭证无效，无法进行操作";

    /// <summary>找不到</summary>
    public static final String USER_NOT_FOUND = "账号不存在";
    /// <summary>无效信息，无法用于操作</summary>
    public static final String USER_INVALID = "身份信息无效";
    public static final String USER_NAME_ALREADY_EXISTS = "用户名已存在";
    public static final String USER_ROLE_REQUIRED = "用户身份不能为空";
    public static final String USER_NAME_REQUIRED = "用户名不能为空";
    public static final String USER_PASSWORD_REQUIRED = "密码不能为空";
    public static final String USER_OLD_PASSWORD_REQUIRED = "旧密码不能为空";
    public static final String USER_NEW_PASSWORD_REQUIRED = "新密码不能为空";
    public static final String USER_AVATAR_REQUIRED = "新密码不能为空";
    public static final String USER_REGISTER_SUCCESS = "注册成功";
    public static final String USER_PASSWORD_ERROR = "密码错误";
    public static final String USER_LOGIN_SUCCESS = "登录成功";
    public static final String USER_LOGOUT_SUCCESS = "退出登录成功";
    public static final String USER_OLD_PASSWORD_ERROR = "旧密码错误";
    public static final String USER_NEW_PASSWORD_SAME_AS_OLD = "新密码不能与旧密码相同";
    public static final String USER_PASSWORD_UPDATE_SUCCESS = "密码更新成功";
    public static final String USER_AVATAR_UPLOAD_FAILED = "头像上传失败";
    public static final String USER_AVATAR_UPLOADING = "头像上传中";
    public static final String USER_SEARCH_ONLY_ADMIN = "只有管理员可以查询用户";
    public static final String USER_DELETE_ONLY_ADMIN = "只有管理员可以删除用户";
    public static final String USER_DELETE_SUCCESS = "用户删除成功";
    public static final String USER_FRONTEND_SALT_REQUIRED = "前端盐值不能为空";
    public static final String USER_SALT_GET_SUCCESS = "获取盐值成功";
    public static final String USER_TOKEN_REFRESH_SUCCESS = "令牌刷新成功";

    public static final String PRODUCT_ID_REQUIRED = "商品ID不能为空";
    public static final String PRODUCT_NAME_REQUIRED = "商品名称不能为空";
    public static final String PRODUCT_CATEGORY_REQUIRED = "商品分类不能为空";
    public static final String PRODUCT_PURCHASE_PRICE_REQUIRED = "商品进价不能为空";
    public static final String PRODUCT_SALE_PRICE_REQUIRED = "商品售价不能为空";
    public static final String PRODUCT_STOCK_REQUIRED = "商品库存不能为空";
    public static final String PRODUCT_IMAGE_REQUIRED = "商品图片不能为空";
    public static final String PRODUCT_NOT_FOUND = "商品不存在";
    public static final String PRODUCT_CATEGORY_INVALID = "商品分类不存在";
    public static final String PRODUCT_ADD_ONLY_BY_MERCHANT = "只有商家才能新增商品";
    public static final String PRODUCT_ADD_SUCCESS = "新增商品成功";
    public static final String PRODUCT_GET_ONLY_OWN = "只能获取自己商品的信息";
    public static final String PRODUCT_UPDATE_ONLY_OWN = "只能更新自己商品的信息";
    public static final String PRODUCT_UPDATE_SUCCESS = "更新商品成功";
    public static final String PRODUCT_DELETE_ONLY_OWN = "只能删除自己的商品";
    public static final String PRODUCT_DELETE_SUCCESS = "删除商品成功";
    public static final String PRODUCT_IMAGE_UPLOAD_ONLY_MERCHANT = "只有商家才能上传商品图片";
    public static final String PRODUCT_IMAGE_UPLOAD_ONLY_OWN = "只能上传自己商品的图片";
    public static final String PRODUCT_IMAGE_UPLOAD_FAILED = "商品图片上传失败";
    public static final String PRODUCT_IMAGE_UPLOADING = "商品图片上传中";

    public static final String CATEGORY_ADD_ONLY_ADMIN = "只有管理员能够添加商品分类";
    public static final String CATEGORY_UPDATE_ONLY_ADMIN = "只有管理员能够更新商品分类";
    public static final String CATEGORY_DELETE_ONLY_ADMIN = "只有管理员能够删除商品分类";
    public static final String CATEGORY_ID_REQUIRED = "商品分类ID不能为空";
    public static final String CATEGORY_NAME_REQUIRED = "商品分类名称不能为空";
    public static final String CATEGORY_NOT_FOUND = "商品分类不存在";
    public static final String CATEGORY_ADD_SUCCESS = "新增商品分类成功";
    public static final String CATEGORY_UPDATE_SUCCESS = "更新商品分类成功";
    public static final String CATEGORY_DELETE_SUCCESS = "删除商品分类成功";

    public static final String ORDER_ID_REQUIRED = "订单ID不能为空";
    public static final String ORDER_BUYER_NAME_REQUIRED = "买家姓名不能为空";
    public static final String ORDER_BUYER_PHONE_REQUIRED = "买家电话不能为空";
    public static final String ORDER_PRODUCT_ID_REQUIRED = "商品ID不能为空";
    public static final String ORDER_QUANTITY_REQUIRED = "购买数量不能为空";
    public static final String ORDER_NOT_FOUND = "订单不存在";
    public static final String ORDER_PRODUCT_NOT_FOUND = "订单商品不存在";
    public static final String ORDER_PRODUCT_NO_STOCK = "商品库存不足";
    public static final String ORDER_ADD_ONLY_BY_MERCHANT = "只有商家才能新增订单";
    public static final String ORDER_ADD_SUCCESS = "新增订单成功";
    public static final String ORDER_UPDATE_ONLY_OWN = "只能更新自己的订单";
    public static final String ORDER_UPDATE_SUCCESS = "更新订单成功";
    public static final String ORDER_DELETE_ONLY_OWN = "只能删除自己的订单";
    public static final String ORDER_DELETE_SUCCESS = "删除订单成功";
    public static final String ORDER_STATUS_INVALID = "订单状态无效";
    public static final String ORDER_LOCK_FAILED = "操作繁忙，请稍后重试";

    public static final String STORE_STATS_ONLY_OWN = "只能查询自己的店铺数统计";
    public static final String STORE_STATS_ID_REQUIRED = "查询店铺统计需指定商家ID";
    public static final String STORE_STATS_MERCHANT_NOT_FOUND = "指定的商家不存在";
}
