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
 * WZ文件条目类，继承自WZEntry，实现DataFileEntry接口。
 * 表示WZ文件系统中的一个文件条目（通常对应一个.xml数据文件），
 * 支持设置和获取文件偏移量。
 *
 * @author OdinMS Team
 */
public class WZFileEntry extends WZEntry implements DataFileEntry {
    /** 文件偏移量 */
    private int offset;

    /**
     * 构造WZ文件条目
     * @param name 文件名称
     * @param size 文件大小
     * @param checksum 校验和
     * @param parent 父目录条目
     */
    public WZFileEntry(String name, int size, int checksum, DataEntity parent) {
        super(name, size, checksum, parent);
    }

    /**
     * 获取文件偏移量
     * @return 文件在WZ包中的偏移量
     */
    @Override
    public int getOffset() {
        return offset;
    }

    /**
     * 设置文件偏移量
     * @param offset 文件偏移量
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
