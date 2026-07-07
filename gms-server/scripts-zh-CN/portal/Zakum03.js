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


/**
 * 脚本类型：传送门
 * 对象 ID：Zakum03
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
/*
    Zakum PQ portal
*/

function enter(pi) {
    if (!pi.getEventInstance().isEventCleared()) {
        pi.getPlayer().dropMessage(5, "你的队伍尚未完成试炼，请先完成奥拉的需求。");
        return false;
    }

    if (pi.getEventInstance().gridCheck(pi.getPlayer()) == -1) {
        pi.getPlayer().dropMessage(5, "你还没有领取战利品，请先与Aura交谈。");
        return false;
    }

    pi.playPortalSound();
    pi.warp(211042300);
    return true;
}