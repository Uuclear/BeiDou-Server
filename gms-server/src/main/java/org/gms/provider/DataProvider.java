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
 * 数据提供者接口，负责提供WZ/XML资源数据的访问。
 * 是WZ资源解析系统的核心接口，支持按路径获取数据节点和获取根目录条目。
 *
 * @author OdinMS Team
 */
public interface DataProvider {
    /**
     * 根据指定路径获取数据节点
     * @param path 数据路径，如"Map/Map/000000000.img"
     * @return 对应的数据节点，如果不存在则返回null
     */
    Data getData(String path);

    /**
     * 获取数据提供者的根目录条目，用于遍历整个资源文件结构
     * @return 根目录条目
     */
    DataDirectoryEntry getRoot();
}
