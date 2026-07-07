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
import org.gms.provider.DataEntry;

/**
 * WZ 条目基类，保存名称、大小、校验和及父节点引用。
 */
public class WZEntry implements DataEntry {
    private final String name;
    private final int size;
    private final int checksum;
    private int offset;
    private final DataEntity parent;

    public WZEntry(String name, int size, int checksum, DataEntity parent) {
        super();
        this.name = name;
        this.size = size;
        this.checksum = checksum;
        this.parent = parent;
    }

/** 获取事件实例名称 */
    public String getName() {
        return name;
    }

/** 获取Size */
    public int getSize() {
        return size;
    }

/** 获取Checksum */
    public int getChecksum() {
        return checksum;
    }

/** 获取 WZ 文件内偏移量 */
    public int getOffset() {
        return offset;
    }

/** 获取父节点 */
    public DataEntity getParent() {
        return parent;
    }
}
