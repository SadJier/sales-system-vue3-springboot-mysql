package com.sadjier.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    /// <summary>生成Token</summary>
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 存入用户名
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME)) // 过期时间
                .signWith(getKey(), SignatureAlgorithm.HS256) // 加密
                .compact();
    }
    /// <summary>解析Token</summary>
    /// <remarks>失效仍返回过期的claims,解析失败返回Null</remarks>
    public Claims parseToken(String token) {
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
    public boolean validateToken(String token) {
        var claims = parseToken(token);
        return claims != null && !claims.getExpiration().before(new Date());
    }
    /// <summary>获取令牌剩余时间</summary>
    public long getRemainingTime(Claims claims) {
        if (claims == null) return 0;
        Date expire = claims.getExpiration();
        return expire.getTime() - System.currentTimeMillis();
    }
    /// <summary>获取用户名</summary>
    public String getUsername(Claims claims) {
        return claims == null ? null : claims.getSubject();
    }
}
