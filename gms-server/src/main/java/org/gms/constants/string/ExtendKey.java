package org.gms.constants.string;

import lombok.Getter;
/**
 * 扩展属性键名常量，定义各业务场景下的扩展字段名称。
 */

public enum ExtendKey {
    ONLINE_TIME("每日在线时间");

    @Getter
    private final String key;

    ExtendKey(String key) {
        this.key = key;
    }
}
