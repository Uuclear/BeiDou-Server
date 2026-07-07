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
package org.gms.server.maps;

/**
 * 保存位置类型枚举（自由市场、世界地图等）。
 */
public enum SavedLocationType {
    FREE_MARKET,
    WORLDTOUR,
    FLORINA,
    INTRO,
    SUNDAY_MARKET,
    MIRROR,
    EVENT,
    BOSSPQ,
    HAPPYVILLE,
    MONSTER_CARNIVAL,
    DEVELOPER,
    JAIL;

    /**
     * 执行 from、String 操作。
     * @param Str Str
     * @return SavedLocationType 类型结果
     */
    public static SavedLocationType fromString(String Str) {
        return valueOf(Str);
    }
}