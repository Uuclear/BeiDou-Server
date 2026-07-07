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
 * WZ 数据提供者接口，负责按路径加载 XML 数据并提供目录导航。
 * <p>
 * 典型实现为 {@link org.gms.provider.wz.XMLWZFile}，由 {@link DataProviderFactory} 创建，
 * 可配合 {@link LocalizedDataProvider} 实现语言包回退。
 * </p>
 */
public interface DataProvider {
    /**
     * 按相对路径加载数据文件（不含 {@code .xml} 后缀）。
     *
     * @param path 相对于 WZ 根目录的路径，例如 {@code "Item/Consume/0200.img"}
     * @return 解析后的数据树根节点，文件不存在时返回 {@code null}
     */
    Data getData(String path);

    /** @return WZ 包的根目录条目，用于枚举子目录与文件 */
    DataDirectoryEntry getRoot();
}
