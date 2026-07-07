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
 * 肤色枚举，定义角色创建时可选的肤色类型。
 */
public enum SkinColor {
    NORMAL(0),
    DARK(1),
    BLACK(2),
    PALE(3),
    BLUE(4),
    GREEN(5),
    WHITE(9),
    PINK(10);

    final int id;

    SkinColor(int id) {
        this.id = id;
    }

    /**
     * 获取ID
     * @return 返回值
     */
    public int getId() {
        return id;
    }

    /**
     * 获取按ID
     * @param id ID
     * @return 返回值
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
