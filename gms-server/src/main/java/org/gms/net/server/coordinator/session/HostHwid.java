package org.gms.net.server.coordinator.session;

import org.gms.net.server.Server;

import java.time.Instant;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 远程主机与 HWID 的绑定记录，包含默认 7 天过期的缓存条目。
 *
 * @param hwid   硬件标识
 * @param expiry 缓存过期时间
 */
record HostHwid(Hwid hwid, Instant expiry) {
    static HostHwid createWithDefaultExpiry(Hwid hwid) {
        return new HostHwid(hwid, getDefaultExpiry());
    }

    private static Instant getDefaultExpiry() {
        return Instant.ofEpochMilli(Server.getInstance().getCurrentTime() + DAYS.toMillis(7));
    }
}
