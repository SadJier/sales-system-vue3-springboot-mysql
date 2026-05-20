package com.sadjier.util;

import com.sadjier.enums.UserRolesEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    /// <summary>密钥(固定)</summary>
    private static String SECRET_KEY;
    @Value("${jwt.secret}")
    private String _jwt_secret;
    /// <summary>过期时间：2小时</summary>
    private static final long EXPIRE_TIME = 2 * 60 * 60 * 1000;
    /// <summary>访问令牌过期时间：10分钟</summary>
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 10 * 60 * 1000;
    /// <summary>刷新令牌过期时间：7天</summary>
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
    /// <summary>Redis键前缀常量</summary>
    public static final String REDIS_ACCESS_PREFIX = "access:";
    /// <summary>Redis刷新令牌键前缀</summary>
    public static final String REDIS_REFRESH_PREFIX = "refresh:";
    /// <summary>Cookies的刷新令牌存储名</summary>
    public static final String COOKIE_REFRESH_NAME = "refreshToken";
    /// <summary>令牌类型枚举</summary>
    public enum TokenType {
        /// <summary>访问令牌</summary>
        ACCESS,
        /// <summary>刷新令牌</summary>
        REFRESH
    }

    @PostConstruct
    public void init() {
        SECRET_KEY = this._jwt_secret;
    }
    /// <summary>获取密钥</summary>
    private static Key getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    /// <summary>生成访问令牌</summary>
    public static String generateToken(Long user_id, UserRolesEnum role) {
        return Jwts.builder()
                .setSubject(user_id.toString())
                .claim("role", role)
                .claim("type", TokenType.ACCESS.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    /// <summary>生成刷新令牌</summary>
    public static String generateRefreshToken(Long user_id, UserRolesEnum role) {
        return Jwts.builder()
                .setSubject(user_id.toString())
                .claim("role", role)
                .claim("type", TokenType.REFRESH.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(getKey(), SignatureAlgorithm.HS256)
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
    /// <summary>获取令牌类型</summary>
    public static TokenType getTokenType(Claims claims) {
        if (claims == null) return null;
        String type_str = claims.get("type", String.class);
        if (!StringUtils.hasText(type_str)) return null;
        return TokenType.valueOf(type_str);
    }
    /// <summary>验证Token是否有效</summary>
    /// <remarks>过期或无效都返回false，仅接受访问令牌</remarks>
    public static boolean validateToken(String token) {
        var claims = parseToken(token);
        if (claims == null || claims.getExpiration().before(new Date())) return false;
        return TokenType.ACCESS == getTokenType(claims);
    }
    /// <summary>验证刷新令牌是否有效</summary>
    public static boolean validateRefreshToken(String token) {
        var claims = parseToken(token);
        if (claims == null || claims.getExpiration().before(new Date())) return false;
        return TokenType.REFRESH == getTokenType(claims);
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
}
