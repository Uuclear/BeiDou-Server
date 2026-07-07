package org.gms.manager;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.gms.ServerApplication;
import org.gms.constants.net.ServerConstants;
import org.gms.net.server.Server;
import org.gms.util.I18nUtil;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.InetAddress;

/**
 * Spring Boot 与游戏核心 {@link org.gms.net.server.Server} 的生命周期桥接器。
 * <p>
 * Spring 容器启动完成后调用 {@link org.gms.net.server.Server#init()} 加载 WZ 数据、
 * 启动 Netty 登录/频道服务；容器关闭时调用 {@code shutdownInternal} 优雅停机。
 * 静态 {@link #applicationContext} 供非 Spring 管理的游戏代码获取 Bean。
 */
@Component
@Slf4j
public class ServerManager implements ApplicationContextAware, ApplicationRunner, DisposableBean {

    /** 全局 Spring 应用上下文，由容器注入后供游戏核心代码使用。 */
    @Getter
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ServerManager.applicationContext = applicationContext;
    }

    /**
     * Spring Boot 启动完成后执行：初始化游戏服、打印版本与 Swagger/前端访问地址。
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Server.getInstance().init();

        SpringDocConfigProperties springDocConfigProperties = applicationContext.getBean(SpringDocConfigProperties.class);
        SwaggerUiConfigProperties swaggerUiConfigProperties = applicationContext.getBean(SwaggerUiConfigProperties.class);
        Environment environment = applicationContext.getBean(Environment.class);
        log.info(I18nUtil.getLogMessage("ServerManager.run.info3"), ServerConstants.BEI_DOU_VERSION, ServerConstants.BEI_DOU_BUILD_TIME);
        if (springDocConfigProperties.getApiDocs().isEnabled() && swaggerUiConfigProperties.isEnabled()) {
            log.info(I18nUtil.getLogMessage("ServerManager.run.info1"), InetAddress.getLocalHost().getHostAddress(), environment.getProperty("server.port"));
        }
        // 判断是否集成前端，集成则提示前端地址
        try(InputStream resource = ServerApplication.class.getClassLoader().getResourceAsStream("static/index.html")) {
            if (resource != null) {
                log.info(I18nUtil.getLogMessage("ServerManager.run.info2"), InetAddress.getLocalHost().getHostAddress(), environment.getProperty("server.port"));
            }
        }
    }

    /**
     * Spring 容器销毁时优雅关闭游戏服（保存玩家数据、关闭 Netty 端口）。
     */
    @Override
    public void destroy() throws Exception {
        Server.getInstance().shutdownInternal(false);
    }
}
