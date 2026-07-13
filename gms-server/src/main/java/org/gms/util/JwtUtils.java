package org.gms.util;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT（JSON Web Token）工具类
 * <p>
 * 提供JWT令牌的生成、解析和验证功能，用于用户身份认证。
 * 使用HS512签名算法，令牌中包含用户名作为主题（subject）。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Component
public class JwtUtils {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * JWT密钥，从配置文件jwt.secret中读取
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * JWT令牌有效期（毫秒），从配置文件jwt.duration中读取
     */
    @Value("${jwt.duration}")
    private int jwtDuration;

    /**
     * 根据用户名生成JWT令牌
     * <p>
     * 生成的令牌包含：
     * <ul>
     *   <li>主题（subject）：用户名</li>
     *   <li>签发时间（issuedAt）：当前时间</li>
     *   <li>过期时间（expiration）：当前时间 + 配置的有效期</li>
     * </ul>
     * 使用HS512算法和配置的密钥进行签名。
     * </p>
     *
     * @param username 用户名，作为令牌的主题
     * @return 生成的JWT令牌字符串
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
     * 从JWT令牌中提取用户名
     *
     * @param token JWT令牌字符串
     * @return 令牌中存储的用户名
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 验证JWT令牌的有效性
     * <p>
     * 验证过程包括检查签名、格式、过期时间等。
     * 如果验证失败，会记录相应的错误日志。
     * </p>
     *
     * @param authToken 要验证的JWT令牌
     * @return 如果令牌有效返回true，否则返回false
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
