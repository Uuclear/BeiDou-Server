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

import org.gms.provider.wz.DataType;

import java.util.List;

/**
 * 数据节点接口，表示WZ/XML数据文件中的一个数据节点。
 * 数据节点可以包含子节点，支持通过路径访问子节点，并可获取节点的数据值和属性。
 * 这是解析冒险岛WZ资源文件的核心数据结构之一。
 *
 * @author OdinMS Team
 */
public interface Data extends DataEntity, Iterable<Data> {
    /**
     * 获取数据节点的名称
     * @return 节点名称
     */
    @Override
    String getName();

    /**
     * 获取数据节点的数据类型
     * @return 数据类型（如INT、STRING、VECTOR、CANVAS等）
     */
    DataType getType();

    /**
     * 获取该节点的所有直接子节点
     * @return 子节点列表
     */
    List<Data> getChildren();

    /**
     * 根据路径获取子节点，支持使用"/"分隔的多级路径和".."返回上级
     * @param path 子节点路径，如"info/icon"或"../parent"
     * @return 找到的子节点，如果不存在则返回null
     */
    Data getChildByPath(String path);

    /**
     * 获取该节点存储的数据值，具体类型由getType()决定
     * @return 数据对象（可能是Integer、String、Point、Double等）
     */
    Object getData();

    /**
     * 获取节点的指定XML属性值
     * @param name 属性名称
     * @return 属性值，如果不存在则返回null
     */
    String getAttributeValue(String name);
}
