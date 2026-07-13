package org.gms.constants.string;

import lombok.Getter;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 扩展类型枚举
 * <p>
 * 定义扩展数据的类型，目前支持每日/每周统计，包括账号和角色两个维度。
 * 需要每月/季度/年度扩展时，请扩展此枚举。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Getter
public enum ExtendType {
    /** 账号扩展 */
    ACCOUNT_EXTEND("11"),
    /** 账号每日扩展 */
    ACCOUNT_EXTEND_DAILY("12"),
    /** 账号每周扩展 */
    ACCOUNT_EXTEND_WEEKLY("13"),
    /** 角色扩展 */
    CHARACTER_EXTEND("21"),
    /** 角色每日扩展 */
    CHARACTER_EXTEND_DAILY("22"),
    /** 角色每周扩展 */
    CHARACTER_EXTEND_WEEKLY("23"),
    /** 不支持的类型 */
    UNSUPPORTED("99");

    private final String type;

    ExtendType(String type) {
        this.type = type;
    }

    /**
     * 根据类型字符串获取扩展类型枚举
     *
     * @param type 类型字符串
     * @return 对应的扩展类型枚举，未找到返回UNSUPPORTED
     */
    public static ExtendType getExtendType(String type) {
        for (ExtendType extendType : ExtendType.values()) {
            if (extendType.getType().equals(type)) {
                return extendType;
            }
        }
        return UNSUPPORTED;
    }

    /**
     * 获取初始化的清理Map，包含每日和每周的起始时间
     *
     * @return 包含各类型起始日期的Map
     */
    public static Map<String, Date> getCleanMap() {
        Map<String, Date> map = new HashMap<>();
        Calendar mondayStart = Calendar.getInstance();
        mondayStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        mondayStart.set(Calendar.HOUR_OF_DAY, 0);
        mondayStart.set(Calendar.MINUTE, 0);
        mondayStart.set(Calendar.SECOND, 0);

        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);

        map.put(ACCOUNT_EXTEND_DAILY.getType(), new Date(todayStart.getTimeInMillis()));
        map.put(ACCOUNT_EXTEND_WEEKLY.getType(), new Date(mondayStart.getTimeInMillis()));
        map.put(CHARACTER_EXTEND_DAILY.getType(), new Date(todayStart.getTimeInMillis()));
        map.put(CHARACTER_EXTEND_WEEKLY.getType(), new Date(mondayStart.getTimeInMillis()));
        return map;
    }

    /**
     * 判断类型是否为账号维度
     *
     * @param type 类型字符串
     * @return 如果是账号维度返回true
     */
    public static boolean isAccount(String type) {
        return ACCOUNT_EXTEND.getType().equals(type) || ACCOUNT_EXTEND_DAILY.getType().equals(type) || ACCOUNT_EXTEND_WEEKLY.getType().equals(type);
    }

    /**
     * 判断类型是否为角色维度
     *
     * @param type 类型字符串
     * @return 如果是角色维度返回true
     */
     public static boolean isCharacter(String type) {
         return CHARACTER_EXTEND.getType().equals(type) || CHARACTER_EXTEND_DAILY.getType().equals(type) || CHARACTER_EXTEND_WEEKLY.getType().equals(type);
     }
}
