package org.gms.net.server.coordinator.session;

import org.gms.net.server.Server;

import java.time.Instant;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

/**
 * HWID 与账号关联的过期时间计算，根据登录相关度动态调整缓存时长。
 */
public class HwidAssociationExpiry {
    /**
     * 根据相关度计算 HWID 账号关联的过期时刻。
     *
     * @param relevance 登录相关度
     * @return 过期时间
     */
    public static Instant getHwidAccountExpiry(int relevance) {
        return Instant.ofEpochMilli(Server.getInstance().getCurrentTime()).plusMillis(hwidExpirationUpdate(relevance));
    }

    private static long hwidExpirationUpdate(int relevance) {
        int degree = getHwidExpirationDegree(relevance);

        final long baseHours = switch (degree) {
            case 0 -> 2;
            case 1 -> DAYS.toHours(1);
            case 2 -> DAYS.toHours(7);
            default -> DAYS.toHours(70);
        };

        int subdegreeTime = (degree * 3) + 1;
        if (subdegreeTime > 10) {
            subdegreeTime = 10;
        }

        return HOURS.toMillis(baseHours + subdegreeTime);
    }

    private static int getHwidExpirationDegree(int relevance) {
        int degree = 1;
        int subdegree;
        while ((subdegree = 5 * degree) <= relevance) {
            relevance -= subdegree;
            degree++;
        }

        return --degree;
    }
}
