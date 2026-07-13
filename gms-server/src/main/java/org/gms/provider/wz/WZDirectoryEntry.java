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

import org.gms.provider.DataDirectoryEntry;
import org.gms.provider.DataEntity;
import org.gms.provider.DataEntry;
import org.gms.provider.DataFileEntry;

import java.util.*;

/**
 * WZ目录条目类，继承自WZEntry，实现DataDirectoryEntry接口。
 * 表示WZ文件系统中的一个目录，可以包含子目录和文件，
 * 提供添加子目录/文件、获取子目录列表、获取文件列表、按名称查找条目等功能。
 *
 * @author OdinMS Team
 */
public class WZDirectoryEntry extends WZEntry implements DataDirectoryEntry {
    /** 子目录列表 */
    private final List<DataDirectoryEntry> subdirs = new ArrayList<>();
    /** 文件列表 */
    private final List<DataFileEntry> files = new ArrayList<>();
    /** 条目名称到条目的映射，用于快速查找 */
    private final Map<String, DataEntry> entries = new HashMap<>();

    /**
     * 构造WZ目录条目
     * @param name 目录名称
     * @param size 目录大小
     * @param checksum 校验和
     * @param parent 父目录条目
     */
    public WZDirectoryEntry(String name, int size, int checksum, DataEntity parent) {
        super(name, size, checksum, parent);
    }

    /**
     * 构造根WZ目录条目（无名称、无父目录）
     */
    public WZDirectoryEntry() {
        super(null, 0, 0, null);
    }

    /**
     * 添加子目录到当前目录
     * @param dir 子目录条目
     */
    public void addDirectory(DataDirectoryEntry dir) {
        subdirs.add(dir);
        entries.put(dir.getName(), dir);
    }

    /**
     * 添加文件到当前目录
     * @param fileEntry 文件条目
     */
    public void addFile(DataFileEntry fileEntry) {
        files.add(fileEntry);
        entries.put(fileEntry.getName(), fileEntry);
    }

    /**
     * 获取所有子目录（返回不可修改列表）
     * @return 子目录条目列表
     */
    public List<DataDirectoryEntry> getSubdirectories() {
        return Collections.unmodifiableList(subdirs);
    }

    /**
     * 获取所有文件（返回不可修改列表）
     * @return 文件条目列表
     */
    public List<DataFileEntry> getFiles() {
        return Collections.unmodifiableList(files);
    }

    /**
     * 根据名称获取条目（可以是文件或子目录）
     * @param name 条目名称
     * @return 找到的条目，不存在则返回null
     */
    public DataEntry getEntry(String name) {
        return entries.get(name);
    }
}
