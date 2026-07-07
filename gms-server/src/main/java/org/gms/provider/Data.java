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
 * WZ/XML 数据树节点接口，表示 MapleStory 客户端资源中的一条数据记录。
 * <p>
 * 服务端通过 {@link DataProvider} 加载 WZ 导出的 XML，以本接口统一访问物品、技能、地图等配置节点。
 * 节点可按路径导航子节点，并读取标量值或属性。
 * </p>
 */
public interface Data extends DataEntity, Iterable<Data> {
    @Override
    String getName();

    /** @return 当前节点的 WZ 数据类型（如 INT、STRING、PROPERTY 等） */
    DataType getType();

    /** @return 直接子节点列表 */
    List<Data> getChildren();

    /**
     * 按斜杠分隔路径查找子节点，例如 {@code "info/price"}。
     *
     * @param path 相对当前节点的路径，支持 {@code ..} 回退父节点
     * @return 匹配的子节点，不存在时返回 {@code null}
     */
    Data getChildByPath(String path);

    /** @return 叶子节点的实际值（数值、字符串、{@link java.awt.Point} 等），目录节点通常为 {@code null} */
    Object getData();

    /**
     * 读取 XML 节点上的属性值。
     *
     * @param name 属性名
     * @return 属性值，不存在时返回 {@code null}
     */
    String getAttributeValue(String name);
}
