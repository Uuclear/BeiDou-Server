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
 * 物品脚本入口管理器（单例），将脚本化物品委托给 {@link NPCScriptManager} 执行。
 * <p>
 * 物品 WZ 中配置的脚本名对应 {@code scripts/item/{script}.js}，引擎变量名为 {@code im}。
 * </p>
 */
public class ItemScriptManager {

    private static final ItemScriptManager instance = new ItemScriptManager();

    /** 获取单例实例 */
    public static ItemScriptManager getInstance() {
        return instance;
    }

    /** 执行物品脚本，委托 NPC 脚本管理器以 {@code im} 变量加载 item 脚本 */
    public void runItemScript(Client c, ScriptedItem scriptItem) {
        NPCScriptManager.getInstance().start(c, scriptItem, null);
    }
}