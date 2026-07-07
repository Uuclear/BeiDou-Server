/**
 * REST API 控制器层（v1 版本）。
 * <p>
 * 为 gms-ui 管理后台提供 HTTP 接口，涵盖账号、角色、掉落、商城、扭蛋、
 * 自动封禁配置等功能。接口默认受 Spring Security + JWT 保护。
 * 游戏客户端不经过此层，直接连接 Netty TCP 端口。
 */
package org.gms.controller;
