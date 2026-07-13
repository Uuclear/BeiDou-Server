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
package org.gms.scripting.item;

import org.gms.client.Client;
import org.gms.scripting.npc.NPCScriptManager;
import org.gms.server.ItemInformationProvider.ScriptedItem;

/**
 * 物品脚本管理器，负责执行物品相关的脚本。
 * 物品脚本用于处理特殊道具（如消耗品、任务物品等）的使用逻辑，
 * 实际委托给NPCScriptManager执行，复用NPC对话系统。
 *
 * @author OdinMS Team
 */
public class ItemScriptManager {

    /**
     * 单例实例
     */
    private static final ItemScriptManager instance = new ItemScriptManager();

    /**
     * 获取单例实例
     *
     * @return ItemScriptManager单例对象
     */
    public static ItemScriptManager getInstance() {
        return instance;
    }

    /**
     * 执行物品脚本
     *
     * @param c 客户端连接对象
     * @param scriptItem 脚本物品信息，包含脚本名称等
     */
    public void runItemScript(Client c, ScriptedItem scriptItem) {
        NPCScriptManager.getInstance().start(c, scriptItem, null);
    }
}