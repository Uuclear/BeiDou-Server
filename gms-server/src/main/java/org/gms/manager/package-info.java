/**
 * Spring 与游戏核心之间的桥接层。
 * <p>
 * {@link org.gms.manager.ServerManager} 在 Spring Boot 启动完成后初始化
 * {@link org.gms.net.server.Server}，并在容器销毁时优雅关闭游戏服务。
 * 同时持有静态 {@code ApplicationContext}，供非 Spring 管理的游戏代码访问 Bean。
 */
package org.gms.manager;
