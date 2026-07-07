/**
 * 北斗（BeiDou）MapleStory v83 私服服务端根包。
 * <p>
 * 本服务端基于 Cosmic 汉化与优化，采用双运行时架构：
 * <ul>
 *   <li>{@link org.gms.ServerApplication} — Spring Boot 3 启动入口，提供 REST 管理 API（默认端口 8686）</li>
 *   <li>{@link org.gms.net.server.Server} — 传统游戏核心单例，负责 Netty TCP 游戏协议（登录 8484 / 频道）</li>
 * </ul>
 * 两者通过 {@link org.gms.manager.ServerManager} 在同一 JVM 进程中桥接。
 *
 * @see org.gms.net 网络协议与包处理
 * @see org.gms.client 玩家会话与角色状态
 * @see org.gms.server 地图、怪物、任务等游戏世界逻辑
 */
package org.gms;
