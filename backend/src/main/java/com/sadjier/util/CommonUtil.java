package com.sadjier.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/// <summary>杂项工具</summary>
public class CommonUtil {
    /// <summary>用户头像图片相对路径</summary>
    @Value("${file.avatar.path}")
    private static String avatar_path;
    /// <summary>商品图片相对路径</summary>
    @Value("${file.product.path}")
    private static String product_image_path;

    /// <summary>获取头像保存路径</summary>
    public static String getAvatarFolderPath(){
        String project_path = System.getProperty("user.dir");
        return project_path + "/" + avatar_path;
    }
    /// <summary>获取商品图片保存路径</summary>
    public static String getProductImageFolderPath(){
        String project_path = System.getProperty("user.dir");
        return project_path + "/" + product_image_path;
    }
    /// <summary>获取当前请求Token</summary>
    public static String getToken() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return null;

            HttpServletRequest request = attributes.getRequest();

            return request.getHeader("Authorization");
        } catch (Exception e) {
            return null;
        }
    }
}
