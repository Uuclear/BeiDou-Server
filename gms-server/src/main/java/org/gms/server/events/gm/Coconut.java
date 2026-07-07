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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author kevintjuh93
 */
//Make them better :)
/**
 * 椰子活动（单人）。
 */
public class Coconut extends Event {
    private MapleMap map = null;
    private int MapleScore = 0;
    private int StoryScore = 0;
    private int countBombing = 80;
    private int countFalling = 401;
    private int countStopped = 20;
    private final List<Coconuts> coconuts = new LinkedList<>();

    /**
     * 构造 Coconut 实例。
     * @param map 地图名称
     */
    public Coconut(MapleMap map) {
        super(1, 50);
        this.map = map;
    }

    /**
     * 执行 start、事件 操作。
     */
    public void startEvent() {
        map.startEvent();
        for (int i = 0; i < 506; i++) {
            coconuts.add(new Coconuts(i));
        }
        map.broadcastMessage(PacketCreator.hitCoconut(true, 0, 0));
        setCoconutsHittable(true);
        map.broadcastMessage(PacketCreator.getClock(300));

        TimerManager.getInstance().schedule(() -> {
            if (map.getId() == MapId.EVENT_COCONUT_HARVEST) {
                if (getMapleScore() == getStoryScore()) {
                    bonusTime();
                } else if (getMapleScore() > getStoryScore()) {
                    for (Character chr : map.getCharacters()) {
                        if (chr.getTeam() == 0) {
                            chr.sendPacket(PacketCreator.showEffect("event/coconut/victory"));
                            chr.sendPacket(PacketCreator.playSound("Coconut/Victory"));
                        } else {
                            chr.sendPacket(PacketCreator.showEffect("event/coconut/lose"));
                            chr.sendPacket(PacketCreator.playSound("Coconut/Failed"));
                        }
                    }
                    warpOut();
                } else {
                    for (Character chr : map.getCharacters()) {
                        if (chr.getTeam() == 1) {
                            chr.sendPacket(PacketCreator.showEffect("event/coconut/victory"));
                            chr.sendPacket(PacketCreator.playSound("Coconut/Victory"));
                        } else {
                            chr.sendPacket(PacketCreator.showEffect("event/coconut/lose"));
                            chr.sendPacket(PacketCreator.playSound("Coconut/Failed"));
                        }
                    }
                    warpOut();
                }
            }
        }, 300000);
    }

    /**
     * 执行 bonus、时间 操作。
     */
    public void bonusTime() {
        map.broadcastMessage(PacketCreator.getClock(120));
        TimerManager.getInstance().schedule(() -> {
            if (getMapleScore() == getStoryScore()) {
                for (Character chr : map.getCharacters()) {
                    chr.sendPacket(PacketCreator.showEffect("event/coconut/lose"));
                    chr.sendPacket(PacketCreator.playSound("Coconut/Failed"));
                }
                warpOut();
            } else if (getMapleScore() > getStoryScore()) {
                for (Character chr : map.getCharacters()) {
                    if (chr.getTeam() == 0) {
                        chr.sendPacket(PacketCreator.showEffect("event/coconut/victory"));
                        chr.sendPacket(PacketCreator.playSound("Coconut/Victory"));
                    } else {
                        chr.sendPacket(PacketCreator.showEffect("event/coconut/lose"));
                        chr.sendPacket(PacketCreator.playSound("Coconut/Failed"));
                    }
                }
                warpOut();
            } else {
                for (Character chr : map.getCharacters()) {
                    if (chr.getTeam() == 1) {
                        chr.sendPacket(PacketCreator.showEffect("event/coconut/victory"));
                        chr.sendPacket(PacketCreator.playSound("Coconut/Victory"));
                    } else {
                        chr.sendPacket(PacketCreator.showEffect("event/coconut/lose"));
                        chr.sendPacket(PacketCreator.playSound("Coconut/Failed"));
                    }
                }
                warpOut();
            }
        }, 120000);

    }

    /**
     * 传送Out。
     */
    public void warpOut() {
        setCoconutsHittable(false);
        TimerManager.getInstance().schedule(() -> {
            List<Character> chars = new ArrayList<>(map.getCharacters());

            for (Character chr : chars) {
                if ((getMapleScore() > getStoryScore() && chr.getTeam() == 0) || (getStoryScore() > getMapleScore() && chr.getTeam() == 1)) {
                    chr.changeMap(MapId.EVENT_WINNER);
                } else {
                    chr.changeMap(MapId.EVENT_EXIT);
                }
            }
            map.setCoconut(null);
        }, 12000);
    }

    /**
     * 获取冒险岛、Score。
     * @return int 类型结果
     */
    public int getMapleScore() {
        return MapleScore;
    }

    /**
     * 获取Story、Score。
     * @return int 类型结果
     */
    public int getStoryScore() {
        return StoryScore;
    }

    /**
     * 添加冒险岛、Score。
     */
    public void addMapleScore() {
        this.MapleScore += 1;
    }

    /**
     * 添加Story、Score。
     */
    public void addStoryScore() {
        this.StoryScore += 1;
    }

    /**
     * 获取Bombings。
     * @return int 类型结果
     */
    public int getBombings() {
        return countBombing;
    }

    /**
     * 执行 bomb、椰子 操作。
     */
    public void bombCoconut() {
        countBombing--;
    }

    /**
     * 获取Falling。
     * @return int 类型结果
     */
    public int getFalling() {
        return countFalling;
    }

    /**
     * 执行 fall、椰子 操作。
     */
    public void fallCoconut() {
        countFalling--;
    }

    /**
     * 获取Stopped。
     * @return int 类型结果
     */
    public int getStopped() {
        return countStopped;
    }

    /**
     * 执行 stop、椰子 操作。
     */
    public void stopCoconut() {
        countStopped--;
    }

    /**
     * 获取椰子。
     * @param id ID
     * @return Coconuts 类型结果
     */
    public Coconuts getCoconut(int id) {
        return coconuts.get(id);
    }

    /**
     * 获取所有、Coconuts。
     * @return List<Coconuts> 类型结果
     */
    public List<Coconuts> getAllCoconuts() {
        return coconuts;
    }

    /**
     * 设置Coconuts、Hittable。
     * @param hittable hittable
     */
    public void setCoconutsHittable(boolean hittable) {
        for (Coconuts nut : coconuts) {
            nut.setHittable(hittable);
        }
    }
}  