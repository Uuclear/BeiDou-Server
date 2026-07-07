/**
 * REST API 业务服务层。
 * <p>
 * 封装数据库操作与游戏数据查询，供 {@link org.gms.controller} 调用。
 * 与游戏核心中的直接 JDBC / 单例访问并存，主要服务于管理端而非实时游戏逻辑。
 */
package org.gms.service;
