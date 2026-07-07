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

import org.gms.client.Client;

import java.awt.*;

/**
 * 传送门接口，定义进入/离开地图的传送行为。
 */
public interface Portal {
    int TELEPORT_PORTAL = 1;
    int MAP_PORTAL = 2;
    int DOOR_PORTAL = 6;
    boolean OPEN = true;
    boolean CLOSED = false;
    /**
     * 获取传送门类型。
     * @return int
     */
    int getType();
    /**
     * 获取传送门 ID。
     * @return int
     */
    int getId();
    /**
     * 获取传送门坐标。
     * @return Point
     */
    Point getPosition();
    /**
     * 获取传送门名称。
     * @return String
     */
    String getName();
    /**
     * 获取目标传送门名称。
     * @return String
     */
    String getTarget();
    String getScriptName();
    void setScriptName(String newName);
    void setPortalStatus(boolean newStatus);
    boolean getPortalStatus();
    /**
     * 获取目标地图 ID。
     * @return int
     */
    int getTargetMapId();
    /**
     * 角色进入传送门，执行传送逻辑。
     * @param c 参数
     * @return boolean
     */
    void enterPortal(Client c);
    void setPortalState(boolean state);
    boolean getPortalState();
}
