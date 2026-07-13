package org.gms.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 默认日期常量类
 * 提供系统中使用的默认日期值，作为未设置日期时的占位符
 * 2005年5月11日是MapleGlobal（冒险岛国际服）正式发布的日期，具有象征意义
 *
 * @author OdinMS Team
 */
final public class DefaultDates {
    /**
     * 私有构造函数，防止实例化
     */
    private DefaultDates() {
    }

    /**
     * 获取默认生日日期
     * @return 2005-05-11（冒险岛国际服发布日期）
     */
    public static LocalDate getBirthday() {
        return LocalDate.parse("2005-05-11");
    }

    /**
     * 获取默认封禁日期时间
     * 用于表示账号未被临时封禁
     * @return 2005-05-11T00:00:00
     */
    public static LocalDateTime getTempban() {
        return LocalDateTime.parse("2005-05-11T00:00:00");
    }
}
