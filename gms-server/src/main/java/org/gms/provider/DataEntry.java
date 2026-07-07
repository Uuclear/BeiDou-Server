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
 * WZ 目录/文件条目的元数据接口（名称、大小、校验和、偏移）。
 * <p>
 * 用于在不解析 XML 的情况下遍历 WZ 包结构；二进制 WZ 格式中偏移量有意义，
 * XML 导出模式下多为占位值 0。
 * </p>
 *
 * @author Matze
 */
public interface DataEntry extends DataEntity {
    @Override
    String getName();

    /** @return 条目字节大小 */
    int getSize();

    /** @return 条目校验和 */
    int getChecksum();

    /** @return 在 WZ 包内的字节偏移（XML 模式下通常为 0） */
    int getOffset();
}
