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
package org.gms.scripting.reactor;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.constants.inventory.ItemConstants;
import org.gms.scripting.AbstractPlayerInteraction;
import org.gms.server.ItemInformationProvider;
import org.gms.server.TimerManager;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.Monster;
import org.gms.server.maps.MapMonitor;
import org.gms.server.maps.Reactor;
import org.gms.server.maps.ReactorDropEntry;
import org.gms.server.partyquest.CarnivalFactory;
import org.gms.server.partyquest.CarnivalFactory.MCSkill;
import org.gms.util.NumberTool;

import javax.script.Invocable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * 反应堆动作管理器，继承自AbstractPlayerInteraction，
 * 为反应堆脚本提供与玩家、地图、怪物和掉落物交互的专用方法。
 * 支持反应堆击打、触发、掉落物品、生成怪物等功能。
 *
 * @author Lerk
 * @author Ronan
 */
public class ReactorActionManager extends AbstractPlayerInteraction {
    /**
     * 当前交互的反应堆对象
     */
    private final Reactor reactor;
    
    /**
     * 脚本引擎可调用接口
     */
    private final Invocable iv;
    
    /**
     * 物品喷溅定时任务，用于延迟掉落物品
     */
    private ScheduledFuture<?> sprayTask = null;

    /**
     * 构造反应堆动作管理器
     *
     * @param c 客户端连接对象
     * @param reactor 反应堆对象
     * @param iv 脚本引擎可调用接口
     */
    public ReactorActionManager(Client c, Reactor reactor, Invocable iv) {
        super(c);
        this.reactor = reactor;
        this.iv = iv;
    }

    /**
     * 击打反应堆，触反应堆击打逻辑
     */
    public void hitReactor() {
        reactor.hitReactor(c);
    }

    /**
     * 销毁地图上指定ID的NPC
     *
     * @param npcId 要销毁的NPC ID
     */
    public void destroyNpc(int npcId) {
        reactor.getMap().destroyNPC(npcId);
    }

