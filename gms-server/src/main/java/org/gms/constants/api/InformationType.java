package org.gms.constants.api;

import lombok.Getter;

/**
 * 信息类型枚举
 * <p>
 * 定义游戏中各种信息数据的类型，用于分类查询和管理游戏资源数据。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Getter
public enum InformationType {

    /**
     * 现金道具
     */
    CASH("cash"),

    /**
     * 消耗品道具
     */
    CONSUME("consume"),

    /**
     * 装备道具
     */
    EQP("eqp"),

    /**
     * 其他道具
     */
    ETC("etc"),

    /**
     * 安装道具
     */
    INS("ins"),

    /**
     * 地图信息
     */
    MAP("map"),

    /**
     * 怪物信息
     */
    MOB("mob"),

    /**
     * NPC信息
     */
    NPC("npc"),

    /**
     * 宠物信息
     */
    PET("pet"),

    /**
     * 技能信息
     */
    SKILL("skill"),
    ;

    /**
     * 类型字符串标识
     */
    private final String type;

    /**
     * 构造函数
     *
     * @param type 类型字符串标识
     */
    InformationType(final String type) {
        this.type = type;
    }

    /**
     * 根据类型字符串获取对应的枚举值
     *
     * @param type 类型字符串
     * @return 对应的InformationType枚举，如果未找到返回null
     */
    public static InformationType ofType(final String type) {
        for (InformationType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}
