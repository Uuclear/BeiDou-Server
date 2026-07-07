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
 * 脚本类型：地图
 * 对象 ID：dojang_Msg
 * 功能描述：地图脚本（dojang_Msg），控制地图内特殊逻辑。
 * 原作者：Traitor
 */
var messages = Array("Your courage for challenging the Mu Lung Dojo is commendable!", "If you want to taste the bitterness of defeat, come on in!", "I will make you thoroughly regret challenging the Mu Lung Dojo! Hurry up!");

function start(ms) {
    if (ms.getPlayer().getMap().getId() == 925020000) {
        if (ms.getPlayer().getMap().findClosestPlayerSpawnpoint(ms.getPlayer().getPosition()).getId() == 0) {
            ms.getPlayer().startMapEffect(messages[(Math.random() * messages.length) | 0], 5120024);
        }

        ms.resetDojoEnergy();
    } else {
        ms.getPlayer().resetEnteredScript(); //in case the person dcs in here we set it at dojang_tuto portal
        ms.getPlayer().startMapEffect("Ha! Let's see what you got! I won't let you leave unless you defeat me first!", 5120024);
    }
}
