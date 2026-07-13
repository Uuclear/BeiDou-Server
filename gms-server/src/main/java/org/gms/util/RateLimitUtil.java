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
 * 请求限流工具类
 * <p>
 * 基于IP地址的请求频率限制，使用单例模式。
 * 支持配置限流开关、时间窗口、请求次数限制和自动封禁功能。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Slf4j
public class RateLimitUtil {

    /**
     * 单例实例
     */
    private static RateLimitUtil instance;

    /**
     * 限流配置属性
     */
    private final ServiceProperty.RateLimitProperty rateLimitProperty;

    /**
     * IP限流上下文映射，key为IP地址，value为限流上下文
     */
    private final Map<String, RateLimitContext> contextMap;

    /**
     * 私有构造函数，初始化配置和上下文Map
     */
    private RateLimitUtil() {
        rateLimitProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class).getRateLimit();
        contextMap = new HashMap<>();
    }

    /**
     * 获取单例实例（双重检查锁定模式）
     *
     * @return RateLimitUtil单例实例
     */
    public static RateLimitUtil getInstance() {
        if (instance == null) {
            instance = new RateLimitUtil();
        }
        return instance;
    }

    /**
     * 检查指定IP是否超过限流阈值
     * <p>
     * 检查逻辑：
     * <ol>
     *   <li>如果限流未启用，直接返回true</li>
     *   <li>如果是IP第一次请求，创建新的限流上下文</li>
     *   <li>如果超过时间窗口，重置计数器</li>
     *   <li>递增计数器，如果超过限制则返回false</li>
     *   <li>如果启用自动封禁，超过限制时自动封禁IP</li>
     * </ol>
     * </p>
     *
     * @param ip 客户端IP地址
     * @return 如果请求允许返回true，超过限流返回false
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
