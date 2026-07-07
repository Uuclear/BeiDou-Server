/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
 * 对象 ID：2111014
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
var status;

function start() {
    if (cm.isQuestStarted(3311)) {
        cm.sendOk("The diary of Dr. De Lang. A lot of formulas and pompous scientific texts can be found all way through the pages.", 2);
    }
    cm.dispose();
}