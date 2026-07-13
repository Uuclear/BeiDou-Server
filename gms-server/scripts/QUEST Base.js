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
	任务脚本基础模板（BeiDou北斗冒险岛服务端）
	【使用说明】
	- 将此文件复制一份，以任务ID命名（如100000.js）放入scripts/quest目录
	- start()函数处理任务开始（接任务）流程
	- end()函数处理任务完成（交任务）流程
	- qm对象：任务动作管理器（QuestActionManager），提供所有任务相关API
		常用API：qm.sendNext()、qm.sendYesNo()、qm.forceStartQuest()、qm.forceCompleteQuest()、qm.gainExp()、qm.gainMeso()、qm.gainItem()等
		参数说明（mode, type, selection）：
			mode: 玩家操作模式（1=确认，0=否，-1=关闭对话）
			type: 对话类型
			selection: 选项索引
 */

/**
 * 对话状态变量
 */
var status = -1;

/**
 * 任务开始处理函数（接任务）
 * 当玩家点击NPC接取任务时调用
 * @param {number} mode - 玩家操作模式
 * @param {number} type - 对话类型
 * @param {number} selection - 选择索引
 */
function start(mode, type, selection) {
    if (mode == -1) {
        // 玩家关闭对话
        qm.dispose();
    } else {
        if (mode == 0 && type > 0) {
            // 玩家选择"否"，结束对话
            qm.dispose();
            return;
        }

        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            // 第一步：显示任务介绍文本
            qm.sendNext("Sample Text.");
        } else if (status == 1) {
            // 第二步：强制开始任务（接取成功）
            qm.forceStartQuest();
            qm.dispose();
        }
    }
}

/**
 * 任务完成处理函数（交任务）
 * 当玩家完成任务后与NPC对话提交任务时调用
 * @param {number} mode - 玩家操作模式
 * @param {number} type - 对话类型
 * @param {number} selection - 选择索引
 */
function end(mode, type, selection) {
    if (mode == -1) {
        // 玩家关闭对话
        qm.dispose();
    } else {
        if (mode == 0 && type > 0) {
            // 玩家选择"否"，结束对话
            qm.dispose();
            return;
        }

        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            // 第一步：显示任务完成对话
            qm.sendNext("Sample Text.");
        } else if (status == 1) {
            // 第二步：强制完成任务，发放奖励
            qm.forceCompleteQuest();
            qm.dispose();
        }
    }
}
