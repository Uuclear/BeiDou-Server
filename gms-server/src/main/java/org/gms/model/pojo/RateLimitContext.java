package org.gms.model.pojo;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 接口限流上下文 POJO，保存当前窗口内的请求计数 curr 与窗口过期时间 expire，供速率限制拦截器使用。
 */
@Data
public class RateLimitContext {
    private AtomicInteger curr;
    private Long expire;
}
