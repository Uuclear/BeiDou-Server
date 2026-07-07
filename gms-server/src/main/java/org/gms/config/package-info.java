/**
 * Spring Boot 配置类，涵盖安全、数据源、游戏参数等。
 * <p>
 * 游戏运行时配置（如频道数、经验倍率）部分来自数据库 {@code game_config} 表，
 * 通过 {@link org.gms.config.GameConfig} 在启动时加载。
 */
package org.gms.config;