    /**
     * 对掉落条目进行分类排序。
     * 将普通物品、可见任务物品、其他任务物品分开处理。
     *
     * @param from 原始掉落列表
     * @param item 普通物品列表（输出参数）
     * @param visibleQuest 可见任务物品列表（输出参数）
     * @param otherQuest 其他任务物品列表（输出参数）
     * @param chr 当前玩家角色
     */
    private static void sortDropEntries(List<ReactorDropEntry> from, List<ReactorDropEntry> item, List<ReactorDropEntry> visibleQuest, List<ReactorDropEntry> otherQuest, Character chr) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (ReactorDropEntry mde : from) {
            if (!ii.isQuestItem(mde.itemId)) {
                item.add(mde);
            } else {
                if (chr.needQuestItem(mde.questid, mde.itemId)) {
                    visibleQuest.add(mde);
                } else {
                    otherQuest.add(mde);
                }
            }
        }
    }

    /**
     * 组装并打乱反应堆掉落列表，使掉落顺序更自然。
     *
     * @param chr 当前玩家角色
     * @param items 原始掉落列表
     * @return 重新排序后的掉落列表
     */
    private static List<ReactorDropEntry> assembleReactorDropEntries(Character chr, List<ReactorDropEntry> items) {
        final List<ReactorDropEntry> dropEntry = new ArrayList<>();
        final List<ReactorDropEntry> visibleQuestEntry = new ArrayList<>();
        final List<ReactorDropEntry> otherQuestEntry = new ArrayList<>();
        sortDropEntries(items, dropEntry, visibleQuestEntry, otherQuestEntry, chr);

        Collections.shuffle(dropEntry);
        Collections.shuffle(visibleQuestEntry);
        Collections.shuffle(otherQuestEntry);

        items.clear();
        items.addAll(dropEntry);
        items.addAll(visibleQuestEntry);
        items.addAll(otherQuestEntry);

        List<ReactorDropEntry> items1 = new ArrayList<>(items.size());
        List<ReactorDropEntry> items2 = new ArrayList<>(items.size() / 2);

        for (int i = 0; i < items.size(); i++) {
            if (i % 2 == 0) {
                items1.add(items.get(i));
            } else {
                items2.add(items.get(i));
            }
        }

        Collections.reverse(items1);
        items1.addAll(items2);

        return items1;
    }

    /**
     * 以喷溅方式掉落物品（使用反应堆位置，无金币）
     */
    public void sprayItems() {
        sprayItems(false, 0, 0, 0, 0);
    }

    /**
     * 以喷溅方式掉落物品（使用反应堆位置）
     *
     * @param meso 是否掉落金币
     * @param mesoChance 金币掉落概率（1/n的概率）
     * @param minMeso 最小金币数量
     * @param maxMeso 最大金币数量
     */
    public void sprayItems(boolean meso, int mesoChance, int minMeso, int maxMeso) {
        sprayItems(meso, mesoChance, minMeso, maxMeso, 0);
    }

    /**
     * 以喷溅方式掉落物品（使用反应堆位置）
     *
     * @param meso 是否掉落金币
     * @param mesoChance 金币掉落概率（1/n的概率）
     * @param minMeso 最小金币数量
     * @param maxMeso 最大金币数量
     * @param minItems 最少掉落物品数量
     */
    public void sprayItems(boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
        sprayItems((int) reactor.getPosition().getX(), (int) reactor.getPosition().getY(), meso, mesoChance, minMeso, maxMeso, minItems);
    }

    /**
     * 以喷溅方式在指定位置掉落物品
     *
     * @param posX X坐标
     * @param posY Y坐标
     * @param meso 是否掉落金币
     * @param mesoChance 金币掉落概率
     * @param minMeso 最小金币数量
     * @param maxMeso 最大金币数量
     * @param minItems 最少掉落物品数量
     */
    public void sprayItems(int posX, int posY, boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
        dropItems(true, posX, posY, meso, mesoChance, minMeso, maxMeso, minItems);
    }

    /**
     * 一次性掉落所有物品（使用反应堆位置，无金币）
     */
    public void dropItems() {
        dropItems(false, 0, 0, 0, 0);
    }

    /**
     * 一次性掉落所有物品（使用反应堆位置）
     *
     * @param meso 是否掉落金币
     * @param mesoChance 金币掉落概率
     * @param minMeso 最小金币数量
     * @param maxMeso 最大金币数量
     */
    public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso) {
        dropItems(meso, mesoChance, minMeso, maxMeso, 0);
    }

    /**
     * 一次性掉落所有物品（使用反应堆位置）
     *
     * @param meso 是否掉落金币
     * @param mesoChance 金币掉落概率
     * @param minMeso 最小金币数量
     * @param maxMeso 最大金币数量
     * @param minItems 最少掉落物品数量
     */
    public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
        dropItems((int) reactor.getPosition().getX(), (int) reactor.getPosition().getY(), meso, mesoChance, minMeso, maxMeso, minItems);
    }

    /**
     * 一次性在指定位置掉落所有物品
     *
     * @param posX X坐标
     * @param posY Y坐标
     * @param meso 是否掉落金币
     * @param mesoChance 金币掉落概率
     * @param minMeso 最小金币数量
     * @param maxMeso 最大金币数量
     * @param minItems 最少掉落物品数量
     */
    public void dropItems(int posX, int posY, boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
        dropItems(true, posX, posY, meso, mesoChance, minMeso, maxMeso, minItems);
    }

    /**
     * 掉落物品的核心实现方法，支持延迟喷溅或即时掉落。
     *
     * @param delayed 是否使用延迟喷溅模式
     * @param posX 掉落位置X坐标
     * @param posY 掉落位置Y坐标
     * @param meso 是否掉落金币
     * @param mesoChance 金币掉落概率
     * @param minMeso 最小金币数量
     * @param maxMeso 最大金币数量
     * @param minItems 最少掉落物品数量
     */
    public void dropItems(boolean delayed, int posX, int posY, boolean meso, int mesoChance, final int minMeso, final int maxMeso, int minItems) {
        Character chr = c.getPlayer();
        if (chr == null) {
            return;
        }

        List<ReactorDropEntry> items = assembleReactorDropEntries(chr, generateDropList(getDropChances(), chr.getDropRate(), meso, mesoChance, minItems));
        if (items.size() % 2 == 0) {
            posX -= 12;
        }
        final Point dropPos = new Point(posX, posY);

        if (!delayed) {
            ItemInformationProvider ii = ItemInformationProvider.getInstance();

            byte p = 1;
            for (ReactorDropEntry d : items) {
                dropPos.x = posX + ((p % 2 == 0) ? (25 * ((p + 1) / 2)) : -(25 * (p / 2)));
                p++;

                if (d.itemId == 0) {
                    int range = maxMeso - minMeso;
                    double displayDrop = Math.random() * range + minMeso;
                    int mesoDrop = NumberTool.doubleToInt(displayDrop * c.getWorldServer().getMesoRate());
                    reactor.getMap().spawnMesoDrop(mesoDrop, reactor.getMap().calcDropPos(dropPos, reactor.getPosition()), reactor, c.getPlayer(), false, (byte) 2);
                } else {
                    Item drop;

                    if (ItemConstants.getInventoryType(d.itemId) != InventoryType.EQUIP) {
                        drop = new Item(d.itemId, (short) 0, (short) 1);
                    } else {
                        drop = ii.randomizeStats((Equip) ii.getEquipById(d.itemId));
                    }

                    reactor.getMap().dropFromReactor(getPlayer(), reactor, drop, dropPos, (short) d.questid);
                }
            }
        } else {
            final Reactor r = reactor;
            final List<ReactorDropEntry> dropItems = items;
            final float worldMesoRate = c.getWorldServer().getMesoRate();

            dropPos.x -= (12 * items.size());

            sprayTask = TimerManager.getInstance().register(() -> {
                if (dropItems.isEmpty()) {
                    sprayTask.cancel(false);
                    return;
                }

                ReactorDropEntry d = dropItems.remove(0);
                if (d.itemId == 0) {
                    int range = maxMeso - minMeso;
                    double displayDrop = Math.random() * range + minMeso;
                    int mesoDrop = NumberTool.doubleToInt(displayDrop * worldMesoRate);
                    r.getMap().spawnMesoDrop(mesoDrop, r.getMap().calcDropPos(dropPos, r.getPosition()), r, chr, false, (byte) 2);
                } else {
                    Item drop;

                    if (ItemConstants.getInventoryType(d.itemId) != InventoryType.EQUIP) {
                        drop = new Item(d.itemId, (short) 0, (short) 1);
                    } else {
                        ItemInformationProvider ii = ItemInformationProvider.getInstance();
                        drop = ii.randomizeStats((Equip) ii.getEquipById(d.itemId));
                    }

                    r.getMap().dropFromReactor(getPlayer(), r, drop, dropPos, (short) d.questid);
                }

                dropPos.x += 25;
            }, 200);
        }
    }

    /**
     * 获取当前反应堆的掉落配置列表
     *
     * @return 反应堆掉落条目列表
     */
    private List<ReactorDropEntry> getDropChances() {
        return ReactorScriptManager.getInstance().getDrops(reactor.getId());
    }

    /**
     * 根据掉落概率生成实际掉落列表
     *
     * @param drops 配置的掉落列表
     * @param dropRate 掉落倍率
     * @param meso 是否掉落金币
     * @param mesoChance 金币掉落概率
     * @param minItems 最少掉落数量
     * @return 随机生成的掉落列表
     */
    private List<ReactorDropEntry> generateDropList(List<ReactorDropEntry> drops, float dropRate, boolean meso, int mesoChance, int minItems) {
        List<ReactorDropEntry> items = new ArrayList<>();
        if (meso && Math.random() < (1 / (double) mesoChance)) {
            items.add(new ReactorDropEntry(0, mesoChance, -1));
        }

        for (ReactorDropEntry mde : drops) {
            if (Math.random() < (dropRate / (double) mde.chance)) {
                items.add(mde);
            }
        }

        while (items.size() < minItems) {
            items.add(new ReactorDropEntry(0, mesoChance, -1));
        }

        return items;
    }

    /**
     * 在反应堆位置生成1只指定怪物
     *
     * @param id 怪物ID
     */
    public void spawnMonster(int id) {
        spawnMonster(id, 1, getPosition());
    }

    /**
     * 创建地图监视器，用于监控地图状态变化
     *
     * @param mapId 要监控的地图ID
     * @param portal 玩家退出时传送的传送门名称
     */
    public void createMapMonitor(int mapId, String portal) {
        new MapMonitor(c.getChannelServer().getMapFactory().getMap(mapId), portal);
    }

    /**
     * 在反应堆位置生成指定数量的怪物
     *
     * @param id 怪物ID
     * @param qty 怪物数量
     */
    public void spawnMonster(int id, int qty) {
        spawnMonster(id, qty, getPosition());
    }

    /**
     * 在指定坐标生成指定数量的怪物
     *
     * @param id 怪物ID
     * @param qty 怪物数量
     * @param x X坐标
     * @param y Y坐标
     */
    public void spawnMonster(int id, int qty, int x, int y) {
        spawnMonster(id, qty, new Point(x, y));
    }

    /**
     * 在指定位置生成指定数量的怪物
     *
     * @param id 怪物ID
     * @param qty 怪物数量
     * @param pos 生成位置
     */
    public void spawnMonster(int id, int qty, Point pos) {
        for (int i = 0; i < qty; i++) {
            reactor.getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(id), pos);
        }
    }

    /**
     * 杀死地图上所有指定ID的怪物（不掉落物品）
     *
     * @param id 怪物ID
     */
    public void killMonster(int id) {
        killMonster(id, false);
    }

    /**
     * 杀死地图上所有指定ID的怪物
     *
     * @param id 怪物ID
     * @param withDrops 杀死怪物时是否掉落物品
     */
    public void killMonster(int id, boolean withDrops) {
        if (withDrops) {
            getMap().killMonsterWithDrops(id);
        } else {
            getMap().killMonster(id);
        }
    }

    /**
     * 获取反应堆的位置（向上偏移10像素）
     *
     * @return 调整后的位置点
     */
    public Point getPosition() {
        Point pos = reactor.getPosition();
        pos.y -= 10;
        return pos;
    }

    /**
     * 在反应堆位置生成NPC
     *
     * @param npcId NPC ID
     */
    public void spawnNpc(int npcId) {
        spawnNpc(npcId, getPosition());
    }

    /**
     * 在指定位置生成NPC
     *
     * @param npcId NPC ID
     * @param pos 生成位置
     */
    public void spawnNpc(int npcId, Point pos) {
        spawnNpc(npcId, pos, reactor.getMap());
    }

    /**
     * 获取当前反应堆对象
     *
     * @return 反应堆对象
     */
    public Reactor getReactor() {
        return reactor;
    }

    /**
     * 生成假怪物（用于显示效果）
     *
     * @param id 怪物ID
     */
    public void spawnFakeMonster(int id) {
        reactor.getMap().spawnFakeMonsterOnGroundBelow(LifeFactory.getMonster(id), getPosition());
    }

    /**
     * 延迟召唤Boss，用于Targa和Scarlion等Boss战。
     *
     * @param mobId Boss怪物ID
     * @param delayMs 延迟时间（毫秒）
     * @param x 召唤位置X坐标
     * @param y 召唤位置Y坐标
     * @param bgm 召唤时播放的背景音乐
     * @param summonMessage 召唤时显示的地图消息
     */
    public void summonBossDelayed(final int mobId, final int delayMs, final int x, final int y, final String bgm,
                                  final String summonMessage) {
        TimerManager.getInstance().schedule(() -> {
            summonBoss(mobId, x, y, bgm, summonMessage);
        }, delayMs);
    }

    /**
     * 立即召唤Boss，播放音乐并显示消息
     *
     * @param mobId Boss怪物ID
     * @param x X坐标
     * @param y Y坐标
     * @param bgmName BGM名称
     * @param summonMessage 召唤消息
     */
    private void summonBoss(int mobId, int x, int y, String bgmName, String summonMessage) {
        spawnMonster(mobId, x, y);
        changeMusic(bgmName);
        mapMessage(6, summonMessage);
    }

    /**
     * 驱散所有怪物的指定守卫技能（用于嘉年华CPQ）
     *
     * @param num 守卫技能编号
     * @param team 队伍编号（0=红队，1=蓝队）
     */
    public void dispelAllMonsters(int num, int team) {
        final MCSkill skil = CarnivalFactory.getInstance().getGuardian(num);
        if (skil != null) {
            for (Monster mons : getMap().getAllMonsters()) {
                if (mons.getTeam() == team) {
                    mons.dispelSkill(skil.getSkill());
                }
            }
        }
        if (team == 0) {
            getPlayer().getMap().getRedTeamBuffs().remove(skil);
        } else {
            getPlayer().getMap().getBlueTeamBuffs().remove(skil);
        }
    }
}