package org.gms.constants.string;

import lombok.Getter;

/**
 * 扩展键枚举
 * <p>
 * 定义扩展数据的键名，用于标识不同类型的扩展统计数据。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public enum ExtendKey {
    /** 每日在线时间 */
    ONLINE_TIME("每日在线时间");

    /** 键名 */
    @Getter
    private final String key;

    ExtendKey(String key) {
        this.key = key;
    }
}
