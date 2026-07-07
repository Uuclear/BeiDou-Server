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
 * 对象 ID：q2073
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
/**
 * @author Twdtwd
 * @purpose Warps to Utah's Pig Farm for the quest Camila's Gem.
 */
function enter(pi) {
    if (pi.isQuestStarted(2073)) {
        pi.playPortalSound();
        pi.warp(900000000, 0);
        return true;
    } else {
        pi.message("Private property. This place can only be entered when running an errand from Camila.");
        return false;
    }
}