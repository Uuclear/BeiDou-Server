package org.gms.constants.inventory;

/**
 * 装备槽位枚举
 * <p>
 * 定义装备的各个槽位类型，包括帽子、脸饰、眼饰、耳环、上衣、套服、裤裙、鞋子、
 * 手套、披风、盾牌、武器、戒指、吊坠、坐骑、马鞍、勋章、腰带等。
 * </p>
 *
 * @author The Spookster (The Real Spookster)
 */
public enum EquipSlot {

    /** 帽子 */
    HAT("Cp", -1),
    /** 特殊帽子 */
    SPECIAL_HAT("HrCp", -1),
    /** 脸饰 */
    FACE_ACCESSORY("Af", -2),
    /** 眼饰 */
    EYE_ACCESSORY("Ay", -3),
    /** 耳环 */
    EARRINGS("Ae", -4),
    /** 上衣 */
    TOP("Ma", -5),
    /** 套服 */
    OVERALL("MaPn", -5),
    /** 裤裙 */
    PANTS("Pn", -6),
    /** 鞋子 */
    SHOES("So", -7),
    /** 手套 */
    GLOVES("GlGw", -8),
    /** 现金手套 */
    CASH_GLOVES("Gv", -8),
    /** 披风 */
    CAPE("Sr", -9),
    /** 盾牌 */
    SHIELD("Si", -10),
    /** 武器 */
    WEAPON("Wp", -11),
    /** 武器2 */
    WEAPON_2("WpSi", -11),
    /** 低级武器 */
    LOW_WEAPON("WpSp", -11),
    /** 戒指 */
    RING("Ri", -12, -13, -15, -16),
    /** 吊坠 */
    PENDANT("Pe", -17),
    /** 驯服怪物 */
    TAMED_MOB("Tm", -18),
    /** 马鞍 */
    SADDLE("Sd", -19),
    /** 勋章 */
    MEDAL("Me", -49),
    /** 腰带 */
    BELT("Be", -50),
    /** 宠物装备 */
    PET_EQUIP;

    /** WZ文件中的槽位名称 */
    private String name;
    /** 允许的槽位位置数组 */
    private int[] allowed;

    EquipSlot() {
    }

    EquipSlot(String wz, int... in) {
        name = wz;
        allowed = in;
    }

    /**
     * 获取WZ文件中的槽位名称
     *
     * @return 槽位名称
     */
    public String getName() {
        return name;
    }

    /**
     * 检查指定槽位是否允许装备
     *
     * @param slot 槽位位置
     * @param cash 是否为现金道具
     * @return 如果允许装备返回true
     */
    public boolean isAllowed(int slot, boolean cash) {
        if (slot < 0) {
            if (allowed != null) {
                for (Integer allow : allowed) {
                    int condition = cash ? allow - 100 : allow;
                    if (slot == condition) {
                        return true;
                    }
                }
            }
        }
        return cash && slot < 0;
    }

    /**
     * 根据文本槽位名称获取装备槽位枚举
     *
     * @param slot 槽位名称字符串
     * @return 对应的装备槽位枚举，未找到则返回PET_EQUIP
     */
    public static EquipSlot getFromTextSlot(String slot) {
        if (!slot.isEmpty()) {
            for (EquipSlot c : values()) {
                if (c.getName() != null) {
                    if (c.getName().equals(slot)) {
                        return c;
                    }
                }
            }
        }
        return PET_EQUIP;
    }
}
