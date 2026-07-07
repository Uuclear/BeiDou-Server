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
package org.gms.provider.wz;

import org.gms.provider.DataEntity;
import org.gms.provider.DataFileEntry;

/**
 * WZ 数据文件条目，对应一个 {@code .img} 资源（XML 模式下为 {@code .xml} 文件）。
 */
public class WZFileEntry extends WZEntry implements DataFileEntry {
    private int offset;

    public WZFileEntry(String name, int size, int checksum, DataEntity parent) {
        super(name, size, checksum, parent);
    }

    @Override
/** 获取 WZ 文件内偏移量 */
    public int getOffset() {
        return offset;
    }

/** 设置 WZ 文件内偏移量 */
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
