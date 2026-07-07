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
 * 脚本类型：NPC
 * 对象 ID：2040027
 * 功能描述：Fourth Eos Rock，Brings you to 41st Floor（原版描述）。
 * 原作者：Xterminator, Moogra
 */
function start() {
    if (cm.haveItem(4001020)) {
        cm.sendYesNo("您可以使用#b#t4001020##k来激活#b#p2040027##k。您要前往第41层的#b#p2040026##k吗？");
    } else {
        cm.sendOk("有一块魔法石可以让你传送到#b#p2040026##k，但如果没有卷轴就无法激活。");
        cm.dispose();
    }
}

function action(mode, type, selection) {
    if (mode < 1) {
    } else {
        cm.gainItem(4001020, -1);
        cm.warp(221021700, 3);
    }
    cm.dispose();
}