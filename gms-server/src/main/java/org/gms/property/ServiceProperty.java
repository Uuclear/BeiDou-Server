package org.gms.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GMS 服务配置属性类，绑定 application 中 gms.service 前缀的运行时参数。
 * 包含语言、限流、内外网地址及登录端口等 REST/游戏服共用配置。
 */
@ConfigurationProperties(prefix = "gms.service")
@Component
@Data
public class ServiceProperty {
    private String language;
    private RateLimitProperty rateLimit;
    private String wanHost;
    private String lanHost;
    private String localhost;
    private int loginPort;

    /**
     * 限流配置嵌套属性，包含开关、阈值、时间窗口及是否自动封禁。
     */
    @Data
    public static class RateLimitProperty {
        private boolean enabled;
        private int limit;
        private long duration;
        private boolean autoBan;
    }
}
