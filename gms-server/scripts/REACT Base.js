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

/*
	反应堆(Reactor)脚本基础模板（BeiDou北斗冒险岛服务端）
	@Author Ronan
	【使用说明】
	- 反应堆是指地图中可交互的物体（如宝箱、机关、任务物品等）
	- 将此文件复制一份，以反应堆ID命名（如1000000.js）放入scripts/reactor目录
	- rm对象：反应堆动作管理器（ReactorActionManager），提供反应堆交互API
	- act()函数：当玩家攻击/点击反应堆触发时执行
 */

/**
 * 反应堆触发动作函数
 * 当玩家与反应堆交互（攻击、点击等）时自动调用
 * 在此编写反应堆被触发后的逻辑（如掉落物品、传送、改变状态等）
 */
function act() {
	// 在此编写反应堆逻辑
	// 示例：rm.dropItems(); // 掉落物品
	// 示例：rm.getMap().killAllMonsters(); // 消灭所有怪物
	// 示例：rm.warp(地图ID); // 传送玩家
}
