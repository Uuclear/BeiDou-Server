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
 * 业务请求过滤器，在 Spring Security 之后执行 IP 封禁、限流与请求体包装。
 * 跳过静态资源与 Swagger 路径，是 REST 入站的第二层防护。
 */
@Slf4j
@Component
@AllArgsConstructor
public class ServerFilter extends HttpFilter {
    private final AccountService accountService;

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
        // 这一步 应该在限流之后进行
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

    private static class CachedHttpServletRequest extends HttpServletRequestWrapper {

        private byte[] cachedBody;

        /**
         * 执行 CachedHttpServletRequest 相关业务逻辑。
         *
         * @param request 请求体封装对象
         */
        public CachedHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            cacheRequestBody(request);
        }

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
         * 执行 getReader 相关业务逻辑。
         * @return BufferedReader 类型结果
         */
        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        /**
         * 执行 getInputStream 相关业务逻辑。
         * @return ServletInputStream 类型结果
         */
        @Override
        public ServletInputStream getInputStream() {
            return new CachedServletInputStream(this.cachedBody);
        }
    }

    private static class CachedServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream byteArrayInputStream;

        /**
         * 执行 CachedServletInputStream 相关业务逻辑。
         *
         * @param cachedBody cachedBody
         */
        public CachedServletInputStream(byte[] cachedBody) {
            this.byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        }

        /**
         * 执行 isFinished 相关业务逻辑。
         * @return boolean 类型结果
         */
        @Override
        public boolean isFinished() {
            return byteArrayInputStream.available() == 0;
        }

        /**
         * 执行 isReady 相关业务逻辑。
         * @return boolean 类型结果
         */
        @Override
        public boolean isReady() {
            return true;
        }

        /**
         * 执行 setReadListener 相关业务逻辑。
         *
         * @param listener listener
         */
        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException();
        }

        /**
         * 执行 read 相关业务逻辑。
         * @return int 类型结果
         */
        @Override
        public int read() {
            return byteArrayInputStream.read();
        }
    }
}
