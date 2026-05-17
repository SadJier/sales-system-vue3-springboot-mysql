package com.sadjier.util;

import com.sadjier.common.Result;
import com.sadjier.constant.ResultMsgConstant;
import com.sadjier.enums.ResultStatusEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/// <summary>杂项工具</summary>
@Slf4j
@Component
public class CommonUtil {
    /// <summary>用户头像图片相对路径</summary>
    private static String avatar_path;
    /// <summary>商品图片相对路径</summary>
    private static String product_image_path;

    /// <summary>通过Set方法注入静态变量</summary>
    @Value("${file.avatar.path}")
    public void setAvatarPath(String path) {
        avatar_path = path;
    }
    @Value("${file.product.path}")
    public void setProductImagePath(String path) {
        product_image_path = path;
    }

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
    /// <summary>获取商品图片</summary>
    public static Resource getProductImage(Long product_id){
        var folder_path = getProductImageFolderPath();
        String file_name = product_id + ".jpg";
        File avatar_file = new File(folder_path + "/" + file_name);
        if (!avatar_file.exists()) {
            return null;
        }
        return new FileSystemResource(avatar_file);
    }
    /// <summary>获取用户头像</summary>
    public static Resource getUserAvatar(Long user_id){
        String folder_path = getAvatarFolderPath();
        String file_name = user_id + ".jpg";//当前只有Jpg
        File avatar_file = new File(folder_path + "/" + file_name);
        if (!avatar_file.exists()) {
            return null;
        }
        return new FileSystemResource(avatar_file);
    }
    /// <summary>上传商品图片</summary>
    public static boolean uploadProductImage(Long product_id, MultipartFile file){
        String absolute_path = getProductImageFolderPath();
        File folder = new File(absolute_path);
        if (!folder.exists()) {
            boolean ignore = folder.mkdirs();
        }

        String file_name = product_id + ".jpg";
        File dest_file = new File(absolute_path + "/" + file_name);

        try{
            file.transferTo(dest_file);
            return true;
        }catch (Exception e){
            log.error("商品图片上传失败:{}",e.getMessage());
            return false;
        }
    }
    /// <summary>上传用户头像</summary>
    public static boolean uploadUserAvatar(Long user_id, MultipartFile file){
        String absolute_path = getAvatarFolderPath();
        File folder = new File(absolute_path);
        if (!folder.exists()) {
            boolean ignore = folder.mkdirs();
        }

        String file_name = user_id + ".jpg";
        File dest_file = new File(absolute_path + "/" + file_name);

        try{
            file.transferTo(dest_file);
            return true;
        }catch (Exception e){
            log.error("用户头像上传失败:{}",e.getMessage());
            return false;
        }
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
