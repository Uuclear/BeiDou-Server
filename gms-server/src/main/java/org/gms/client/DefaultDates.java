package org.gms.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 默认日期常量工具类，提供账号生日和临时封禁的默认日期值（2005-05-11，MapleGlobal 发布日）。
 */
final public class DefaultDates {
    // May 11 2005 is the date MapleGlobal released, so it's a symbolic default value

    private DefaultDates() {
    }

    /**
     * 获取默认生日日期。
     *
     * @return 默认生日（2005-05-11）
     */
    public static LocalDate getBirthday() {
        return LocalDate.parse("2005-05-11");
    }

    /**
     * 获取默认临时封禁日期时间。
     *
     * @return 默认临时封禁时间（2005-05-11 00:00:00）
     */
    public static LocalDateTime getTempban() {
        return LocalDateTime.parse("2005-05-11T00:00:00");
    }
}
