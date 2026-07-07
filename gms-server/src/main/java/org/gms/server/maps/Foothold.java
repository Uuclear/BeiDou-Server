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
 * 地图 foothold（落脚点）线段，用于碰撞检测与垂直移动。
 */
public class Foothold implements Comparable<Foothold> {
    private final Point p1;
    private final Point p2;
    private final int id;
    private int next, prev;

    /**
     * 构造 Foothold 实例。
     * @param p1 p1
     * @param p2 p2
     * @param id ID
     */
    public Foothold(Point p1, Point p2, int id) {
        this.p1 = p1;
        this.p2 = p2;
        this.id = id;
    }

    /**
     * 判断是否为Wall。
     * @return boolean 类型结果
     */
    public boolean isWall() {
        return p1.x == p2.x;
    }

    /**
     * 获取X1。
     * @return int 类型结果
     */
    public int getX1() {
        return p1.x;
    }

    /**
     * 获取X2。
     * @return int 类型结果
     */
    public int getX2() {
        return p2.x;
    }

    /**
     * 获取Y1。
     * @return int 类型结果
     */
    public int getY1() {
        return p1.y;
    }

    /**
     * 获取Y2。
     * @return int 类型结果
     */
    public int getY2() {
        return p2.y;
    }

    // XXX may need more precision
    /**
     * 执行 calculate、Footing 操作。
     * @param x x
     * @return int 类型结果
     */
    public int calculateFooting(int x) {
        if (p1.y == p2.y) {
            return p2.y; // y at both ends is the same
        }
        int slope = (p1.y - p2.y) / (p1.x - p2.x);
        int intercept = p1.y - (slope * p1.x);
        return (slope * x) + intercept;
    }

    /**
     * 执行 compare、到 操作。
     * @param o o
     * @return int 类型结果
     */
    @Override
    public int compareTo(Foothold o) {
        Foothold other = o;
        if (p2.y < other.getY1()) {
            return -1;
        } else if (p1.y > other.getY2()) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 获取ID。
     * @return int 类型结果
     */
    public int getId() {
        return id;
    }

    /**
     * 获取下一。
     * @return int 类型结果
     */
    public int getNext() {
        return next;
    }

    /**
     * 设置下一。
     * @param next next
     */
    public void setNext(int next) {
        this.next = next;
    }

    /**
     * 获取Prev。
     * @return int 类型结果
     */
    public int getPrev() {
        return prev;
    }

    /**
     * 设置Prev。
     * @param prev prev
     */
    public void setPrev(int prev) {
        this.prev = prev;
    }
}
