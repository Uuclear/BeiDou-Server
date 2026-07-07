/*
    This file is part of the HeavenMS MapleStory Server
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
 * 整数区间集合构建器，支持合并重叠区间并高效查询点或子区间是否被覆盖。
 * <p>
 * 使用读写锁保证并发安全，适用于地图阻挡、活动时间段等区间判定场景。
 *
 * @author Ronan
 */
public class IntervalBuilder {
    private final List<Line2D> intervalLimits = new ArrayList<>();
    private final Lock intervalRlock;
    private final Lock intervalWlock;

    /**
     * 构造空的区间构建器。
     */
    public IntervalBuilder() {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.intervalRlock = readWriteLock.readLock();
        this.intervalWlock = readWriteLock.writeLock();
    }

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
     * 添加闭区间 {@code [from, to]}，自动与已有重叠区间合并。
     *
     * @param from 区间起点（含）
     * @param to   区间终点（含）
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
     * 判断单个整型点是否落在已添加的任一区间内。
     *
     * @param point 待查询的点
     * @return 在区间内返回 {@code true}
     */
    public boolean inInterval(int point) {
        return inInterval(point, point);
    }

    /**
     * 判断闭区间 {@code [from, to]} 是否完全落在已添加的某一区间内。
     *
     * @param from 子区间起点（含）
     * @param to   子区间终点（含）
     * @return 完全被覆盖返回 {@code true}
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
     * 清空所有已添加的区间。
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
