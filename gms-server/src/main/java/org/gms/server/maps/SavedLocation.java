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
 * 角色保存位置记录（用于回归技能等）。
 */
public class SavedLocation {
    private final int mapId;
    private final int portal;

    /**
     * 构造 SavedLocation 实例。
     * @param mapId mapId
     * @param portal portal
     */
    public SavedLocation(int mapId, int portal) {
        this.mapId = mapId;
        this.portal = portal;
    }

    /**
     * 获取地图ID。
     * @return int 类型结果
     */
    public int getMapId() {
        return mapId;
    }

    /**
     * 获取传送门。
     * @return int 类型结果
     */
    public int getPortal() {
        return portal;
    }
}
