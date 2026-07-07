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
package org.gms.server.maps;

import org.gms.client.Character;
import org.gms.server.TimerManager;
import org.gms.util.PacketCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 迷你地下城实例，含独立计时与怪物刷新。
 */
public class MiniDungeon {
    List<Character> players = new ArrayList<>();
    ScheduledFuture<?> timeoutTask = null;
    private final Lock lock = new ReentrantLock(true);

    int baseMap;
    long expireTime;

    /**
     * 构造 MiniDungeon 实例。
     * @param base base
     * @param timeLimit 时间限制（秒）
     */
    public MiniDungeon(int base, long timeLimit) {
        baseMap = base;
        expireTime = SECONDS.toMillis(timeLimit);

        timeoutTask = TimerManager.getInstance().schedule(() -> close(), expireTime);

        expireTime += System.currentTimeMillis();
    }

    /**
     * 注册玩家。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean registerPlayer(Character chr) {
        int time = (int) ((expireTime - System.currentTimeMillis()) / 1000);
        if (time > 0) {
            chr.sendPacket(PacketCreator.getClock(time));
        }

        lock.lock();
        try {
            if (timeoutTask == null) {
                return false;
            }

            players.add(chr);
        } finally {
            lock.unlock();
        }

        return true;
    }

    /**
     * 执行 unregister、玩家 操作。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean unregisterPlayer(Character chr) {
        chr.sendPacket(PacketCreator.removeClock());

        lock.lock();
        try {
            players.remove(chr);

            if (players.isEmpty()) {
                dispose();
                return false;
            }
        } finally {
            lock.unlock();
        }

        if (chr.isPartyLeader()) {  // thanks Conrad for noticing party is not sent out of the MD as soon as leader leaves it
            close();
        }

        return true;
    }

    /**
     * 执行 close 操作。
     */
    public void close() {
        lock.lock();
        try {
            List<Character> lchr = new ArrayList<>(players);

            for (Character chr : lchr) {
                chr.changeMap(baseMap);
            }

            dispose();
            timeoutTask = null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行 dispose 操作。
     */
    public void dispose() {
        lock.lock();
        try {
            players.clear();

            if (timeoutTask != null) {
                timeoutTask.cancel(false);
                timeoutTask = null;
            }
        } finally {
            lock.unlock();
        }
    }
}
