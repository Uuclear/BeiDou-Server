package org.gms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域资源共享（CORS）配置类，允许 Vue 前端域名访问 REST API。
 * 通过 WebMvcConfigurer 全局注册跨域规则。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Value("${app.vue}")
    private String vue;

    /**
     * 注册全局 CORS 规则，允许 Vue 前端跨域访问 API。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")// 项目中的所有接口都支持跨域  
                .allowedOriginPatterns(vue)// 所有地址都可以访问，也可以配置具体地址  
                .allowCredentials(true)
                .allowedMethods("*")//"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"  
                .maxAge(3600);// 跨域允许时间  
    }
}
