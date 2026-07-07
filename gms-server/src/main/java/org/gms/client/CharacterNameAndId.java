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
package org.gms.client;

/**
 * 角色名称与 ID 的简单数据对，用于列表展示和查询。
 */
public class CharacterNameAndId {
    private final int id;
    private final String name;

    /**
     * 角色名称AndID
     * @param id ID
     * @param name 名称
     */
    public CharacterNameAndId(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    /**
     * 获取ID
     * @return 返回值
     */
    public int getId() {
        return id;
    }

    /**
     * 获取名称
     * @return 返回值
     */
    public String getName() {
        return name;
    }
}
