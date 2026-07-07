/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/




/**
 * 脚本类型：传送门
 * 对象 ID：met_out
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 * 原作者：kevintjuh93
 */
function enter(pi) {
    var mapId = pi.getPlayer().getSavedLocation("MIRROR");

    pi.playPortalSound();
    if (mapId == -1) {
        pi.warp(102040000, 12);
    } else {
        pi.warp(mapId);
    }

    //pi.warp(102040000, 12);
    return true;
}