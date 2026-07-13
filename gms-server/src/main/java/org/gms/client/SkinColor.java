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
package org.gms.client;

/**
 * 皮肤颜色枚举类
 * 定义了游戏中角色可用的各种皮肤颜色及其ID
 *
 * @author OdinMS Team
 */
public enum SkinColor {
    /** 普通肤色 */
    NORMAL(0),
    /** 黝黑肤色 */
    DARK(1),
    /** 黑色肤色 */
    BLACK(2),
    /** 苍白肤色 */
    PALE(3),
    /** 蓝色肤色 */
    BLUE(4),
    /** 绿色肤色 */
    GREEN(5),
    /** 白色肤色 */
    WHITE(9),
    /** 粉色肤色 */
    PINK(10);

    /** 皮肤颜色ID */
    final int id;

    /**
     * 构造函数
     * @param id 皮肤颜色ID
     */
    SkinColor(int id) {
        this.id = id;
    }

    /**
     * 获取皮肤颜色ID
     * @return 皮肤颜色ID
     */
    public int getId() {
        return id;
    }

    /**
     * 根据ID获取对应的皮肤颜色枚举
     * @param id 皮肤颜色ID
     * @return 对应的SkinColor枚举，如果未找到则返回null
     */
    public static SkinColor getById(int id) {
        for (SkinColor l : SkinColor.values()) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }
}
