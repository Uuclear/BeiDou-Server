/*
    This file is part of the HeavenMS Maple Story Server
    Copyleft (L) 2016 - 2019 RonanLana

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
package org.gms.util;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 区间构建器
 * <p>
 * 用于管理和操作数值区间，支持区间的添加、查询和清空操作。
 * 自动合并重叠的区间，使用二分查找提高查询效率，
 * 并通过读写锁保证线程安全。
 * </p>
 *
 * @author Ronan
 * @since 1.0.0
 */
public class IntervalBuilder {

    /**
     * 区间边界列表，使用Line2D存储区间的起止点（X1为起点，X2为终点）
     */
    private final List<Line2D> intervalLimits = new ArrayList<>();

    /**
     * 读锁，用于并发读取区间数据
     */
    private final Lock intervalRlock;

    /**
     * 写锁，用于修改区间数据
     */
    private final Lock intervalWlock;

    /**
     * 构造函数，初始化读写锁（使用公平锁）
     */
    public IntervalBuilder() {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.intervalRlock = readWriteLock.readLock();
        this.intervalWlock = readWriteLock.writeLock();
    }

    /**
     * 重新调整重叠区间
     * <p>
     * 将新增区间与已有重叠区间合并为一个新的连续区间。
     * </p>
     *
     * @param st      重叠区间的起始索引
     * @param en      重叠区间的结束索引
     * @param newFrom 新区间的起始点
     * @param newTo   新区间的结束点
     */
    private void refitOverlappedIntervals(int st, int en, int newFrom, int newTo) {
        List<Line2D> checkLimits = new ArrayList<>(intervalLimits.subList(st, en));

        float newLimitX1, newLimitX2;
        if (!checkLimits.isEmpty()) {
            Line2D firstLimit = checkLimits.get(0);
            Line2D lastLimit = checkLimits.get(checkLimits.size() - 1);

            newLimitX1 = (float) ((newFrom < firstLimit.getX1()) ? newFrom : firstLimit.getX1());
            newLimitX2 = (float) ((newTo > lastLimit.getX2()) ? newTo : lastLimit.getX2());

            for (Line2D limit : checkLimits) {
                intervalLimits.remove(st);
            }
        } else {
            newLimitX1 = newFrom;
            newLimitX2 = newTo;
        }

        intervalLimits.add(st, new Line2D.Float(newLimitX1, 0, newLimitX2, 0));
    }

    /**
     * 使用二分查找定位点所在的区间
     *
     * @param point 要查找的点
     * @return 找到的区间索引，如果未找到则返回最后一个不大于该点的区间索引
     */
    private int bsearchInterval(int point) {
        int st = 0, en = intervalLimits.size() - 1;

        int mid, idx;
        while (en >= st) {
            idx = (st + en) / 2;
            mid = (int) intervalLimits.get(idx).getX1();

            if (mid == point) {
                return idx;
            } else if (mid < point) {
                st = idx + 1;
            } else {
                en = idx - 1;
            }
        }

        return en;
    }

    /**
     * 添加一个新的区间
     * <p>
     * 如果新区间与已有区间重叠或相邻，会自动合并。
     * 该方法是线程安全的，使用写锁保护。
     * </p>
     *
     * @param from 区间起始点
     * @param to   区间结束点
     */
    public void addInterval(int from, int to) {
        intervalWlock.lock();
        try {
            int st = bsearchInterval(from);
            if (st < 0) {
                st = 0;
            } else if (intervalLimits.get(st).getX2() < from) {
                st += 1;
            }

            int en = bsearchInterval(to);
            if (en < st) {
                en = st - 1;
            }

            refitOverlappedIntervals(st, en + 1, from, to);
        } finally {
            intervalWlock.unlock();
        }
    }

    /**
     * 检查一个点是否在某个区间内
     *
     * @param point 要检查的点
     * @return 如果点在区间内返回true，否则返回false
     */
    public boolean inInterval(int point) {
        return inInterval(point, point);
    }

    /**
     * 检查一个范围[from, to]是否完全包含在某个区间内
     * <p>
     * 该方法是线程安全的，使用读锁保护。
     * </p>
     *
     * @param from 范围起始点
     * @param to   范围结束点
     * @return 如果范围完全在某个区间内返回true，否则返回false
     */
    public boolean inInterval(int from, int to) {
        intervalRlock.lock();
        try {
            int idx = bsearchInterval(from);
            return idx >= 0 && to <= intervalLimits.get(idx).getX2();
        } finally {
            intervalRlock.unlock();
        }
    }

    /**
     * 清空所有区间
     * <p>
     * 该方法是线程安全的，使用写锁保护。
     * </p>
     */
    public void clear() {
        intervalWlock.lock();
        try {
            intervalLimits.clear();
        } finally {
            intervalWlock.unlock();
        }
    }

}
