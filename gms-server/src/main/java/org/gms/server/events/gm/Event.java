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

package org.gms.server.events.gm;

/**
 * GM 活动事件基类。
 */
public class Event {
    private final int mapid;
    private int limit;

    /**
     * 构造 Event 实例。
     * @param mapid 地图 ID
     * @param limit limit
     */
    public Event(int mapid, int limit) {
        this.mapid = mapid;
        this.limit = limit;
    }

    /**
     * 获取地图ID。
     * @return int 类型结果
     */
    public int getMapId() {
        return mapid;
    }

    /**
     * 获取限制。
     * @return int 类型结果
     */
    public int getLimit() {
        return limit;
    }

    /**
     * 执行 minus、限制 操作。
     */
    public void minusLimit() {
        this.limit--;
    }

    /**
     * 添加限制。
     */
    public void addLimit() {
        this.limit++;
    }
}  