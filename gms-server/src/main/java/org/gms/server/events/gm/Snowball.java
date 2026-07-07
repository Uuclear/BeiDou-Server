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

import org.gms.client.Character;
import org.gms.constants.id.MapId;
import org.gms.server.TimerManager;
import org.gms.server.maps.MapleMap;
import org.gms.util.PacketCreator;

import java.util.LinkedList;
import java.util.List;

/**
 * 打雪仗活动。
 */
public class Snowball {
    private final MapleMap map;
    private int position = 0;
    private int hits = 3;
    private int snowmanhp = 1000;
    private boolean hittable = false;
    private final int team;
    private boolean winner = false;
    List<Character> characters = new LinkedList<>();

    /**
     * 构造 Snowball 实例。
     * @param team team
     * @param map 地图名称
     */
    public Snowball(int team, MapleMap map) {
        this.map = map;
        this.team = team;

        for (Character chr : map.getCharacters()) {
            if (chr.getTeam() == team) {
                characters.add(chr);
            }
        }
    }

    /**
     * 执行 start、事件 操作。
     */
    public void startEvent() {
        if (hittable == true) {
            return;
        }

        for (Character chr : characters) {
            if (chr != null) {
                chr.sendPacket(PacketCreator.rollSnowBall(false, 1, map.getSnowball(0), map.getSnowball(1)));
                chr.sendPacket(PacketCreator.getClock(600));
            }
        }
        hittable = true;
        TimerManager.getInstance().schedule(() -> {
            if (map.getSnowball(team).getPosition() > map.getSnowball(team == 0 ? 1 : 0).getPosition()) {
                for (Character chr : characters) {
                    if (chr != null) {
                        chr.sendPacket(PacketCreator.rollSnowBall(false, 3, map.getSnowball(0), map.getSnowball(0)));
                    }
                }
                winner = true;
            } else if (map.getSnowball(team == 0 ? 1 : 0).getPosition() > map.getSnowball(team).getPosition()) {
                for (Character chr : characters) {
                    if (chr != null) {
                        chr.sendPacket(PacketCreator.rollSnowBall(false, 4, map.getSnowball(0), map.getSnowball(0)));
                    }
                }
                winner = true;
            } //Else
            warpOut();
        }, 600000);

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
     * @param hit hit
     */
    public void setHittable(boolean hit) {
        this.hittable = hit;
    }

    /**
     * 获取位置。
     * @return int 类型结果
     */
    public int getPosition() {
        return position;
    }

    /**
     * 获取Snowman、H、P。
     * @return int 类型结果
     */
    public int getSnowmanHP() {
        return snowmanhp;
    }

    /**
     * 设置Snowman、H、P。
     * @param hp hp
     */
    public void setSnowmanHP(int hp) {
        this.snowmanhp = hp;
    }

    /**
     * 执行 hit 操作。
     * @param what what
     * @param damage 伤害值
     */
    public void hit(int what, int damage) {
        if (what < 2) {
            if (damage > 0) {
                this.hits--;
            } else {
                if (this.snowmanhp - damage < 0) {
                    this.snowmanhp = 0;

                    TimerManager.getInstance().schedule(() -> {
                        setSnowmanHP(7500);
                        message(5);
                    }, 10000);
                } else {
                    this.snowmanhp -= damage;
                }
                map.broadcastMessage(PacketCreator.rollSnowBall(false, 1, map.getSnowball(0), map.getSnowball(1)));
            }
        }

        if (this.hits == 0) {
            this.position += 1;
            switch (this.position) {
            case 45:
                map.getSnowball(team == 0 ? 1 : 0).message(1);
                break;
            case 290:
                map.getSnowball(team == 0 ? 1 : 0).message(2);
                break;
            case 560:
                map.getSnowball(team == 0 ? 1 : 0).message(3);
                break;
            }

            this.hits = 3;
            map.broadcastMessage(PacketCreator.rollSnowBall(false, 0, map.getSnowball(0), map.getSnowball(1)));
            map.broadcastMessage(PacketCreator.rollSnowBall(false, 1, map.getSnowball(0), map.getSnowball(1)));
        }
        map.broadcastMessage(PacketCreator.hitSnowBall(what, damage));
    }

    /**
     * 执行 message 操作。
     * @param message message
     */
    public void message(int message) {
        for (Character chr : characters) {
            if (chr != null) {
                chr.sendPacket(PacketCreator.snowballMessage(team, message));
            }
        }
    }

    /**
     * 传送Out。
     */
    public void warpOut() {
        TimerManager.getInstance().schedule(() -> {
            if (winner) {
                map.warpOutByTeam(team, MapId.EVENT_WINNER);
            } else {
                map.warpOutByTeam(team, MapId.EVENT_EXIT);
            }

            map.setSnowball(team, null);
        }, 10000);
    }
}