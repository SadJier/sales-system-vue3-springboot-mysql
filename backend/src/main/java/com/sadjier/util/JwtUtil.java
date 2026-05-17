package com.sadjier.util;

import com.sadjier.enums.UserRolesEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    /// <summary>密钥(固定)</summary>
    private static final String SECRET_KEY = "testsecretkey14514171717978848664bb";
    /// <summary>过期时间：2小时</summary>
    private static final long EXPIRE_TIME = 2 * 60 * 60 * 1000;

    /// <summary>获取密钥</summary>
    private static Key getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    /// <summary>生成Token</summary>
    public static String generateToken(Long user_id, UserRolesEnum role) {
        return Jwts.builder()
                .setSubject(user_id.toString()) // 存入用户id
                .claim("role", role) // 存入身份
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME)) // 过期时间
                .signWith(getKey(), SignatureAlgorithm.HS256) // 加密
                .compact();
    }
    /// <summary>解析Token</summary>
    /// <remarks>失效仍返回过期的claims,解析失败返回Null</remarks>
    public static Claims parseToken(String token) {
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }catch (Exception e){
            return null;
        }
    }
    /// <summary>验证Token是否有效</summary>
    /// <remarks>过期或无效都返回false</remarks>
    public static boolean validateToken(String token) {
        var claims = parseToken(token);
        return claims != null && !claims.getExpiration().before(new Date());
    }
    /// <summary>获取令牌剩余时间</summary>
    public static long getRemainingTime(Claims claims) {
        if (claims == null) return 0;
        Date expire = claims.getExpiration();
        return expire.getTime() - System.currentTimeMillis();
    }
    /// <summary>获取用户名</summary>
    public static String getUsername(Claims claims) {
        return claims == null ? null : claims.getSubject();
    }
    /// <summary>获取当前请求的Token荷载</summary>
    public static Claims getNowClaims() {
        return parseToken(CommonUtil.getToken());
    }
    /// <summary>获取token内的用户id</summary>
    /// <remarks>若无，返回null</remarks>
    public static Long getUserId(Claims claims){
        if(claims == null) return null;
        String user_id_str = claims.getSubject();
        if(!StringUtils.hasText(user_id_str)) return null;
        try{
            return Long.valueOf(user_id_str);
        }catch (Exception e){
            log.error("token内userid解析异常{}", e.getMessage(), e);
            return null;
        }
    }
    /// <summary>获取token内的用户id</summary>
    /// <remarks>若无，返回null</remarks>
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return getUserId(claims);
    }
    /// <summary>获取当前请求的token内的用户id</summary>
    /// <remarks>若无，返回null</remarks>
    public static Long getUserId(){
        return getUserId(CommonUtil.getToken());
    }
    /// <summary>获取token内的用户Role</summary>
    /// <remarks>若无，返回null</remarks>
    public static UserRolesEnum getUserRole(Claims claims){
        if(claims == null) return null;
        String user_role_str = claims.get("role",String.class);
        if(!StringUtils.hasText(user_role_str)) return null;
        try{
            return UserRolesEnum.valueOf(user_role_str);
        }catch (Exception e){
            log.error("token内role解析异常{}", e.getMessage(), e);
            return null;
        }
    }
    /// <summary>获取token内的用户Role</summary>
    /// <remarks>若无，返回null</remarks>
    public static UserRolesEnum getUserRole(String token){
        Claims claims = parseToken(token);
        return getUserRole(claims);
    }
    /// <summary>获取当前请求的token内的用户Role</summary>
    /// <remarks>若无，返回null</remarks>
    public static UserRolesEnum getUserRole(){
        return getUserRole(CommonUtil.getToken());
    }
    /// <summary>获取token内的用户身份呢</summary>
}
