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

/**
 * WZ数据类型枚举，定义了WZ/XML文件中可能出现的各种数据节点类型。
 * 用于标识Data节点存储的数据类型，对应冒险岛WZ文件中的各种数据格式。
 *
 * @author OdinMS Team
 */
public enum DataType {
    /** 无类型 */
    NONE,
    /** IMG空节点类型（0x00） */
    IMG_0x00,
    /** 短整型（16位整数） */
    SHORT,
    /** 整型（32位整数） */
    INT,
    /** 单精度浮点数 */
    FLOAT,
    /** 双精度浮点数 */
    DOUBLE,
    /** 字符串类型 */
    STRING,
    /** 扩展类型 */
    EXTENDED,
    /** 属性/目录节点类型（imgdir） */
    PROPERTY,
    /** 画布/图片类型，包含宽高信息 */
    CANVAS,
    /** 二维向量类型，包含x、y坐标 */
    VECTOR,
    /** 凸多边形类型 */
    CONVEX,
    /** 声音类型 */
    SOUND,
    /** UOL（引用链接）类型 */
    UOL,
    /** 未知类型 */
    UNKNOWN_TYPE,
    /** 未知扩展类型 */
    UNKNOWN_EXTENDED_TYPE
}