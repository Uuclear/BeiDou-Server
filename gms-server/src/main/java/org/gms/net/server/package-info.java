/**
 * 游戏服务器核心运行时，管理世界、频道、公会、定时任务等。
 * <p>
 * {@link org.gms.net.server.Server} 为全局单例，在 Spring 启动后由
 * {@link org.gms.manager.ServerManager} 调用 {@code init()} 完成初始化。
 */
package org.gms.net.server;
