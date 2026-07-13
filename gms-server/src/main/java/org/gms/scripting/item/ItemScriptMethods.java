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
 * 物品脚本方法类，继承自AbstractPlayerInteraction，
 * 为物品脚本提供与玩家交互的方法。目前作为基础类存在，
 * 可扩展添加物品脚本专用的交互方法。
 *
 * @author kevintjuh93
 */
public class ItemScriptMethods extends AbstractPlayerInteraction {
    /**
     * 构造物品脚本方法对象
     *
     * @param c 客户端连接对象
     */
    public ItemScriptMethods(Client c) {
        super(c);
    }
}
