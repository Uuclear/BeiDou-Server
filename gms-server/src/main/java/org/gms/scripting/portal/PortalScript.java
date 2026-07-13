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
package org.gms.scripting.portal;

/**
 * 传送门脚本接口，定义传送门脚本必须实现的方法。
 * JavaScript传送门脚本通过实现此接口来定义玩家进入传送门时的行为逻辑。
 *
 * @author OdinMS Team
 */
public interface PortalScript {
    /**
     * 玩家进入传送门时调用的方法。
     *
     * @param ppi 传送门玩家交互对象，提供与玩家、地图交互的各种方法
     * @return 如果传送门可以正常进入返回true，否则返回false
     */
    boolean enter(PortalPlayerInteraction ppi);
}