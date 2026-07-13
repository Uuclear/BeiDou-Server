package org.gms.aop;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT认证入口点
 * <p>
 * 实现Spring Security的AuthenticationEntryPoint接口，
 * 用于处理未认证用户访问受保护资源时的响应。
 * 当用户尝试访问需要认证的资源但未提供有效JWT令牌时，
 * 返回401 Unauthorized错误响应。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * 处理未认证请求
     * <p>
     * 当用户未认证时触发此方法，记录错误日志并返回401状态码。
     * </p>
     *
     * @param request       HTTP请求对象
     * @param response      HTTP响应对象
     * @param authException 认证异常
     * @throws IOException      如果发生I/O错误
     * @throws ServletException 如果发生Servlet错误
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error with {}: {}", request.getRequestURI(), authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}
