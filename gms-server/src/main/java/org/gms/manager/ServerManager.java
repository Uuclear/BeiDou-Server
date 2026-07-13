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
 * 服务器管理器
 * <p>
 * Spring Boot应用生命周期管理类，负责：
 * <ul>
 *   <li>保存Spring ApplicationContext引用，供非Spring管理的类获取Bean</li>
 *   <li>应用启动完成后初始化游戏服务器</li>
 *   <li>应用关闭时优雅关停游戏服务器</li>
 *   <li>打印服务器启动信息，包括版本号、Swagger地址、前端地址等</li>
 * </ul>
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Component
@Slf4j
public class ServerManager implements ApplicationContextAware, ApplicationRunner, DisposableBean {

    /**
     * Spring应用上下文，静态保存供全局使用
     */
    @Getter
    private static ApplicationContext applicationContext;

    /**
     * 设置应用上下文
     * <p>
     * 实现ApplicationContextAware接口，Spring容器启动时自动调用，
     * 将ApplicationContext保存到静态变量中，供非Spring管理的类使用。
     * </p>
     *
     * @param applicationContext Spring应用上下文
     * @throws BeansException 如果设置上下文失败
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ServerManager.applicationContext = applicationContext;
    }

    /**
     * 应用启动完成后执行
     * <p>
     * Spring Boot应用启动完成后回调此方法，执行以下操作：
     * <ol>
     *   <li>初始化游戏服务器（Server.getInstance().init()）</li>
     *   <li>打印北斗版本号和构建时间</li>
     *   <li>如果Swagger启用，打印API文档地址</li>
     *   <li>检测是否集成前端静态资源，如果有则打印前端访问地址</li>
     * </ol>
     * </p>
     *
     * @param args 应用启动参数
     * @throws Exception 如果初始化过程中发生异常
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
        try (InputStream resource = ServerApplication.class.getClassLoader().getResourceAsStream("static/index.html")) {
            if (resource != null) {
                log.info(I18nUtil.getLogMessage("ServerManager.run.info2"), InetAddress.getLocalHost().getHostAddress(), environment.getProperty("server.port"));
            }
        }
    }

    /**
     * 应用销毁时执行
     * <p>
     * Spring容器关闭时回调此方法，执行游戏服务器的优雅关停逻辑。
     * </p>
     *
     * @throws Exception 如果关停过程中发生异常
     */
    @Override
    public void destroy() throws Exception {
        Server.getInstance().shutdownInternal(false);
    }
}
