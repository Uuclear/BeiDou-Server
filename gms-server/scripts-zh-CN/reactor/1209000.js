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
 * 脚本类型：反应堆
 * 对象 ID：1209000
 * 功能描述：地图反应堆交互脚本。
 * 原作者：Ronan
 */
function act() {    // string visibility thanks to ProXAIMeRx & Glvelturall
    if (rm.isQuestStarted(6400)) {
        rm.setQuestProgress(6400, 1, 2);
        rm.setQuestProgress(6400, 6401, "q3");
    }

    rm.message("Real Bart has been found. Return to Jonathan through the portal.");
}