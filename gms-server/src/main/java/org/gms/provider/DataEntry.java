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
 * 数据条目接口，表示WZ文件系统中的一个条目（文件或目录）。
 * 继承自DataEntity，提供文件大小、校验和、偏移量等元数据信息。
 *
 * @author Matze
 */
public interface DataEntry extends DataEntity {
    /**
     * 获取条目的名称
     * @return 条目名称
     */
    String getName();

    /**
     * 获取条目数据的大小（字节数）
     * @return 数据大小
     */
    int getSize();

    /**
     * 获取条目数据的校验和，用于验证数据完整性
     * @return 校验和值
     */
    int getChecksum();

    /**
     * 获取条目在WZ文件中的偏移量位置
     * @return 文件偏移量
     */
    int getOffset();
}
