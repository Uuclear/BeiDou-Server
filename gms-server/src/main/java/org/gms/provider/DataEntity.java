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
package org.gms.provider;

/**
 * 数据实体接口，是所有WZ数据节点和目录/文件条目的基础接口。
 * 提供获取实体名称和父实体的基本功能，构成WZ资源文件树状结构的基础。
 *
 * @author Matze
 */
public interface DataEntity {
    /**
     * 获取数据实体的名称
     * @return 实体名称
     */
    String getName();

    /**
     * 获取该实体的父实体
     * @return 父实体，如果是根节点则返回null
     */
    DataEntity getParent();
}
