package org.gms.net.server.coordinator.session;

import org.gms.net.server.Server;

import java.time.Instant;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 主机HWID记录类
 * 存储主机的硬件ID及其过期时间
 * 用于追踪和限制同一台机器的登录
 *
 * @author OdinMS开发团队
 */
record HostHwid(Hwid hwid, Instant expiry) {
    /**
     * 创建使用默认过期时间（7天）的HostHwid
     *
     * @param hwid 硬件ID对象
     * @return HostHwid实例
     */
    static HostHwid createWithDefaultExpiry(Hwid hwid) {
        return new HostHwid(hwid, getDefaultExpiry());
    }

    /**
     * 获取默认过期时间（当前时间+7天）
     *
     * @return 过期时间Instant
     */
    private static Instant getDefaultExpiry() {
        return Instant.ofEpochMilli(Server.getInstance().getCurrentTime() + DAYS.toMillis(7));
    }
}
