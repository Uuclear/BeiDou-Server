package org.gms.aop;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.exception.BizException;
import org.gms.service.AccountService;
import org.gms.util.RateLimitUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * 服务器业务过滤器
 * <p>
 * 处理业务层面的请求过滤，该过滤器在Spring Security过滤器之后执行。
 * 主要功能包括：
 * <ul>
 *   <li>获取真实客户端IP地址（支持X-Forwarded-For和X-Real-IP）</li>
 *   <li>IP封禁检查</li>
 *   <li>请求限流</li>
 *   <li>缓存请求体，允许多次读取（非multipart请求）</li>
 * </ul>
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Slf4j
@Component
@AllArgsConstructor
public class ServerFilter extends HttpFilter {

    /**
     * 账号服务，用于检查IP封禁状态
     */
    private final AccountService accountService;

    /**
     * 判断是否应该跳过过滤
     * <p>
     * 以下资源直接放行：
     * <ul>
     *   <li>/assets开头的静态资源</li>
     *   <li>Swagger UI和API文档（/swagger-ui、/v3/api-docs）</li>
     *   <li>根路径/</li>
     * </ul>
     * </p>
     *
     * @param request HTTP请求对象
     * @return true表示跳过过滤，false表示需要过滤
     */
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        // web resource
        if (requestURI.startsWith("/assets")) {
            return true;
        }
        // swagger
        if (requestURI.startsWith("/swagger-ui") || requestURI.startsWith("/v3/api-docs")) {
            return true;
        }
        return "/".equals(requestURI);
    }

    /**
     * 执行过滤逻辑
     * <p>
     * 处理流程：
     * <ol>
     *   <li>获取客户端真实IP地址</li>
     *   <li>检查IP是否被封禁</li>
     *   <li>IP限流检查</li>
     *   <li>对需要过滤的非multipart请求，包装为可重复读取请求体的CachedHttpServletRequest</li>
     * </ol>
     * </p>
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param chain    过滤器链
     * @throws IOException      如果发生I/O错误
     * @throws ServletException 如果发生Servlet错误
     */
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String forwardedIp = request.getHeader("X-Forwarded-For");
            String realIp = request.getHeader("X-Real-IP");
            String remoteAddr = request.getRemoteAddr();
            if (RequireUtil.isEmpty(remoteAddr)) remoteAddr = forwardedIp;
            if (RequireUtil.isEmpty(remoteAddr)) remoteAddr = realIp;
            RequireUtil.requireNotEmpty(remoteAddr, "Unknown remote address");

            // 封禁ip禁止请求
            if (accountService.isBanned(remoteAddr)) {
                request.getInputStream().close();
                throw new BizException("Banned ip is requesting, forwardedIp: " + forwardedIp + ",realIp: " + realIp + ", remoteAddr: " + remoteAddr);
            }

            // 限流
            if (!RateLimitUtil.getInstance().check(remoteAddr)) {
                throw new BizException("IP " + remoteAddr + " has reached rate limit.");
            }
        } catch (Exception e) {
            log.error("Filter error", e);
            // 释放流，否则可能内存泄漏
            request.getInputStream().close();
            response.getOutputStream().close();
            return;
        }
        // 这一步应该在限流之后进行
        if (shouldNotFilter(request)) {
            chain.doFilter(request, response);
            return;
        }
        if (request.getContentType() == null || request.getContentType().contains("multipart/form-data")) {
            chain.doFilter(request, response);
            return;
        }
        // 替换成允许多次读取的HttpServletRequest
        chain.doFilter(new CachedHttpServletRequest(request), response);
    }

    /**
     * 可缓存请求体的HttpServletRequest包装类
     * <p>
     * 将请求体缓存到字节数组中，允许多次读取getInputStream()和getReader()。
     * 这对于需要多次读取请求体的场景（如日志记录、签名验证等）非常有用。
     * </p>
     */
    private static class CachedHttpServletRequest extends HttpServletRequestWrapper {

        /**
         * 缓存的请求体字节数组
         */
        private byte[] cachedBody;

        /**
         * 构造函数，缓存请求体
         *
         * @param request 原始HttpServletRequest
         * @throws IOException 如果读取请求体失败
         */
        public CachedHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            cacheRequestBody(request);
        }

        /**
         * 缓存请求体到字节数组
         *
         * @param request 原始HttpServletRequest
         * @throws IOException 如果读取请求体失败
         */
        private void cacheRequestBody(HttpServletRequest request) throws IOException {
            InputStream requestInputStream = request.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = requestInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            this.cachedBody = byteArrayOutputStream.toByteArray();
        }

        /**
         * 获取BufferedReader读取请求体
         *
         * @return BufferedReader对象
         */
        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        /**
         * 获取ServletInputStream读取请求体
         *
         * @return CachedServletInputStream对象
         */
        @Override
        public ServletInputStream getInputStream() {
            return new CachedServletInputStream(this.cachedBody);
        }
    }

    /**
     * 缓存的ServletInputStream实现
     * <p>
     * 从字节数组中读取数据，支持isFinished()和isReady()方法。
     * </p>
     */
    private static class CachedServletInputStream extends ServletInputStream {

        /**
         * 内部字节数组输入流
         */
        private final ByteArrayInputStream byteArrayInputStream;

        /**
         * 构造函数
         *
         * @param cachedBody 缓存的请求体字节数组
         */
        public CachedServletInputStream(byte[] cachedBody) {
            this.byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        }

        /**
         * 判断流是否已读完
         *
         * @return true表示已读完，false表示还有数据
         */
        @Override
        public boolean isFinished() {
            return byteArrayInputStream.available() == 0;
        }

        /**
         * 判断流是否就绪可以读取
         *
         * @return 始终返回true
         */
        @Override
        public boolean isReady() {
            return true;
        }

        /**
         * 设置读取监听器（不支持异步，抛出异常）
         *
         * @param listener ReadListener
         */
        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException();
        }

        /**
         * 读取一个字节
         *
         * @return 读取的字节，-1表示已到流末尾
         */
        @Override
        public int read() {
            return byteArrayInputStream.read();
        }
    }
}
