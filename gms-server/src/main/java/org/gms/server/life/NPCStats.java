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
package org.gms.server.life;

/**
 * NPC 静态属性（名称、脚本、功能标志等）。
 */
public class NPCStats {
    private String name;

    /**
     * 构造 NPCStats 实例。
     * @param name name
     */
    public NPCStats(String name) {
        this.name = name;
    }

    /**
     * 获取名称。
     * @return String 类型结果
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称。
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }
}
