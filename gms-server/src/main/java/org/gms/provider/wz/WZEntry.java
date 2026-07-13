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
 * WZ条目基类，实现了DataEntry接口，表示WZ文件系统中的一个条目。
 * 存储条目的基本元数据：名称、大小、校验和、偏移量以及父条目引用。
 * 是WZFileEntry和WZDirectoryEntry的父类。
 *
 * @author OdinMS Team
 */
public class WZEntry implements DataEntry {
    /** 条目名称 */
    private final String name;
    /** 条目数据大小 */
    private final int size;
    /** 条目数据校验和 */
    private final int checksum;
    /** 条目在文件中的偏移量 */
    private int offset;
    /** 父条目引用 */
    private final DataEntity parent;

    /**
     * 构造WZ条目
     * @param name 条目名称
     * @param size 数据大小
     * @param checksum 校验和
     * @param parent 父条目
     */
    public WZEntry(String name, int size, int checksum, DataEntity parent) {
        super();
        this.name = name;
        this.size = size;
        this.checksum = checksum;
        this.parent = parent;
    }

    /**
     * 获取条目名称
     * @return 条目名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取数据大小
     * @return 数据大小（字节）
     */
    public int getSize() {
        return size;
    }

    /**
     * 获取校验和
     * @return 校验和值
     */
    public int getChecksum() {
        return checksum;
    }

    /**
     * 获取文件偏移量
     * @return 偏移量
     */
    public int getOffset() {
        return offset;
    }

    /**
     * 获取父条目
     * @return 父数据实体
     */
    public DataEntity getParent() {
        return parent;
    }
}
