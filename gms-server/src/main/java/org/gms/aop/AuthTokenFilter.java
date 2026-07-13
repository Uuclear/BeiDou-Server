package org.gms.aop;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.gms.service.UserDetailsServiceImpl;
import org.gms.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证令牌过滤器
 * <p>
 * 继承OncePerRequestFilter确保每个请求只执行一次过滤。
 * 从请求头Authorization中解析JWT令牌，验证其有效性，
 * 并设置Spring Security的认证上下文。
 * </p>
 * <p>
 * 特殊处理：
 * <ul>
 *   <li>/auth/开头的接口直接放行，不做JWT校验</li>
 *   <li>Swagger启用时，使用"swagger"作为测试token可直接以admin身份登录（仅用于开发环境）</li>
 * </ul>
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    /**
     * JWT工具类
     */
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户详情服务
     */
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * SpringDoc配置属性（可选，用于检测Swagger是否启用）
     */
    @Autowired(required = false)
    private SpringDocConfigProperties springDocConfigProperties;

    /**
     * Swagger UI配置属性（可选，用于检测Swagger是否启用）
     */
    @Autowired(required = false)
    private SwaggerUiConfigProperties swaggerUiConfigProperties;

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * 执行过滤器内部逻辑
     * <p>
     * 处理流程：
     * <ol>
     *   <li>放行/auth/开头的授权接口</li>
     *   <li>解析JWT令牌</li>
     *   <li>处理Swagger测试token（开发环境）</li>
     *   <li>验证JWT令牌有效性</li>
     *   <li>加载用户详情并设置认证上下文</li>
     * </ol>
     * </p>
     *
     * @param request     HTTP请求对象
     * @param response    HTTP响应对象
     * @param filterChain 过滤器链
     * @throws ServletException 如果发生Servlet错误
     * @throws IOException      如果发生I/O错误
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 授权接口不做身份校验
            if (request.getRequestURI().startsWith("/auth/")) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwt = parseJwt(request);
            // 测试token，生产环境一定要把swagger关掉，否则裸奔
            if (springDocConfigProperties != null && swaggerUiConfigProperties != null && "swagger".equals(jwt) && springDocConfigProperties.getApiDocs().isEnabled() && swaggerUiConfigProperties.isEnabled()) {
                UserDetails userDetails = userDetailsService.loadUserByUsername("admin");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Filter error", e);
            // 释放流，否则可能内存泄漏
            request.getInputStream().close();
            response.getOutputStream().close();
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 从HTTP请求中解析JWT令牌
     * <p>
     * 从Authorization请求头中提取Bearer token，去掉"Bearer "前缀后返回。
     * </p>
     *
     * @param request HTTP请求对象
     * @return JWT令牌字符串，如果没有或格式不正确返回null
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
