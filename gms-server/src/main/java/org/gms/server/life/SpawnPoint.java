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
package org.gms.server.life;

import org.gms.client.Character;
import org.gms.net.server.Server;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 怪物刷新点，控制刷新间隔、数量上限与范围。
 */
public class SpawnPoint {
    private final int monster;
    private final int mobTime;
    private final int team;
    private final int fh;
    private final int f;
    private final Point pos;
    private long nextPossibleSpawn;
    private int mobInterval = 5000;
    private final AtomicInteger spawnedMonsters = new AtomicInteger(0);
    private final boolean immobile;
    private boolean denySpawn = false;

    /**
     * 构造 SpawnPoint 实例。
     * @param monster 怪物
     * @param pos 坐标
     * @param immobile immobile
     * @param mobTime mobTime
     * @param mobInterval mobInterval
     * @param team team
     */
    public SpawnPoint(final Monster monster, Point pos, boolean immobile, int mobTime, int mobInterval, int team) {
        this.monster = monster.getId();
        this.pos = new Point(pos);
        this.mobTime = mobTime;
        this.team = team;
        this.fh = monster.getFh();
        this.f = monster.getF();
        this.immobile = immobile;
        this.mobInterval = mobInterval;
        this.nextPossibleSpawn = Server.getInstance().getCurrentTime();
    }

    /**
     * 获取Spawned。
     * @return int 类型结果
     */
    public int getSpawned() {
        return spawnedMonsters.intValue();
    }

    /**
     * 设置Deny、刷新。
     * @param val val
     */
    public void setDenySpawn(boolean val) {
        denySpawn = val;
    }

    /**
     * 获取Deny、刷新。
     * @return boolean 类型结果
     */
    public boolean getDenySpawn() {
        return denySpawn;
    }

    /**
     * 执行 should、刷新 操作。
     * @return boolean 类型结果
     */
    public boolean shouldSpawn() {
        if (denySpawn || mobTime < 0 || spawnedMonsters.get() > 0) {
            return false;
        }
        return nextPossibleSpawn <= Server.getInstance().getCurrentTime();
    }

    /**
     * 执行 should、Force、刷新 操作。
     * @return boolean 类型结果
     */
    public boolean shouldForceSpawn() {
        return mobTime >= 0 && spawnedMonsters.get() <= 0;
    }

    /**
     * 获取怪物。
     * @return Monster 类型结果
     */
    public Monster getMonster() {
        Monster mob = new Monster(LifeFactory.getMonster(monster));
        mob.setPosition(new Point(pos));
        mob.setTeam(team);
        mob.setFh(fh);
        mob.setF(f);
        spawnedMonsters.incrementAndGet();
        mob.addListener(new MonsterListener() {
            /**
             * 执行 monster、Killed 操作。
             * @param aniTime aniTime
             */
            @Override
            public void monsterKilled(int aniTime) {
                nextPossibleSpawn = Server.getInstance().getCurrentTime();
                if (mobTime > 0) {
                    nextPossibleSpawn += SECONDS.toMillis(mobTime);
                } else {
                    nextPossibleSpawn += aniTime;
                }
                spawnedMonsters.decrementAndGet();
            }

            /**
             * 执行 monster、Damaged 操作。
             * @param from from
             */
            @Override
            public void monsterDamaged(Character from, int trueDmg) {}

            @Override
            public void monsterHealed(int trueHeal) {}
        });
        if (mobTime == 0) {
            nextPossibleSpawn = Server.getInstance().getCurrentTime() + mobInterval;
        }
        return mob;
    }

    /**
     * 获取怪物ID。
     * @return int 类型结果
     */
    public int getMonsterId() {
        return monster;
    }

    /**
     * 获取位置。
     * @return Point 类型结果
     */
    public Point getPosition() {
        return pos;
    }

    /**
     * 获取F。
     * @return int 类型结果
     */
    public final int getF() {
        return f;
    }

    /**
     * 获取Fh。
     * @return int 类型结果
     */
    public final int getFh() {
        return fh;
    }

    /**
     * 获取怪物时间。
     * @return int 类型结果
     */
    public int getMobTime() {
        return mobTime;
    }

    /**
     * 获取队伍。
     * @return int 类型结果
     */
    public int getTeam() {
        return team;
    }
}
