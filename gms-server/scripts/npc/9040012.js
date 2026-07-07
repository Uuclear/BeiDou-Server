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
 * 对象 ID：9040012
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 * 原作者：Lerk
 */
function start() {
    cm.sendOk("The plaque translates as follows: \r\n\"The knights of Sharenian are proud warriors. Their Longinus Spears are both formidable weapons and the key to the castle's defense: By removing them from their platforms at the highest points of this hall, they block off the entrance from invaders.\"\r\n\r\nSomething seems to be etched in English on the side, barely readable: \r\n\"evil stole spears, chained up behind obstacles. return to top of towers. large spear, grab from higher up.\"\r\n...Obviously whoever figured it out didn't have much time to live. Poor guy.");
    cm.dispose();
}