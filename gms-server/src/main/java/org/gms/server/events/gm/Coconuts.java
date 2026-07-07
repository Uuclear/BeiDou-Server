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
 * 椰子活动（多人）。
 */
public class Coconuts {
    private final int id;
    private int hits = 0;
    private boolean hittable = false;
    private long hittime = System.currentTimeMillis();

    /**
     * 构造 Coconuts 实例。
     * @param id ID
     */
    public Coconuts(int id) {
        this.id = id;
    }

    /**
     * 执行 hit 操作。
     */
    public void hit() {
        this.hittime = System.currentTimeMillis() + 750;
        hits++;
    }

    /**
     * 获取Hits。
     * @return int 类型结果
     */
    public int getHits() {
        return hits;
    }

    /**
     * 重置Hits。
     */
    public void resetHits() {
        hits = 0;
    }

    /**
     * 判断是否为Hittable。
     * @return boolean 类型结果
     */
    public boolean isHittable() {
        return hittable;
    }

    /**
     * 设置Hittable。
     * @param hittable hittable
     */
    public void setHittable(boolean hittable) {
        this.hittable = hittable;
    }

    /**
     * 获取Hit时间。
     * @return long 类型结果
     */
    public long getHitTime() {
        return hittime;
    }
}
