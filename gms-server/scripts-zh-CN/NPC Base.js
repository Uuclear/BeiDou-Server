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
	NPC脚本基础模板（BeiDou北斗冒险岛服务端 - 简体中文版本）
	【使用说明】
	- 将此文件复制一份，以NPC ID命名（如1000000.js）放入scripts-zh-CN/npc目录
	- 修改下方示例文本为实际NPC对话内容（中文）
	- cm对象：NPC对话管理器（NPCConversationManager），提供所有NPC交互API
	- start()：对话开始入口函数
	- action(mode, type, selection)：处理玩家对话选择的回调函数
		参数说明：
			mode: 玩家操作模式（1=确认/下一项，0=否/返回，-1=关闭对话）
			type: 对话类型
			selection: 选项索引（用于选择菜单）
 */

/**
 * 对话状态变量
 * 用于跟踪当前对话进行到哪一步
 */
var status;

/**
 * NPC对话开始函数
 * 当玩家点击NPC时自动调用
 * 初始化状态并启动对话流程
 */
function start() {
    status = -1;
    action(1, 0, 0);
}

/**
 * 处理玩家对话动作的核心函数
 * @param {number} mode - 玩家操作模式：1=确认/下一个，0=否/返回上一个，-1=关闭对话
 * @param {number} type - 对话类型
 * @param {number} selection - 选择的选项索引（当显示选择菜单时使用）
 */
function action(mode, type, selection) {
    if (mode == -1) {
        // 玩家关闭对话，释放资源
        cm.dispose();
    } else {
        if (mode == 0 && type > 0) {
            // 玩家选择"否"且不是最后一步，结束对话
            cm.dispose();
            return;
        }
        if (mode == 1) {
            // 玩家点击确认/下一个，状态+1进入下一段对话
            status++;
        } else {
            // 玩家点击返回，状态-1回到上一段对话
            status--;
        }

        if (status == 0) {
            // 第一步对话：显示示例文本
            cm.sendOk("Sample text.");
            cm.dispose();
        }
    }
}

/**
 * 工具函数：生成选择菜单字符串
 * 用于cm.sendSimple()等方法，将字符串数组转换为冒险岛对话链接格式
 * @param {Array<string>} array - 选项文本数组
 * @returns {string} 格式化后的菜单字符串，可直接用于对话函数
 */
function generateSelectionMenu(array) {
    var menu = "";
    for (var i = 0; i < array.length; i++) {
        // #L数字#选项文本#l 是冒险岛客户端的链接语法
        menu += "#L" + i + "#" + array[i] + "#l\r\n";
    }
    return menu;
}
