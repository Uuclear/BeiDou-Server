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
import org.gms.scripting.AbstractPlayerInteraction;

/**
 * 物品脚本 API 占位类，继承 {@link AbstractPlayerInteraction}。
 * <p>
 * 物品脚本实际由 {@link org.gms.scripting.npc.NPCScriptManager} 以 {@code im} 变量注入
 * {@link org.gms.scripting.npc.NPCConversationManager} 执行；本类保留供独立物品脚本扩展。
 * </p>
 *
 * @author kevintjuh93
 */
public class ItemScriptMethods extends AbstractPlayerInteraction {
    /**
     * @param c 当前玩家客户端
     */
    public ItemScriptMethods(Client c) {
        super(c);
    }
}
