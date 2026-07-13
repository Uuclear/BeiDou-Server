/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

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
package org.gms.client.inventory;

import lombok.Getter;
import org.gms.util.I18nUtil;

/**
 * 背包类型枚举类
 * 定义了游戏中各种背包类型及其ID和名称
 * 包括装备、消耗、设置、其他、现金等背包分类
 *
 * @author Matze
 */
@Getter
public enum InventoryType {
    /** 未定义 */
    UNDEFINED(0, I18nUtil.getMessage("InventoryType.UNDEFINED")),
    /** 装备栏 */
    EQUIP(1, I18nUtil.getMessage("InventoryType.EQUIP")),
    /** 消耗栏 */
    USE(2, I18nUtil.getMessage("InventoryType.USE")),
    /** 设置栏 */
    SETUP(3, I18nUtil.getMessage("InventoryType.SETUP")),
    /** 其他栏 */
    ETC(4, I18nUtil.getMessage("InventoryType.ETC")),
    /** 现金栏 */
    CASH(5, I18nUtil.getMessage("InventoryType.CASH")),
    /** 可持有验证栏（用于移除物品后的插入检查） */
    CANHOLD(6, I18nUtil.getMessage("InventoryType.CANHOLD")),
    /** 已装备栏（Nexon移除物品时的特殊处理） */
    EQUIPPED(-1, I18nUtil.getMessage("InventoryType.EQUIPPED"));

    /** 背包类型ID */
    private final byte type;
    /** 背包类型名称（支持国际化） */
    private final String name;

    /**
     * 构造函数
     * @param type 类型ID
     * @param name 类型名称
     */
    InventoryType(int type, String name) {
        this.type = (byte) type;
        this.name = name;
    }

    /**
     * 获取位域编码值
     * @return 位域编码
     */
    public short getBitfieldEncoding() {
        return (short) (2 << type);
    }

    /**
     * 根据类型ID获取背包类型枚举
     * @param type 类型ID
     * @return 对应的InventoryType枚举
     */
    public static InventoryType getByType(byte type) {
        for (InventoryType l : InventoryType.values()) {
            if (l.getType() == type) {
                return l;
            }
        }
        return UNDEFINED;
    }

    /**
     * 根据WZ文件中的名称获取背包类型
     * @param name WZ名称
     * @return 对应的InventoryType枚举
     */
    public static InventoryType getByWZName(String name) {
        return switch (name) {
            case "Install" -> SETUP;
            case "Consume" -> USE;
            case "Etc" -> ETC;
            case "Cash" -> CASH;
            case "Pet" -> CASH;
            default -> UNDEFINED;
        };
    }

    /**
     * 判断是否可以修改该背包的最大槽位数
     * @return 是否可以修改
     */
    public boolean canChangeSlotMax() {
        return this == USE || this == ETC;
    }

    /**
     * 判断是否为装备类型（包括已装备）
     * @return 是否为装备
     */
    public boolean isEquip() {
        return this == EQUIP || this == EQUIPPED;
    }
}
