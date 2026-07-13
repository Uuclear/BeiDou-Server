package org.gms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.gms.aop.ServerFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * 服务器配置类
 * <p>
 * 配置服务器相关的Bean，包括：
 * <ul>
 *   <li>服务器过滤器注册</li>
 *   <li>Swagger/OpenAPI文档配置</li>
 * </ul>
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Configuration
public class ServerConfig {

    /**
     * 注册服务器过滤器
     * <p>
     * 将ServerFilter注册为Servlet过滤器，拦截所有URL模式（/*）的请求。
     * 该过滤器用于处理服务器级别的请求预处理逻辑。
     * </p>
     *
     * @param serverFilter 服务器过滤器实例，由Spring自动注入
     * @return 过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<ServerFilter> filterRegistrationBean(ServerFilter serverFilter) {
        FilterRegistrationBean<ServerFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(serverFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    /**
     * 配置OpenAPI/Swagger文档
     * <p>
     * 配置Swagger UI的API文档信息，包括：
     * <ul>
     *   <li>API标题和描述</li>
     *   <li>版本信息</li>
     *   <li>全局Authorization请求头（JWT认证）</li>
     *   <li>安全要求配置</li>
     * </ul>
     * </p>
     *
     * @return OpenAPI配置实例
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("BeiDou api").description("北斗项目地址：https://github.com/BeiDouMS/BeiDou-Server").version("v1"))
                .schemaRequirement(HttpHeaders.AUTHORIZATION, new SecurityScheme().type(SecurityScheme.Type.APIKEY).name(HttpHeaders.AUTHORIZATION).in(SecurityScheme.In.HEADER))
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
    }
}
