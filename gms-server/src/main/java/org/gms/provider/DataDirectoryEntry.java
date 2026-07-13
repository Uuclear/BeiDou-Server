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
 * 数据目录条目接口，表示WZ文件系统中的一个目录条目。
 * 继承自DataEntry，提供访问子目录、文件和按名称获取条目的能力。
 *
 * @author Matze
 */
public interface DataDirectoryEntry extends DataEntry {
    /**
     * 获取该目录下的所有子目录
     * @return 子目录条目列表
     */
    List<DataDirectoryEntry> getSubdirectories();

    /**
     * 获取该目录下的所有文件
     * @return 文件条目列表
     */
    List<DataFileEntry> getFiles();

    /**
     * 根据名称获取该目录下的条目（可以是文件或子目录）
     * @param name 条目名称
     * @return 找到的条目，如果不存在则返回null
     */
    DataEntry getEntry(String name);
}
