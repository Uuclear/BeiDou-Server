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
 * 角色名称和ID数据类
 * 用于存储角色的基本标识信息（ID和名称）
 * 主要在加载角色列表时使用
 *
 * @author OdinMS Team
 */
public class CharacterNameAndId {
    /** 角色ID */
    private final int id;
    /** 角色名称 */
    private final String name;

    /**
     * 构造函数
     * @param id 角色ID
     * @param name 角色名称
     */
    public CharacterNameAndId(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    /**
     * 获取角色ID
     * @return 角色ID
     */
    public int getId() {
        return id;
    }

    /**
     * 获取角色名称
     * @return 角色名称
     */
    public String getName() {
        return name;
    }
}
