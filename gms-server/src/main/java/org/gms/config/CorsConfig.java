package org.gms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域资源共享（CORS）配置类
 * <p>
 * 配置Spring MVC的跨域访问策略，允许前端应用从不同源访问后端API接口。
 * 通过配置文件app.vue属性指定允许的前端源地址。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 允许跨域访问的前端地址
     * 从配置文件app.vue属性中读取，支持通配符模式匹配
     */
    @Value("${app.vue}")
    private String vue;

    /**
     * 配置CORS跨域映射
     * <p>
     * 对所有接口路径配置跨域规则：
     * <ul>
     *   <li>允许配置文件中指定的前端源地址访问</li>
     *   <li>允许携带认证信息（Cookie等）</li>
     *   <li>允许所有HTTP方法（GET、POST、PUT、DELETE、OPTIONS等）</li>
     *   <li>预检请求缓存时间为3600秒（1小时）</li>
     * </ul>
     * </p>
     *
     * @param registry CORS注册表，用于注册跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(vue)
                .allowCredentials(true)
                .allowedMethods("*")
                .maxAge(3600);
    }
}
