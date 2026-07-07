package org.gms.util;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT（JSON Web Token）工具类，用于生成、解析与校验访问令牌。
 * <p>
 * 密钥与过期时间由配置项 {@code jwt.secret}、{@code jwt.duration} 注入。
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.duration}")
    private int jwtDuration;

    /**
     * 为指定用户名生成 JWT 令牌。
     *
     * @param username 用户名（作为 subject）
     * @return 签名后的 JWT 字符串
     */
    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject((username))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtDuration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * 从 JWT 令牌中解析用户名（subject）。
     *
     * @param token JWT 字符串
     * @return 用户名
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 校验 JWT 令牌是否有效（签名正确且未过期）。
     *
     * @param authToken JWT 字符串
     * @return 有效返回 {@code true}，否则返回 {@code false}
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("访问者的Token签名无效: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("访问者的Token无效: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("访问者的Token已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("访问者的Token不被支持: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("访问者的Token参数为空: {}", e.getMessage());
        }

        return false;
    }
}
