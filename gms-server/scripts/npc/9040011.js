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
 * 对象 ID：9040011
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
function start() {
    cm.sendOk("<Notice> \r\n Are you part of a Guild that possesses an ample amount of courage and trust? Then take on the Guild Quest and challenge yourselves!\r\n\r\n#bTo Participate :#k\r\n1. The Guild must consist of at least 6 people!\r\n2. The leader of the Guild Quest must be a Master or a Jr. Master of the Guild!\r\n3. The Guild Quest may end early if the number of guild members participating falls below 6, or if the leader decides to end it early!");
    cm.dispose();
}