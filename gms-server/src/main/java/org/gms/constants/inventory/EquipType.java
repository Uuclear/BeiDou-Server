/*
    This file is part of the HeavenMS Maple Story Server
    Copyleft (L) 2016 - 2019 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.constants.inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * 装备类型枚举
 * <p>
 * 定义各种装备的类型，包括饰品、帽子、披风、上衣、脸饰、手套、发型、套服、
 * 裤裙、宠物装备、戒指、盾牌、鞋子、驯服怪物、马鞍，以及各种武器类型。
 * 提供根据道具ID获取装备类型的方法。
 * </p>
 *
 * @author RonanLana
 */
public enum EquipType {
    /** 未定义 */
    UNDEFINED(-1),
    /** 饰品 */
    ACCESSORY(0),
    /** 帽子 */
    CAP(100),
    /** 披风 */
    CAPE(110),
    /** 上衣 */
    COAT(104),
    /** 脸饰 */
    FACE(2),
    /** 手套 */
    GLOVES(108),
    /** 发型 */
    HAIR(3),
    /** 套服 */
    LONGCOAT(105),
    /** 裤裙 */
    PANTS(106),
    /** 宠物装备 */
    PET_EQUIP(180),
    /** 宠物装备-字段 */
    PET_EQUIP_FIELD(181),
    /** 宠物装备-名称标签 */
    PET_EQUIP_LABEL(182),
    /** 宠物装备-聊天气球 */
    PET_EQUIP_QUOTE(183),
    /** 戒指 */
    RING(111),
    /** 盾牌 */
    SHIELD(109),
    /** 鞋子 */
    SHOES(107),
    /** 驯服怪物 */
    TAMING(190),
    /** 驯服怪物-马鞍 */
    TAMING_SADDLE(191),
    /** 单手剑 */
    SWORD(1302),
    /** 单手斧 */
    AXE(1312),
    /** 单手钝器 */
    MACE(1322),
    /** 短剑 */
    DAGGER(1332),
    /** 短杖 */
    WAND(1372),
    /** 长杖 */
    STAFF(1382),
    /** 双手剑 */
    SWORD_2H(1402),
    /** 双手斧 */
    AXE_2H(1412),
    /** 双手钝器 */
    MACE_2H(1422),
    /** 枪 */
    SPEAR(1432),
    /** 矛 */
    POLEARM(1442),
    /** 弓 */
    BOW(1452),
    /** 弩 */
    CROSSBOW(1462),
    /** 拳套 */
    CLAW(1472),
    /** 指节 */
    KNUCKLER(1482),
    /** 手枪 */
    PISTOL(1492);

    /** 类型值 */
    private final int i;
    /** 类型值到枚举的映射缓存 */
    private static final Map<Integer, EquipType> map = new HashMap(34);

    EquipType(int val) {
        this.i = val;
    }

    /**
     * 获取类型值
     *
     * @return 类型值
     */
    public int getValue() {
        return i;
    }

    static {
        for (EquipType eqEnum : EquipType.values()) {
            map.put(eqEnum.i, eqEnum);
        }
    }

    /**
     * 根据道具ID获取装备类型
     *
     * @param itemid 道具ID
     * @return 对应的装备类型枚举，未找到则返回UNDEFINED
     */
    public static EquipType getEquipTypeById(int itemid) {
        EquipType ret;
        int val = itemid / 100000;

        if (val == 13 || val == 14) {
            ret = map.get(itemid / 1000);
        } else {
            ret = map.get(itemid / 10000);
        }

        return (ret != null) ? ret : EquipType.UNDEFINED;
    }
}