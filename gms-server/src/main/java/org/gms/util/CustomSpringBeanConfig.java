package org.gms.util;

import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Spring Bean 补充配置：在关闭 SpringDoc/Swagger UI 时仍注册占位 Bean，避免依赖注入失败。
 */
@Configuration

public class CustomSpringBeanConfig {
    /**
     * 当 {@code springdoc.api-docs.enabled=false} 时注册空的 SpringDoc 配置 Bean。
     *
     * @return SpringDoc 配置属性
     */
    @Bean
    @ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "false")
    public SpringDocConfigProperties springDocConfigProperties() {
        return new SpringDocConfigProperties();
    }
    /**
     * 当 {@code springdoc.swagger-ui.enabled=false} 时注册空的 Swagger UI 配置 Bean。
     *
     * @return Swagger UI 配置属性
     */
    @Bean
    @ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "false")
    public SwaggerUiConfigProperties swaggerUiConfigProperties() {
        return new SwaggerUiConfigProperties();
    }
}