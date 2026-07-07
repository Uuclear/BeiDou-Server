package org.gms.util;

import lombok.extern.slf4j.Slf4j;
import org.gms.manager.ServerManager;
import org.gms.model.pojo.RateLimitContext;
import org.gms.property.ServiceProperty;
import org.gms.service.AccountService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于 IP 的请求频率限制工具（单例）。
 * <p>
 * 在配置的时间窗口内统计同一 IP 的请求次数，超限时可按配置自动封禁该 IP。
 */
@Slf4j
public class RateLimitUtil {
    private static RateLimitUtil instance;
    private final ServiceProperty.RateLimitProperty rateLimitProperty;
    private final Map<String, RateLimitContext> contextMap;

    private RateLimitUtil() {
        rateLimitProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class).getRateLimit();
        contextMap = new HashMap<>();
    }

    /**
     * 获取频率限制工具的单例实例。
     *
     * @return {@link RateLimitUtil} 实例
     */
    public static RateLimitUtil getInstance() {
        if (instance == null) {
            instance = new RateLimitUtil();
        }
        return instance;
    }

    /**
     * 检查指定 IP 是否允许继续请求。
     * <p>
     * 未启用限制时始终返回 {@code true}；窗口过期后计数重置；超限时可选自动封禁。
     *
     * @param ip 客户端 IP 地址
     * @return 允许请求返回 {@code true}，被限流返回 {@code false}
     */
    public boolean check(String ip) {
        if (!rateLimitProperty.isEnabled()) {
            return true;
        }
        try {
            RateLimitContext rateLimitContext = contextMap.get(ip);
            if (rateLimitContext == null) {
                rateLimitContext = new RateLimitContext();
                rateLimitContext.setCurr(new AtomicInteger(1));
                rateLimitContext.setExpire(System.currentTimeMillis() + rateLimitProperty.getDuration());
                contextMap.put(ip, rateLimitContext);
                return true;
            }
            if (rateLimitContext.getExpire() < System.currentTimeMillis()) {
                rateLimitContext.setExpire(System.currentTimeMillis() + rateLimitProperty.getDuration());
                rateLimitContext.getCurr().set(1);
                contextMap.put(ip, rateLimitContext);
                return true;
            }
            if (rateLimitContext.getCurr().incrementAndGet() > rateLimitProperty.getLimit()) {
                if (rateLimitProperty.isAutoBan()) {
                    AccountService accountService = ServerManager.getApplicationContext().getBean(AccountService.class);
                    accountService.ban(ip, "Auto banned by rate limit", true);
                }
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Rate limit check error", e);
        }
        return false;
    }
}
