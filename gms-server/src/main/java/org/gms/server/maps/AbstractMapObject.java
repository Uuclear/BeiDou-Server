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
package org.gms.server.maps;

import java.awt.*;

/**
 * 地图对象抽象基类，提供 OID、位置、地图引用等通用实现。
 */
public abstract class AbstractMapObject implements MapObject {
    private Point position = new Point();
    private int objectId;

    /**
     * 获取类型。
     * @return abstract MapObjectType 类型结果
     */
    @Override
    public abstract MapObjectType getType();

    /**
     * 获取位置。
     * @return Point 类型结果
     */
    @Override
    public Point getPosition() {
        return new Point(position);
    }

    /**
     * 设置位置。
     * @param position 坐标
     */
    @Override
    public void setPosition(Point position) {
        this.position.move(position.x, position.y);
    }

    /**
     * 获取对象ID。
     * @return int 类型结果
     */
    @Override
    public int getObjectId() {
        return objectId;
    }

    /**
     * 设置对象ID。
     * @param id ID
     */
    @Override
    public void setObjectId(int id) {
        this.objectId = id;
    }

    /**
     * 执行 nullify、位置 操作。
     */
    @Override
    public void nullifyPosition() {
        this.position = null;
    }
}
