package org.gms.model.pojo;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流上下文实体类
 * 用于实现API请求限流功能，记录当前请求次数和过期时间
 */
@Data
public class RateLimitContext {
    /**
     * 当前请求计数（原子操作，保证线程安全）
     */
    private AtomicInteger curr;

    /**
     * 限流过期时间戳（毫秒）
     */
    private Long expire;
}
