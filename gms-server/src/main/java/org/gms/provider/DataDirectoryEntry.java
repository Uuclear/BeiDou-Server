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

import java.util.List;

/**
 * WZ 目录条目接口，可包含子目录与数据文件，用于构建 WZ 包导航树。
 *
 * @author Matze
 */
public interface DataDirectoryEntry extends DataEntry {
    /** @return 直接子目录列表 */
    List<DataDirectoryEntry> getSubdirectories();

    /** @return 直接子文件列表 */
    List<DataFileEntry> getFiles();

    /**
     * 按名称查找子目录或文件。
     *
     * @param name 条目名称（不含扩展名）
     * @return 匹配的条目，不存在时返回 {@code null}
     */
    DataEntry getEntry(String name);
}
