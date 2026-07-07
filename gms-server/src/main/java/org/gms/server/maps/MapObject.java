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

import org.gms.client.Client;

import java.awt.*;

/**
 * 地图对象接口，所有可放置在地图上的实体（怪物、NPC、掉落物等）的公共契约。
 */
public interface MapObject {
    /**
     * 获取地图对象唯一 OID。
     * @return int
     */
    int getObjectId();
    /**
     * 设置地图对象 OID。
     * @param id 参数
     */
    void setObjectId(int id);
    /**
     * 获取地图对象类型。
     * @return MapObjectType
     */
    MapObjectType getType();
    /**
     * 获取对象在地图上的坐标。
     * @return Point
     */
    Point getPosition();
    /**
     * 设置对象在地图上的坐标。
     * @param position 参数
     */
    void setPosition(Point position);
    /**
     * 向指定客户端发送对象生成数据包。
     * @param client 参数
     */
    void sendSpawnData(Client client);
    /**
     * 向指定客户端发送对象销毁数据包。
     * @param client 参数
     */
    void sendDestroyData(Client client);
    /**
     * 清空对象位置（对象离开地图时调用）。
     */
    void nullifyPosition();
}