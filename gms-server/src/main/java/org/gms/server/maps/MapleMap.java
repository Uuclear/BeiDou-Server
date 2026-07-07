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

import org.gms.client.BuffStat;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.autoban.AutobanFactory;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.Pet;
import org.gms.client.status.MonsterStatus;
import org.gms.client.status.MonsterStatusEffect;
import org.gms.config.GameConfig;
import org.gms.constants.game.GameConstants;
import org.gms.constants.id.MapId;
import org.gms.constants.id.MobId;
import org.gms.constants.inventory.ItemConstants;
import org.gms.net.packet.Packet;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.coordinator.world.MonsterAggroCoordinator;
import org.gms.net.server.services.task.channel.MobMistService;
import org.gms.net.server.services.task.channel.OverallService;
import org.gms.net.server.services.type.ChannelServices;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.World;
import org.gms.util.NumberTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.event.EventInstanceManager;
import org.gms.scripting.map.MapScriptManager;
import org.gms.server.ItemInformationProvider;
import org.gms.server.StatEffect;
import org.gms.server.TimerManager;
import org.gms.server.events.gm.Coconut;
import org.gms.server.events.gm.Fitness;
import org.gms.server.events.gm.Ola;
import org.gms.server.events.gm.OxQuiz;
import org.gms.server.events.gm.Snowball;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.LifeFactory.selfDestruction;
import org.gms.server.life.Monster;
import org.gms.server.life.MonsterDropEntry;
import org.gms.server.life.MonsterGlobalDropEntry;
import org.gms.server.life.MonsterInformationProvider;
import org.gms.server.life.MonsterListener;
import org.gms.server.life.NPC;
import org.gms.server.life.PlayerNPC;
import org.gms.server.life.SpawnPoint;
import org.gms.server.partyquest.CarnivalFactory;
import org.gms.server.partyquest.CarnivalFactory.MCSkill;
import org.gms.server.partyquest.GuardianSpawnPoint;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;
import org.gms.util.Randomizer;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 单张地图的运行时实例。管理地图对象（怪物、NPC、掉落物、反应堆、传送门）、玩家进出、怪物刷新、掉落计算、伤害处理及各类地图事件，使用读写锁保护并发访问。
 */
public class MapleMap {
    private static final Logger log = LoggerFactory.getLogger(MapleMap.class);
    private static final List<MapObjectType> rangedMapobjectTypes = Arrays.asList(MapObjectType.SHOP, MapObjectType.ITEM, MapObjectType.NPC, MapObjectType.MONSTER, MapObjectType.DOOR, MapObjectType.SUMMON, MapObjectType.REACTOR);
    private static final Map<Integer, Pair<Integer, Integer>> dropBoundsCache = new HashMap<>(100);

    private final Map<Integer, MapObject> mapobjects = new LinkedHashMap<>();
    private final Set<Integer> selfDestructives = new LinkedHashSet<>();
    private final Collection<SpawnPoint> monsterSpawn = Collections.synchronizedList(new LinkedList<>());
    private final Collection<SpawnPoint> allMonsterSpawn = Collections.synchronizedList(new LinkedList<>());
    private final AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    private final AtomicInteger droppedItemCount = new AtomicInteger(0);
    private final Collection<Character> characters = new LinkedHashSet<>();
    private final Map<Integer, Set<Integer>> mapParty = new LinkedHashMap<>();
    private final Map<Integer, Portal> portals = new HashMap<>();
    private final Map<Integer, Integer> backgroundTypes = new HashMap<>();
    private final Map<String, Integer> environment = new LinkedHashMap<>();
    private final Map<MapItem, Long> droppedItems = new LinkedHashMap<>();
    private final LinkedList<WeakReference<MapObject>> registeredDrops = new LinkedList<>();
    private final Map<MobLootEntry, Long> mobLootEntries = new HashMap(20);
    private final List<Runnable> statUpdateRunnables = new ArrayList(50);
    private final List<Rectangle> areas = new ArrayList<>();
    private FootholdTree footholds = null;
    private Pair<Integer, Integer> xLimits;  // 缓存有落脚点可用的最小/最大 X 坐标
    private final Rectangle mapArea = new Rectangle();
    private final int mapid;
    private final AtomicInteger runningOid = new AtomicInteger(1000000001);
    private final int returnMapId;
    private final int channel;
    private final int world;
    private int seats;
    private byte monsterRate;
    private boolean clock;
    private boolean boat;
    private boolean docked = false;
    private EventInstanceManager event = null;
    private String mapName;
    private String streetName;
    private MapEffect mapEffect = null;
    private boolean everlast = false;
    private int forcedReturnMap = MapId.NONE;
    private int timeLimit;
    private long mapTimer;
    private int decHP = 0;
    private float recovery = 1.0f;
    private int protectItem = 0;
    private boolean town;
    private OxQuiz ox;
    private boolean isOxQuiz = false;
    private boolean dropsOn = true;
    private String onFirstUserEnter;
    private String onUserEnter;
    private int fieldType;
    private int fieldLimit = 0;
    private int mobCapacity = -1;
    private MonsterAggroCoordinator aggroMonitor = null;   // aggroMonitor activity in sync with itemMonitor
    private ScheduledFuture<?> itemMonitor = null;
    private ScheduledFuture<?> expireItemsTask = null;
    private ScheduledFuture<?> mobSpawnLootTask = null;
    private ScheduledFuture<?> characterStatUpdateTask = null;
    private short itemMonitorTimeout;
    private Pair<Integer, String> timeMob = null;
    private short mobInterval = 5000;
    private boolean allowSummons = true; // All maps should have this true at the beginning
    private Character mapOwner = null;
    private long mapOwnerLastActivityTime = Long.MAX_VALUE;

    // events
    private boolean eventstarted = false, isMuted = false;
    private Snowball snowball0 = null;
    private Snowball snowball1 = null;
    private Coconut coconut;

    //CPQ
    private int maxMobs;
    private int maxReactors;
    private int deathCP;
    private int timeDefault;
    private int timeExpand;

    //locks
    private final Lock chrRLock;
    private final Lock chrWLock;
    private final Lock objectRLock;
    private final Lock objectWLock;

    private final Lock lootLock = new ReentrantLock(true);

    // due to the nature of loadMapFromWz (synchronized), sole function that calls 'generateMapDropRangeCache', this lock remains optional.
    private static final Lock bndLock = new ReentrantLock(true);

    /**
     * 构造 MapleMap 实例。
     * @param mapid 地图 ID
     * @param world world
     * @param channel 频道号
     * @param returnMapId returnMapId
     * @param monsterRate monsterRate
     */
    public MapleMap(int mapid, int world, int channel, int returnMapId, float monsterRate) {
        this.mapid = mapid;
        this.channel = channel;
        this.world = world;
        this.returnMapId = returnMapId;
        this.monsterRate = (byte) Math.ceil(monsterRate);
        if (this.monsterRate == 0) {
            this.monsterRate = 1;
        }

        final ReadWriteLock chrLock = new ReentrantReadWriteLock(true);
        chrRLock = chrLock.readLock();
        chrWLock = chrLock.writeLock();

        final ReadWriteLock objectLock = new ReentrantReadWriteLock(true);
        objectRLock = objectLock.readLock();
        objectWLock = objectLock.writeLock();

        aggroMonitor = new MonsterAggroCoordinator();
    }

    /**
     * 设置事件实例。
     * @param eim 事件实例管理器
     */
    public void setEventInstance(EventInstanceManager eim) {
        event = eim;
    }

    /**
     * 获取事件实例。
     * @return EventInstanceManager 类型结果
     */
    public EventInstanceManager getEventInstance() {
        return event;
    }

    /**
     * 获取地图区域。
     * @return Rectangle 类型结果
     */
    public Rectangle getMapArea() {
        return mapArea;
    }

    /**
     * 获取世界。
     * @return int 类型结果
     */
    public int getWorld() {
        return world;
    }

    /**
     * 向地图广播数据包。
     * @param source 来源角色
     * @param packet 网络数据包
     */
    public void broadcastPacket(Character source, Packet packet) {
        broadcastPacket(packet, chr -> chr != null && chr.getClient() != null && chr != source);
    }

    /**
     * 向地图广播GM数据包。
     * @param source 来源角色
     * @param packet 网络数据包
     */
    public void broadcastGMPacket(Character source, Packet packet) {
        broadcastPacket(packet, chr -> chr != null && chr.getClient() != null && chr != source && chr.gmLevel() >= source.gmLevel());
    }

    private void broadcastPacket(Packet packet, Predicate<Character> chrFilter) {
        chrRLock.lock();
        try {
            characters.stream()
                    .filter(chrFilter)
                    .forEach(chr -> chr.sendPacket(packet));
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 切换掉落开关状态。
     */
    public void toggleDrops() {
        this.dropsOn = !dropsOn;
    }

    private static double getRangedDistance() {
        return GameConfig.getServerBoolean("use_max_range") ? Double.POSITIVE_INFINITY : 722500;
    }

    /**
     * 获取地图、对象、在、矩形区域。
     * @param box 矩形区域
     * @param types 对象类型列表（MapObjectType 列表/集合）
     * @return List<MapObject> 类型结果
     */
    public List<MapObject> getMapObjectsInRect(Rectangle box, List<MapObjectType> types) {
        objectRLock.lock();
        final List<MapObject> ret = new LinkedList<>();
        try {
            for (MapObject l : mapobjects.values()) {
                if (types.contains(l.getType())) {
                    if (box.contains(l.getPosition())) {
                        ret.add(l);
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
        return ret;
    }

    /**
     * 获取ID。
     * @return int 类型结果
     */
    public int getId() {
        return mapid;
    }

    /**
     * 获取频道、Server。
     * @return Channel 类型结果
     */
    public Channel getChannelServer() {
        return Server.getInstance().getWorld(world).getChannel(channel);
    }

    /**
     * 获取世界、Server。
     * @return World 类型结果
     */
    public World getWorldServer() {
        return Server.getInstance().getWorld(world);
    }

    /**
     * 获取返回地图。
     * @return MapleMap 类型结果
     */
    public MapleMap getReturnMap() {
        if (returnMapId == MapId.NONE) {
            return this;
        }
        return getChannelServer().getMapFactory().getMap(returnMapId);
    }

    /**
     * 获取返回地图ID。
     * @return int 类型结果
     */
    public int getReturnMapId() {
        return returnMapId;
    }

    /**
     * 获取强制返回地图。
     * @return MapleMap 类型结果
     */
    public MapleMap getForcedReturnMap() {
        return getChannelServer().getMapFactory().getMap(forcedReturnMap);
    }

    /**
     * 获取强制返回ID。
     * @return int 类型结果
     */
    public int getForcedReturnId() {
        return forcedReturnMap;
    }

    /**
     * 设置强制返回地图。
     * @param map 地图名称
     */
    public void setForcedReturnMap(int map) {
        this.forcedReturnMap = map;
    }

    /**
     * 获取时间限制。
     * @return int 类型结果
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * 设置时间限制。
     * @param timeLimit 时间限制（秒）
     */
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * 获取时间剩余。
     * @return int 类型结果
     */
    public int getTimeLeft() {
        return (int) ((mapTimer - System.currentTimeMillis()) / 1000);
    }

    /**
     * 设置反应堆状态。
     */
    public void setReactorState() {
        for (MapObject o : getMapObjects()) {
            if (o.getType() == MapObjectType.REACTOR) {
                if (((Reactor) o).getState() < 1) {
                    Reactor mr = (Reactor) o;
                    mr.lockReactor();
                    try {
                        mr.resetReactorActions(1);
                        broadcastMessage(PacketCreator.triggerReactor((Reactor) o, 1));
                    } finally {
                        mr.unlockReactor();
                    }
                }
            }
        }
    }

    /**
     * 限制反应堆。
     * @param rid 反应堆 ID
     * @param num 数量
     */
    public final void limitReactor(final int rid, final int num) {
        List<Reactor> toDestroy = new ArrayList<>();
        Map<Integer, Integer> contained = new LinkedHashMap<>();

        for (MapObject obj : getReactors()) {
            Reactor mr = (Reactor) obj;
            if (contained.containsKey(mr.getId())) {
                if (contained.get(mr.getId()) >= num) {
                    toDestroy.add(mr);
                } else {
                    contained.put(mr.getId(), contained.get(mr.getId()) + 1);
                }
            } else {
                contained.put(mr.getId(), 1);
            }
        }

        for (Reactor mr : toDestroy) {
            destroyReactor(mr.getObjectId());
        }
    }

    /**
     * 判断是否为所有反应堆状态。
     * @param reactorId 反应堆 ID
     * @param state 状态值
     * @return boolean 类型结果
     */
    public boolean isAllReactorState(final int reactorId, final int state) {
        for (MapObject mo : getReactors()) {
            Reactor r = (Reactor) mo;

            if (r.getId() == reactorId && r.getState() != state) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前队伍ID。
     * @return int 类型结果
     */
    public int getCurrentPartyId() {
        for (Character chr : this.getCharacters()) {
            if (chr.getPartyId() != -1) {
                return chr.getPartyId();
            }
        }
        return -1;
    }

    /**
     * 添加玩家NPC地图对象。
     * @param pnpcobject 玩家 NPC 对象
     */
    public void addPlayerNPCMapObject(PlayerNPC pnpcobject) {
        objectWLock.lock();
        try {
            this.mapobjects.put(pnpcobject.getObjectId(), pnpcobject);
        } finally {
            objectWLock.unlock();
        }
    }

    /**
     * 添加地图对象。
     * @param mapobject 地图对象
     */
    public void addMapObject(MapObject mapobject) {
        int curOID = getUsableOID();

        objectWLock.lock();
        try {
            mapobject.setObjectId(curOID);
            this.mapobjects.put(curOID, mapobject);
        } finally {
            objectWLock.unlock();
        }
    }

    /**
     * 添加自身自爆。
     * @param mob 怪物
     */
    public void addSelfDestructive(Monster mob) {
        if (mob.getStats().selfDestruction() != null) {
            this.selfDestructives.add(mob.getObjectId());
        }
    }

    /**
     * 移除自身自爆。
     * @param mapobjectid mapobjectid
     * @return boolean 类型结果
     */
    public boolean removeSelfDestructive(int mapobjectid) {
        return this.selfDestructives.remove(mapobjectid);
    }

    private void spawnAndAddRangedMapObject(MapObject mapobject, DelayedPacketCreation packetbakery) {
        spawnAndAddRangedMapObject(mapobject, packetbakery, null);
    }

    private void spawnAndAddRangedMapObject(MapObject mapobject, DelayedPacketCreation packetbakery, SpawnCondition condition) {
        List<Character> inRangeCharacters = new LinkedList<>();
        int curOID = getUsableOID();

        chrRLock.lock();
        objectWLock.lock();
        try {
            mapobject.setObjectId(curOID);
            this.mapobjects.put(curOID, mapobject);
            for (Character chr : characters) {
                if (condition == null || condition.canSpawn(chr)) {
                    if (chr.getPosition().distanceSq(mapobject.getPosition()) <= getRangedDistance()) {
                        inRangeCharacters.add(chr);
                        chr.addVisibleMapObject(mapobject);
                    }
                }
            }
        } finally {
            objectWLock.unlock();
            chrRLock.unlock();
        }

        for (Character chr : inRangeCharacters) {
            packetbakery.sendPackets(chr.getClient());
        }
    }

    private void spawnRangedMapObject(MapObject mapobject, DelayedPacketCreation packetbakery, SpawnCondition condition) {
        List<Character> inRangeCharacters = new LinkedList<>();

        chrRLock.lock();
        try {
            int curOID = getUsableOID();
            mapobject.setObjectId(curOID);
            for (Character chr : characters) {
                if (condition == null || condition.canSpawn(chr)) {
                    if (chr.getPosition().distanceSq(mapobject.getPosition()) <= getRangedDistance()) {
                        inRangeCharacters.add(chr);
                        chr.addVisibleMapObject(mapobject);
                    }
                }
            }
        } finally {
            chrRLock.unlock();
        }

        for (Character chr : inRangeCharacters) {
            packetbakery.sendPackets(chr.getClient());
        }
    }

    private int getUsableOID() {
        objectRLock.lock();
        try {
            int curOid;

            // clashes with playernpc on curOid >= 2147000000, developernpc uses >= 2147483000
            do {
                if ((curOid = runningOid.incrementAndGet()) >= 2147000000) {
                    runningOid.set(curOid = 1000000001);
                }
            } while (mapobjects.containsKey(curOid));

            return curOid;
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 移除地图对象。
     * @param num 数量
     */
    public void removeMapObject(int num) {
        objectWLock.lock();
        try {
            this.mapobjects.remove(num);
        } finally {
            objectWLock.unlock();
        }
    }

    /**
     * 移除地图对象。
     * @param obj 地图对象
     */
    public void removeMapObject(final MapObject obj) {
        removeMapObject(obj.getObjectId());
    }

    private Point calcPointBelow(Point initial) {
        Foothold fh = footholds.findBelow(initial);
        if (fh == null) {
            return null;
        }
        int dropY = fh.getY1();
        if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            double s1 = Math.abs(fh.getY2() - fh.getY1());
            double s2 = Math.abs(fh.getX2() - fh.getX1());
            double s5 = Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2)));
            if (fh.getY2() < fh.getY1()) {
                dropY = fh.getY1() - (int) s5;
            } else {
                dropY = fh.getY1() + (int) s5;
            }
        }
        return new Point(initial.x, dropY);
    }

    /**
     * 生成地图、掉落、范围、Cache。
     */
    public void generateMapDropRangeCache() {
        bndLock.lock();
        try {
            Pair<Integer, Integer> bounds = dropBoundsCache.get(mapid);

            if (bounds != null) {
                xLimits = bounds;
            } else {
                // assuming MINIMAP always have an equal-greater picture representation of the map area (players won't walk beyond the area known by the minimap).
                Point lp = new Point(mapArea.x, mapArea.y);
                Point rp = new Point(mapArea.x + mapArea.width, mapArea.y);
                Point fallback = new Point(mapArea.x + (mapArea.width / 2), mapArea.y);

                lp = bsearchDropPos(lp, fallback);  // approximated leftmost fh node position
                rp = bsearchDropPos(rp, fallback);  // approximated rightmost fh node position

                xLimits = new Pair<>(lp.x + 14, rp.x - 14);
                dropBoundsCache.put(mapid, xLimits);
            }
        } finally {
            bndLock.unlock();
        }
    }

    private Point bsearchDropPos(Point initial, Point fallback) {
        Point res, dropPos = null;

        int awayx = fallback.x;
        int homex = initial.x;

        int y = initial.y - 85;

        do {
            int distx = awayx - homex;
            int dx = distx / 2;

            int searchx = homex + dx;
            if ((res = calcPointBelow(new Point(searchx, y))) != null) {
                awayx = searchx;
                dropPos = res;
            } else {
                homex = searchx;
            }
        } while (Math.abs(homex - awayx) > 5);

        return (dropPos != null) ? dropPos : fallback;
    }

    /**
     * 计算掉落位置。
     * @param initial 初始坐标
     * @param fallback 回退坐标
     * @return Point 类型结果
     */
    public Point calcDropPos(Point initial, Point fallback) {
        if (initial.x < xLimits.left) {
            initial.x = xLimits.left;
        } else if (initial.x > xLimits.right) {
            initial.x = xLimits.right;
        }

        Point ret = calcPointBelow(new Point(initial.x, initial.y - 85));   // actual drop ranges: default - 120, explosive - 360
        if (ret == null) {
            ret = bsearchDropPos(initial, fallback);
        }

        if (!mapArea.contains(ret)) { // found drop pos outside the map :O
            return fallback;
        }

        return ret;
    }

    /**
     * 判断是否可以部署传送门。
     * @param pos 坐标
     * @return boolean 类型结果
     */
    public boolean canDeployDoor(Point pos) {
        Point toStep = calcPointBelow(pos);
        return toStep != null && toStep.distance(pos) <= 42;
    }

    /**
     * Fetches angle relative between spawn and door points where 3 O'Clock is 0
     * and 12 O'Clock is 270 degrees
     *
     * @param spawnPoint
     * @param doorPoint
     * @return angle in degress from 0-360.
     */
    private static double getAngle(Point doorPoint, Point spawnPoint) {
        double dx = doorPoint.getX() - spawnPoint.getX();
        // Minus to correct for coord re-mapping
        double dy = -(doorPoint.getY() - spawnPoint.getY());

        double inRads = Math.atan2(dy, dx);

        // We need to map to coord system when 0 degree is at 3 O'clock, 270 at 12 O'clock
        if (inRads < 0) {
            inRads = Math.abs(inRads);
        } else {
            inRads = 2 * Math.PI - inRads;
        }

        return Math.toDegrees(inRads);
    }

    /**
     * 获取取整坐标。
     * @param angle angle
     * @return String 类型结果
     */
    public static String getRoundedCoordinate(double angle) {
        String[] directions = {"E", "SE", "S", "SW", "W", "NW", "N", "NE", "E"};
        return directions[(int) Math.round(((angle % 360) / 45))];
    }

    /**
     * 获取传送门位置状态。
     * @param pos 坐标
     * @return Pair<String, Integer> 类型结果
     */
    public Pair<String, Integer> getDoorPositionStatus(Point pos) {
        Portal portal = findClosestPlayerSpawnpoint(pos);

        double angle = getAngle(portal.getPosition(), pos);
        double distn = pos.distanceSq(portal.getPosition());

        if (distn <= 777777.7) {
            return null;
        }

        distn = Math.sqrt(distn);
        return new Pair<>(getRoundedCoordinate(angle), (int) distn);
    }

    private static void sortDropEntries(List<MonsterDropEntry> from, List<MonsterDropEntry> item, List<MonsterDropEntry> visibleQuest, List<MonsterDropEntry> otherQuest, Character chr) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (MonsterDropEntry mde : from) {
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

    private byte dropItemsFromMonsterOnMap(List<MonsterDropEntry> dropEntry, Point pos, byte d, float chRate, byte droptype, int mobpos, Character chr, Monster mob) {
        if (dropEntry.isEmpty()) {
            return d;
        }

        Collections.shuffle(dropEntry);

        Item idrop;
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (final MonsterDropEntry de : dropEntry) {
            float cardRate = chr.getCardRate(de.itemId);
            int dropChance = (int) Math.min((float) de.chance * chRate * cardRate, Integer.MAX_VALUE);

            if (Randomizer.nextInt(999999) < dropChance) {
                if (droptype == 3) {
                    pos.x = mobpos + ((d % 2 == 0) ? (40 * ((d + 1) / 2)) : -(40 * (d / 2)));
                } else {
                    pos.x = mobpos + ((d % 2 == 0) ? (25 * ((d + 1) / 2)) : -(25 * (d / 2)));
                }
                if (de.itemId == 0) { // meso
                    int mesos = Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum;

                    if (mesos > 0) {
                        if (chr.getBuffedValue(BuffStat.MESOUP) != null) {
                            mesos = NumberTool.doubleToInt(mesos * chr.getBuffedValue(BuffStat.MESOUP).doubleValue() / 100.0);
                        }
                        mesos = NumberTool.floatToInt(mesos * chr.getMesoRate());
                        if (mesos <= 0) {
                            mesos = Integer.MAX_VALUE;
                        }

                        spawnMesoDrop(mesos, calcDropPos(pos, mob.getPosition()), mob, chr, false, droptype);
                    }
                } else {
                    if (ItemConstants.getInventoryType(de.itemId) == InventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        idrop = new Item(de.itemId, (short) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1));
                    }
                    spawnDrop(idrop, calcDropPos(pos, mob.getPosition()), mob, chr, droptype, de.questid);
                }
                d++;
            }
        }

        return d;
    }

    private byte dropGlobalItemsFromMonsterOnMap(List<MonsterGlobalDropEntry> globalEntry, Point pos, byte d, byte droptype, int mobpos, Character chr, Monster mob) {
        Collections.shuffle(globalEntry);

        Item idrop;
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (final MonsterGlobalDropEntry de : globalEntry) {
            if (Randomizer.nextInt(999999) < de.chance) {
                if (droptype == 3) {
                    pos.x = mobpos + (d % 2 == 0 ? (40 * (d + 1) / 2) : -(40 * (d / 2)));
                } else {
                    pos.x = mobpos + ((d % 2 == 0) ? (25 * (d + 1) / 2) : -(25 * (d / 2)));
                }
                if (de.itemId != 0) {
                    if (ItemConstants.getInventoryType(de.itemId) == InventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        idrop = new Item(de.itemId, (short) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1));
                    }
                    spawnDrop(idrop, calcDropPos(pos, mob.getPosition()), mob, chr, droptype, de.questid);
                    d++;
                }
            }
        }

        return d;
    }

    private void dropFromMonster(final Character chr, final Monster mob, final boolean useBaseRate) {
        if (mob.dropsDisabled() || !dropsOn) {
            return;
        }

        final byte droptype = (byte) (mob.getStats().isExplosiveReward() ? 3 : mob.getStats().isFfaLoot() ? 2 : chr.getParty() != null ? 1 : 0);
        final int mobpos = mob.getPosition().x;
        float chRate = !mob.isBoss() ? chr.getDropRate() : chr.getBossDropRate();
        Point pos = new Point(0, mob.getPosition().y);

        MonsterStatusEffect stati = mob.getStati(MonsterStatus.SHOWDOWN);
        if (stati != null) {
            chRate *= (stati.getStati().get(MonsterStatus.SHOWDOWN).doubleValue() / 100.0 + 1.0);
        }

        if (chr.isFamilyBuff()) {
            chRate *= chr.getFamilyDrop();
        }

        if (useBaseRate) {
            chRate = 1;
        }

        final MonsterInformationProvider mi = MonsterInformationProvider.getInstance();
        final List<MonsterGlobalDropEntry> globalEntry = mi.getRelevantGlobalDrops(this.getId());

        final List<MonsterDropEntry> dropEntry = new ArrayList<>();
        final List<MonsterDropEntry> visibleQuestEntry = new ArrayList<>();
        final List<MonsterDropEntry> otherQuestEntry = new ArrayList<>();

        List<MonsterDropEntry> lootEntry = GameConfig.getServerBoolean("use_spawn_relevant_loot") ? mob.retrieveRelevantDrops() : mi.retrieveEffectiveDrop(mob.getId());
        sortDropEntries(lootEntry, dropEntry, visibleQuestEntry, otherQuestEntry, chr);     // thanks Articuno, Limit, Rohenn for noticing quest loots not showing up in only-quest item drops scenario

        if (lootEntry.isEmpty()) {   // thanks resinate
            return;
        }

        registerMobItemDrops(droptype, mobpos, chRate, pos, dropEntry, visibleQuestEntry, otherQuestEntry, globalEntry, chr, mob);
    }

    /**
     * 掉落物品来自怪物。
     * @param list 掉落条目列表（MonsterDropEntry 列表/集合）
     * @param chr 角色
     * @param mob 怪物
     */
    public void dropItemsFromMonster(List<MonsterDropEntry> list, final Character chr, final Monster mob) {
        if (mob.dropsDisabled() || !dropsOn) {
            return;
        }

        final byte droptype = (byte) (chr.getParty() != null ? 1 : 0);
        final int mobpos = mob.getPosition().x;
        int chRate = 1000000;   // guaranteed item drop
        byte d = 1;
        Point pos = new Point(0, mob.getPosition().y);

        dropItemsFromMonsterOnMap(list, pos, d, chRate, droptype, mobpos, chr, mob);
    }

    /**
     * 掉落来自友好怪物。
     * @param chr 角色
     * @param mob 怪物
     */
    public void dropFromFriendlyMonster(final Character chr, final Monster mob) {
        dropFromMonster(chr, mob, true);
    }

    /**
     * 掉落来自反应堆。
     * @param chr 角色
     * @param reactor 反应堆
     * @param drop 掉落物品
     * @param dropPos 掉落坐标
     * @param questid 任务 ID
     */
    public void dropFromReactor(final Character chr, final Reactor reactor, Item drop, Point dropPos, short questid) {
        spawnDrop(drop, this.calcDropPos(dropPos, reactor.getPosition()), reactor, chr, (byte) (chr.getParty() != null ? 1 : 0), questid);
    }

    private void stopItemMonitor() {
        itemMonitor.cancel(false);
        itemMonitor = null;

        expireItemsTask.cancel(false);
        expireItemsTask = null;

        if (GameConfig.getServerBoolean("use_spawn_loot_on_animation")) {
            mobSpawnLootTask.cancel(false);
            mobSpawnLootTask = null;
        }

        characterStatUpdateTask.cancel(false);
        characterStatUpdateTask = null;
    }

    private void cleanItemMonitor() {
        objectWLock.lock();
        try {
            registeredDrops.removeAll(Collections.singleton(null));
        } finally {
            objectWLock.unlock();
        }
    }

    private void startItemMonitor() {
        chrWLock.lock();
        try {
            if (itemMonitor != null) {
                return;
            }

            itemMonitor = TimerManager.getInstance().register(() -> {
                chrWLock.lock();
                try {
                    if (characters.isEmpty()) {
                        if (itemMonitorTimeout == 0) {
                            if (itemMonitor != null) {
                                stopItemMonitor();
                                aggroMonitor.stopAggroCoordinator();
                            }

                            return;
                        } else {
                            itemMonitorTimeout--;
                        }
                    } else {
                        itemMonitorTimeout = 1;
                    }
                } finally {
                    chrWLock.unlock();
                }

                boolean tryClean;
                objectRLock.lock();
                try {
                    tryClean = registeredDrops.size() > 70;
                } finally {
                    objectRLock.unlock();
                }

                if (tryClean) {
                    cleanItemMonitor();
                }
            }, GameConfig.getServerLong("item_monitor_time"), GameConfig.getServerLong("item_monitor_time"));

            expireItemsTask = TimerManager.getInstance().register(this::makeDisappearExpiredItemDrops, GameConfig.getServerLong("item_expire_check"), GameConfig.getServerLong("item_expire_check"));

            if (GameConfig.getServerBoolean("use_spawn_loot_on_animation")) {
                lootLock.lock();
                try {
                    mobLootEntries.clear();
                } finally {
                    lootLock.unlock();
                }

                mobSpawnLootTask = TimerManager.getInstance().register(this::spawnMobItemDrops, 200, 200);
            }

            characterStatUpdateTask = TimerManager.getInstance().register(this::runCharacterStatUpdate, 200, 200);

            itemMonitorTimeout = 1;
        } finally {
            chrWLock.unlock();
        }
    }

    private boolean hasItemMonitor() {
        chrRLock.lock();
        try {
            return itemMonitor != null;
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 获取已掉落物品数量。
     * @return int 类型结果
     */
    public int getDroppedItemCount() {
        return droppedItemCount.get();
    }

    private void instantiateItemDrop(MapItem mdrop) {
        if (droppedItemCount.get() >= GameConfig.getServerInt("item_limit_on_map")) {
            MapObject mapobj;

            do {
                mapobj = null;

                objectWLock.lock();
                try {
                    while (mapobj == null) {
                        if (registeredDrops.isEmpty()) {
                            break;
                        }
                        mapobj = registeredDrops.remove(0).get();
                    }
                } finally {
                    objectWLock.unlock();
                }
            } while (!makeDisappearItemFromMap(mapobj));
        }

        objectWLock.lock();
        try {
            registerItemDrop(mdrop);
            registeredDrops.add(new WeakReference<>(mdrop));
        } finally {
            objectWLock.unlock();
        }

        droppedItemCount.incrementAndGet();
    }

    private void registerItemDrop(MapItem mdrop) {
        droppedItems.put(mdrop, !everlast ? Server.getInstance().getCurrentTime() + GameConfig.getServerLong("item_expire_time") : Long.MAX_VALUE);
    }

    private void unregisterItemDrop(MapItem mdrop) {
        objectWLock.lock();
        try {
            droppedItems.remove(mdrop);
        } finally {
            objectWLock.unlock();
        }
    }

    private void makeDisappearExpiredItemDrops() {
        List<MapItem> toDisappear = new LinkedList<>();

        objectRLock.lock();
        try {
            long timeNow = Server.getInstance().getCurrentTime();

            for (Entry<MapItem, Long> it : droppedItems.entrySet()) {
                if (it.getValue() < timeNow) {
                    toDisappear.add(it.getKey());
                }
            }
        } finally {
            objectRLock.unlock();
        }

        for (MapItem mmi : toDisappear) {
            makeDisappearItemFromMap(mmi);
        }

        objectWLock.lock();
        try {
            for (MapItem mmi : toDisappear) {
                droppedItems.remove(mmi);
            }
        } finally {
            objectWLock.unlock();
        }
    }

    private void registerMobItemDrops(byte droptype, int mobpos, float chRate, Point pos, List<MonsterDropEntry> dropEntry, List<MonsterDropEntry> visibleQuestEntry, List<MonsterDropEntry> otherQuestEntry, List<MonsterGlobalDropEntry> globalEntry, Character chr, Monster mob) {
        MobLootEntry mle = new MobLootEntry(droptype, mobpos, chRate, pos, dropEntry, visibleQuestEntry, otherQuestEntry, globalEntry, chr, mob);

        if (GameConfig.getServerBoolean("use_spawn_loot_on_animation")) {
            int animationTime = mob.getAnimationTime("die1");

            lootLock.lock();
            try {
                long timeNow = Server.getInstance().getCurrentTime();
                mobLootEntries.put(mle, timeNow + ((long) (0.42 * animationTime)));
            } finally {
                lootLock.unlock();
            }
        } else {
            mle.run();
        }
    }

    private void spawnMobItemDrops() {
        Set<Entry<MobLootEntry, Long>> mleList;

        lootLock.lock();
        try {
            mleList = new HashSet<>(mobLootEntries.entrySet());
        } finally {
            lootLock.unlock();
        }

        long timeNow = Server.getInstance().getCurrentTime();
        List<MobLootEntry> toRemove = new LinkedList<>();
        for (Entry<MobLootEntry, Long> mlee : mleList) {
            if (mlee.getValue() < timeNow) {
                toRemove.add(mlee.getKey());
            }
        }

        if (!toRemove.isEmpty()) {
            List<MobLootEntry> toSpawnLoot = new LinkedList<>();

            lootLock.lock();
            try {
                for (MobLootEntry mle : toRemove) {
                    Long mler = mobLootEntries.remove(mle);
                    if (mler != null) {
                        toSpawnLoot.add(mle);
                    }
                }
            } finally {
                lootLock.unlock();
            }

            for (MobLootEntry mle : toSpawnLoot) {
                mle.run();
            }
        }
    }

    private List<MapItem> getDroppedItems() {
        objectRLock.lock();
        try {
            return new LinkedList<>(droppedItems.keySet());
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 获取已掉落物品数量按ID。
     * @param itemid 物品 ID
     * @return int 类型结果
     */
    public int getDroppedItemsCountById(int itemid) {
        int count = 0;
        for (MapItem mmi : getDroppedItems()) {
            if (mmi.getItemId() == itemid) {
                count++;
            }
        }

        return count;
    }

    /**
     * 拾取物品掉落。
     * @param pickupPacket 拾取数据包
     */
    public void pickItemDrop(Packet pickupPacket, MapItem mdrop) { // mdrop must be already locked and not-pickedup checked at this point
        broadcastMessage(pickupPacket, mdrop.getPosition());

        droppedItemCount.decrementAndGet();
        this.removeMapObject(mdrop);
        mdrop.setPickedUp(true);
        unregisterItemDrop(mdrop);
    }

    /**
     * 更新玩家物品掉落到队伍。
     * @param partyid 队伍 ID
     * @param charid 角色 ID
     * @param partyMembers 队伍成员（Character 列表/集合）
     * @param partyLeaver 离开队伍的角色
     * @return List<MapItem> 类型结果
     */
    public List<MapItem> updatePlayerItemDropsToParty(int partyid, int charid, List<Character> partyMembers, Character partyLeaver) {
        List<MapItem> partyDrops = new LinkedList<>();

        for (MapItem mdrop : getDroppedItems()) {
            if (mdrop.getOwnerId() == charid) {
                mdrop.lockItem();
                try {
                    if (mdrop.isPickedUp()) {
                        continue;
                    }

                    mdrop.setPartyOwnerId(partyid);

                    Packet removePacket = PacketCreator.silentRemoveItemFromMap(mdrop.getObjectId());
                    Packet updatePacket = PacketCreator.updateMapItemObject(mdrop, partyLeaver == null);

                    for (Character mc : partyMembers) {
                        if (this.equals(mc.getMap())) {
                            mc.sendPacket(removePacket);

                            if (mc.needQuestItem(mdrop.getQuest(), mdrop.getItemId())) {
                                mc.sendPacket(updatePacket);
                            }
                        }
                    }

                    if (partyLeaver != null) {
                        if (this.equals(partyLeaver.getMap())) {
                            partyLeaver.sendPacket(removePacket);

                            if (partyLeaver.needQuestItem(mdrop.getQuest(), mdrop.getItemId())) {
                                partyLeaver.sendPacket(PacketCreator.updateMapItemObject(mdrop, true));
                            }
                        }
                    }
                } finally {
                    mdrop.unlockItem();
                }
            } else if (partyid != -1 && mdrop.getPartyOwnerId() == partyid) {
                partyDrops.add(mdrop);
            }
        }

        return partyDrops;
    }

    /**
     * 更新队伍、物品、掉落、到、新加入者。
     * @param newcomer 新加入角色
     * @param partyItems 队伍掉落物列表（MapItem 列表/集合）
     */
    public void updatePartyItemDropsToNewcomer(Character newcomer, List<MapItem> partyItems) {
        for (MapItem mdrop : partyItems) {
            mdrop.lockItem();
            try {
                if (mdrop.isPickedUp()) {
                    continue;
                }

                Packet removePacket = PacketCreator.silentRemoveItemFromMap(mdrop.getObjectId());
                Packet updatePacket = PacketCreator.updateMapItemObject(mdrop, true);

                if (newcomer != null) {
                    if (this.equals(newcomer.getMap())) {
                        newcomer.sendPacket(removePacket);

                        if (newcomer.needQuestItem(mdrop.getQuest(), mdrop.getItemId())) {
                            newcomer.sendPacket(updatePacket);
                        }
                    }
                }
            } finally {
                mdrop.unlockItem();
            }
        }
    }

    private void spawnDrop(final Item idrop, final Point dropPos, final MapObject dropper, final Character chr, final byte droptype, final short questid) {
        final MapItem mdrop = new MapItem(idrop, dropPos, dropper, chr, chr.getClient(), droptype, false, questid);
        mdrop.setDropTime(Server.getInstance().getCurrentTime());
        spawnAndAddRangedMapObject(mdrop, c -> {
            Character chr1 = c.getPlayer();

            if (chr1.needQuestItem(questid, idrop.getItemId())) {
                mdrop.lockItem();
                try {
                    c.sendPacket(PacketCreator.dropItemFromMapObject(chr1, mdrop, dropper.getPosition(), dropPos, (byte) 1));
                } finally {
                    mdrop.unlockItem();
                }
            }
        }, null);

        instantiateItemDrop(mdrop);
        activateItemReactors(mdrop, chr.getClient());
    }

    /**
     * 生成金币掉落。
     * @param meso 金币数量
     * @param position 坐标
     * @param dropper 掉落来源
     * @param owner 归属角色
     * @param playerDrop 是否玩家丢弃
     * @param droptype 掉落类型
     */
    public final void spawnMesoDrop(final int meso, final Point position, final MapObject dropper, final Character owner, final boolean playerDrop, final byte droptype) {
        final Point droppos = calcDropPos(position, position);
        final MapItem mdrop = new MapItem(meso, droppos, dropper, owner, owner.getClient(), droptype, playerDrop);
        mdrop.setDropTime(Server.getInstance().getCurrentTime());

        spawnAndAddRangedMapObject(mdrop, c -> {
            mdrop.lockItem();
            try {
                c.sendPacket(PacketCreator.dropItemFromMapObject(c.getPlayer(), mdrop, dropper.getPosition(), droppos, (byte) 1));
            } finally {
                mdrop.unlockItem();
            }
        }, null);

        instantiateItemDrop(mdrop);
    }

    /**
     * 执行 disappearing、物品、掉落 操作。
     * @param dropper 掉落来源
     * @param owner 归属角色
     * @param item item
     * @param pos 坐标
     */
    public final void disappearingItemDrop(final MapObject dropper, final Character owner, final Item item, final Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final MapItem mdrop = new MapItem(item, droppos, dropper, owner, owner.getClient(), (byte) 1, false);

        mdrop.lockItem();
        try {
            broadcastItemDropMessage(mdrop, dropper.getPosition(), droppos, (byte) 3, mdrop.getPosition());
        } finally {
            mdrop.unlockItem();
        }
    }

    /**
     * 执行 disappearing、金币、掉落 操作。
     * @param meso 金币数量
     * @param dropper 掉落来源
     * @param owner 归属角色
     * @param pos 坐标
     */
    public final void disappearingMesoDrop(final int meso, final MapObject dropper, final Character owner, final Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final MapItem mdrop = new MapItem(meso, droppos, dropper, owner, owner.getClient(), (byte) 1, false);

        mdrop.lockItem();
        try {
            broadcastItemDropMessage(mdrop, dropper.getPosition(), droppos, (byte) 3, mdrop.getPosition());
        } finally {
            mdrop.unlockItem();
        }
    }

    /**
     * 获取怪物按ID。
     * @param id ID
     * @return Monster 类型结果
     */
    public Monster getMonsterById(int id) {
        objectRLock.lock();
        try {
            for (MapObject obj : mapobjects.values()) {
                if (obj.getType() == MapObjectType.MONSTER) {
                    if (((Monster) obj).getId() == id) {
                        return (Monster) obj;
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
        return null;
    }

    /**
     * 统计怪物数量。
     * @param id ID
     * @return int 类型结果
     */
    public int countMonster(int id) {
        return countMonster(id, id);
    }

    /**
     * 统计怪物数量。
     * @param minid 最小 ID
     * @param maxid 最大 ID
     * @return int 类型结果
     */
    public int countMonster(int minid, int maxid) {
        int count = 0;
        for (MapObject m : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.MONSTER))) {
            Monster mob = (Monster) m;
            if (mob.getId() >= minid && mob.getId() <= maxid) {
                count++;
            }
        }
        return count;
    }

    /**
     * 统计怪物数量。
     * @return int 类型结果
     */
    public int countMonsters() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.MONSTER)).size();
    }

    /**
     * 统计反应堆数量。
     * @return int 类型结果
     */
    public int countReactors() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.REACTOR)).size();
    }

    /**
     * 获取反应堆。
     * @return List<MapObject> 类型结果
     */
    public final List<MapObject> getReactors() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.REACTOR));
    }

    /**
     * 获取怪物。
     * @return List<MapObject> 类型结果
     */
    public final List<MapObject> getMonsters() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.MONSTER));
    }

    /**
     * 获取所有反应堆。
     * @return List<Reactor> 类型结果
     */
    public final List<Reactor> getAllReactors() {
        List<Reactor> list = new LinkedList<>();
        for (MapObject mmo : getReactors()) {
            list.add((Reactor) mmo);
        }

        return list;
    }

    /**
     * 获取所有怪物。
     * @return List<Monster> 类型结果
     */
    public final List<Monster> getAllMonsters() {
        List<Monster> list = new LinkedList<>();
        for (MapObject mmo : getMonsters()) {
            list.add((Monster) mmo);
        }

        return list;
    }

    /**
     * 统计物品数量。
     * @return int 类型结果
     */
    public int countItems() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.ITEM)).size();
    }

    /**
     * 获取物品。
     * @return List<MapObject> 类型结果
     */
    public final List<MapObject> getItems() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.ITEM));
    }

    /**
     * 统计玩家数量。
     * @return int 类型结果
     */
    public int countPlayers() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.PLAYER)).size();
    }

    /**
     * 获取玩家。
     * @return List<MapObject> 类型结果
     */
    public List<MapObject> getPlayers() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.PLAYER));
    }

    /**
     * 获取所有玩家。
     * @return List<Character> 类型结果
     */
    public List<Character> getAllPlayers() {
        List<Character> character;
        chrRLock.lock();
        try {
            character = new ArrayList<>(characters);
        } finally {
            chrRLock.unlock();
        }

        return character;
    }

    /**
     * 获取地图所有玩家。
     * @return Map<Integer, Character> 类型结果
     */
    public Map<Integer, Character> getMapAllPlayers() {
        Map<Integer, Character> pchars = new HashMap<>();
        for (Character chr : this.getAllPlayers()) {
            pchars.put(chr.getId(), chr);
        }

        return pchars;
    }

    /**
     * 获取玩家在范围。
     * @param box 矩形区域
     * @return List<Character> 类型结果
     */
    public List<Character> getPlayersInRange(Rectangle box) {
        List<Character> character = new LinkedList<>();
        chrRLock.lock();
        try {
            for (Character chr : characters) {
                if (box.contains(chr.getPosition())) {
                    character.add(chr);
                }
            }
        } finally {
            chrRLock.unlock();
        }

        return character;
    }

    /**
     * 统计存活玩家数量。
     * @return int 类型结果
     */
    public int countAlivePlayers() {
        int count = 0;

        for (Character mc : getAllPlayers()) {
            if (mc.isAlive()) {
                count++;
            }
        }

        return count;
    }

    /**
     * 统计Boss数量。
     * @return int 类型结果
     */
    public int countBosses() {
        int count = 0;

        for (Monster mob : getAllMonsters()) {
            if (mob.isBoss()) {
                count++;
            }
        }

        return count;
    }

    /**
     * 对怪物造成伤害。
     * @param chr 角色
     * @param monster 怪物
     * @param damage 伤害值
     * @return boolean 类型结果
     */
    public boolean damageMonster(final Character chr, final Monster monster, final int damage) {
        if (monster.getId() == MobId.ZAKUM_1) {
            for (MapObject object : chr.getMap().getMapObjects()) {
                Monster mons = chr.getMap().getMonsterByOid(object.getObjectId());
                if (mons != null) {
                    if (mons.getId() >= MobId.ZAKUM_ARM_1 && mons.getId() <= MobId.ZAKUM_ARM_8) {
                        return true;
                    }
                }
            }
        }
        if (monster.isAlive()) {
            boolean killed = monster.damage(chr, damage, false);

            selfDestruction selfDestr = monster.getStats().selfDestruction();
            if (selfDestr != null && selfDestr.getHp() > -1) {// should work ;p
                if (monster.getHp() <= selfDestr.getHp()) {
                    killMonster(monster, chr, true, selfDestr.getAction());
                    return true;
                }
            }
            if (killed) {
                killMonster(monster, chr, true);
            }
            return true;
        }
        return false;
    }

    // 巴洛古(Balrog)讨伐胜利广播
    /**
     * 向地图广播蝙蝠魔胜利。
     * @param leaderName 队长名称
     */
    public void broadcastBalrogVictory(String leaderName) {
        getWorldServer().dropMessage(6,"[远征凯旋] " + leaderName + "的远征队成功讨伐了火焰魔神巴洛古！" + "让我们歌颂这支队伍，他们以" + countAlivePlayers() + "名幸存者的战绩完成了壮举！");
    }

    // 暗黑龙王(Horntail)讨伐胜利广播
    /**
     * 向地图广播黑龙胜利。
     */
    public void broadcastHorntailVictory() {
        getWorldServer().dropMessage(6,"[远征凯旋] 致历经无数次挑战最终征服暗黑龙王的勇士们：" + "谨以此礼赞献给真正的神木村英雄！");
    }

    // 扎昆(Zakum)讨伐胜利广播
    /**
     * 向地图广播扎昆胜利。
     */
    public void broadcastZakumVictory() {
        getWorldServer().dropMessage(6,"[远征凯旋] 长久笼罩天空之城的邪恶之树终于倾倒！" +"致那些历经无数次尝试最终征服扎昆的远征队，胜利属于你们！" +"你们是天空之城真正的传说！");
    }

    // 品克缤(PinkBean)讨伐胜利广播
    /**
     * 向地图广播Pink、Bean、胜利。
     * @param channel 频道号
     */
    public void broadcastPinkBeanVictory(int channel) {
        getWorldServer().dropMessage(6,"[远征凯旋] 在" + channel + "频道挑战品克缤的远征队，" +  "以雷霆之势完成了终极讨伐！时间神殿重现璀璨光辉，" + "当英雄们从战场凯旋之时，被夺走的白昼终于归来！"
        );
    }


    private boolean removeKilledMonsterObject(Monster monster) {
        monster.lockMonster();
        try {
            if (monster.getHp() < 0) {
                return false;
            }

            spawnedMonstersOnMap.decrementAndGet();
            removeMapObject(monster);
            monster.disposeMapObject();
            if (monster.hasBossHPBar()) {   // thanks resinate for noticing boss HPbar not clearing after mob defeat in certain scenarios   //感谢resinate注意到在某些情况下暴徒失败后老板HPbar没有清除
                broadcastBossHpMessage(monster, monster.hashCode(), monster.makeBossHPBarPacket(), monster.getPosition());
            }

            return true;
        } finally {
            monster.unlockMonster();
        }
    }

    /**
     * 击杀怪物。
     * @param monster 怪物
     * @param chr 角色
     * @param withDrops 是否产生掉落
     */
    public void killMonster(final Monster monster, final Character chr, final boolean withDrops) {
        killMonster(monster, chr, withDrops, 1);
    }

    /**
     * 击杀怪物。
     * @param monster 怪物
     * @param chr 角色
     * @param withDrops 是否产生掉落
     * @param animation 死亡动画类型
     */
    public void killMonster(final Monster monster, final Character chr, final boolean withDrops, int animation) {
        if (monster == null) {
            return;
        }

        if (chr == null) {
            if (removeKilledMonsterObject(monster)) {
                monster.dispatchMonsterKilled(false);
                broadcastMessage(PacketCreator.killMonster(monster.getObjectId(), animation), monster.getPosition());
                monster.aggroSwitchController(null, false);
            }
        } else {
            if (removeKilledMonsterObject(monster)) {
                try {
                    if (monster.getStats().getLevel() >= chr.getLevel() + 30 && !chr.isGM()) {
                        AutobanFactory.GENERAL.alert(chr, "因击杀超过自身30级的怪物[" + monster.getName() + "]被系统警告");
                    }

                    /*if (chr.getQuest(Quest.getInstance(29400)).getStatus().equals(QuestStatus.Status.STARTED)) {
                     if (chr.getLevel() >= 120 && monster.getStats().getLevel() >= 120) {
                     //FIX MEDAL SHET
                     } else if (monster.getStats().getLevel() >= chr.getLevel()) {
                     }
                     }*/

                    if (monster.getCP() > 0 && chr.getMap().isCPQMap()) {
                        chr.gainCP(monster.getCP());
                    }

                    int buff = monster.getBuffToGive();
                    if (buff > -1) {
                        ItemInformationProvider mii = ItemInformationProvider.getInstance();
                        for (MapObject mmo : this.getPlayers()) {
                            Character character = (Character) mmo;
                            if (character.isAlive()) {
                                StatEffect statEffect = mii.getItemEffect(buff);
                                character.sendPacket(PacketCreator.showOwnBuffEffect(buff, 1));
                                broadcastMessage(character, PacketCreator.showBuffEffect(character.getId(), buff, 1), false);
                                statEffect.applyTo(character);
                            }
                        }
                    }

                    if (MobId.isZakumArm(monster.getId())) {
                        boolean makeZakReal = true;
                        Collection<MapObject> objects = getMapObjects();
                        for (MapObject object : objects) {
                            Monster mons = getMonsterByOid(object.getObjectId());
                            if (mons != null) {
                                if (MobId.isZakumArm(mons.getId())) {
                                    makeZakReal = false;
                                    break;
                                }
                            }
                        }
                        if (makeZakReal) {
                            MapleMap map = chr.getMap();

                            for (MapObject object : objects) {
                                Monster mons = map.getMonsterByOid(object.getObjectId());
                                if (mons != null) {
                                    if (mons.getId() == MobId.ZAKUM_1) {
                                        makeMonsterReal(mons);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    Character dropOwner = monster.killBy(chr);
                    if (withDrops && !monster.dropsDisabled()) {
                        if (dropOwner == null) {
                            dropOwner = chr;
                        }
                        dropFromMonster(dropOwner, monster, false);
                    }

                    if (monster.hasBossHPBar()) {
                        for (Character mc : this.getAllPlayers()) {
                            if (mc.getTargetHpBarHash() == monster.hashCode()) {
                                mc.resetPlayerAggro();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {     // thanks resinate for pointing out a memory leak possibly from an exception thrown
                    monster.dispatchMonsterKilled(true);
                    broadcastMessage(PacketCreator.killMonster(monster.getObjectId(), animation), monster.getPosition());
                }
            }
        }
    }

    /**
     * 击杀Friendlies。
     * @param mob 怪物
     */
    public void killFriendlies(Monster mob) {
        this.killMonster(mob, (Character) getPlayers().get(0), false);
    }

    /**
     * 击杀怪物。
     * @param mobId mobId
     */
    public void killMonster(int mobId) {
        Character chr = (Character) getPlayers().get(0);
        List<Monster> mobList = getAllMonsters();

        for (Monster mob : mobList) {
            if (mob.getId() == mobId) {
                this.killMonster(mob, chr, false);
            }
        }
    }

    /**
     * 击杀怪物带掉落。
     * @param mobId mobId
     */
    public void killMonsterWithDrops(int mobId) {
        Map<Integer, Character> mapChars = this.getMapPlayers();

        if (!mapChars.isEmpty()) {
            Character defaultChr = mapChars.entrySet().iterator().next().getValue();
            List<Monster> mobList = getAllMonsters();

            for (Monster mob : mobList) {
                if (mob.getId() == mobId) {
                    Character chr = mapChars.get(mob.getHighestDamagerId());
                    if (chr == null) {
                        chr = defaultChr;
                    }

                    this.killMonster(mob, chr, true);
                }
            }
        }
    }

    /**
     * 执行 soft、击杀、所有、怪物 操作。
     */
    public void softKillAllMonsters() {
        closeMapSpawnPoints();

        for (MapObject monstermo : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.MONSTER))) {
            Monster monster = (Monster) monstermo;
            if (monster.getStats().isFriendly()) {
                continue;
            }

            if (removeKilledMonsterObject(monster)) {
                monster.dispatchMonsterKilled(false);
            }
        }
    }

    /**
     * 击杀所有怪物非友好。
     */
    public void killAllMonstersNotFriendly() {
        closeMapSpawnPoints();

        for (MapObject monstermo : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.MONSTER))) {
            Monster monster = (Monster) monstermo;
            if (monster.getStats().isFriendly()) {
                continue;
            }

            killMonster(monster, null, false, 1);
        }
    }

    /**
     * 击杀所有怪物。
     */
    public void killAllMonsters() {
        closeMapSpawnPoints();

        for (MapObject monstermo : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.MONSTER))) {
            Monster monster = (Monster) monstermo;

            killMonster(monster, null, false, 1);
        }
    }

    /**
     * 销毁反应堆。
     * @param first 起始编号
     * @param last 结束编号
     */
    public final void destroyReactors(final int first, final int last) {
        List<Reactor> toDestroy = new ArrayList<>();
        List<MapObject> reactors = getReactors();

        for (MapObject obj : reactors) {
            Reactor mr = (Reactor) obj;
            if (mr.getId() >= first && mr.getId() <= last) {
                toDestroy.add(mr);
            }
        }

        for (Reactor mr : toDestroy) {
            destroyReactor(mr.getObjectId());
        }
    }

    /**
     * 销毁反应堆。
     * @param oid 对象 ID
     */
    public void destroyReactor(int oid) {
        final Reactor reactor = getReactorByOid(oid);

        if (reactor != null) {
            if (reactor.destroy()) {
                removeMapObject(reactor);
            }
        }
    }

    /**
     * 重置反应堆。
     */
    public void resetReactors() {
        List<Reactor> list = new ArrayList<>();

        objectRLock.lock();
        try {
            for (MapObject o : mapobjects.values()) {
                if (o.getType() == MapObjectType.REACTOR) {
                    final Reactor r = ((Reactor) o);
                    list.add(r);
                }
            }
        } finally {
            objectRLock.unlock();
        }

        resetReactors(list);
    }

    /**
     * 重置反应堆。
     * @param list 掉落条目列表（Reactor 列表/集合）
     */
    public final void resetReactors(List<Reactor> list) {
        for (Reactor r : list) {
            if (r.forceDelayedRespawn()) {  // thanks Conrad for suggesting reactor with delay respawning immediately
                continue;
            }

            r.lockReactor();
            try {
                r.resetReactorActions(0);
                r.setAlive(true);
                broadcastMessage(PacketCreator.triggerReactor(r, 0));
            } finally {
                r.unlockReactor();
            }
        }
    }

    /**
     * 执行 shuffle、反应堆 操作。
     */
    public void shuffleReactors() {
        List<Point> points = new ArrayList<>();
        objectRLock.lock();
        try {
            for (MapObject o : mapobjects.values()) {
                if (o.getType() == MapObjectType.REACTOR) {
                    points.add(o.getPosition());
                }
            }
            Collections.shuffle(points);
            for (MapObject o : mapobjects.values()) {
                if (o.getType() == MapObjectType.REACTOR) {
                    o.setPosition(points.remove(points.size() - 1));
                }
            }
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 执行 shuffle、反应堆 操作。
     * @param first 起始编号
     * @param last 结束编号
     */
    public final void shuffleReactors(int first, int last) {
        List<Point> points = new ArrayList<>();
        List<MapObject> reactors = getReactors();
        List<MapObject> targets = new LinkedList<>();

        for (MapObject obj : reactors) {
            Reactor mr = (Reactor) obj;
            if (mr.getId() >= first && mr.getId() <= last) {
                points.add(mr.getPosition());
                targets.add(obj);
            }
        }
        Collections.shuffle(points);
        for (MapObject obj : targets) {
            Reactor mr = (Reactor) obj;
            mr.setPosition(points.remove(points.size() - 1));
        }
    }

    /**
     * 执行 shuffle、反应堆 操作。
     * @param list 掉落条目列表（Object 列表/集合）
     */
    public final void shuffleReactors(List<Object> list) {
        List<Point> points = new ArrayList<>();
        List<MapObject> listObjects = new ArrayList<>();
        List<MapObject> targets = new LinkedList<>();

        objectRLock.lock();
        try {
            for (Object ob : list) {
                if (ob instanceof MapObject mmo) {

                    if (mapobjects.containsValue(mmo) && mmo.getType() == MapObjectType.REACTOR) {
                        listObjects.add(mmo);
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }

        for (MapObject obj : listObjects) {
            Reactor mr = (Reactor) obj;

            points.add(mr.getPosition());
            targets.add(obj);
        }
        Collections.shuffle(points);
        for (MapObject obj : targets) {
            Reactor mr = (Reactor) obj;
            mr.setPosition(points.remove(points.size() - 1));
        }
    }

    private Map<Integer, MapObject> getCopyMapObjects() {
        objectRLock.lock();
        try {
            return new HashMap<>(mapobjects);
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 获取地图对象。
     * @return List<MapObject> 类型结果
     */
    public List<MapObject> getMapObjects() {
        objectRLock.lock();
        try {
            return new LinkedList(mapobjects.values());
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 获取NPC按ID。
     * @param id ID
     * @return NPC 类型结果
     */
    public NPC getNPCById(int id) {
        for (MapObject obj : getMapObjects()) {
            if (obj.getType() == MapObjectType.NPC) {
                NPC npc = (NPC) obj;
                if (npc.getId() == id) {
                    return npc;
                }
            }
        }

        return null;
    }

    /**
     * 执行 contains、N、P、C 操作。
     * @param npcid NPC ID
     * @return boolean 类型结果
     */
    public boolean containsNPC(int npcid) {
        objectRLock.lock();
        try {
            for (MapObject obj : mapobjects.values()) {
                if (obj.getType() == MapObjectType.NPC) {
                    if (((NPC) obj).getId() == npcid) {
                        return true;
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
        return false;
    }

    /**
     * 销毁NPC。
     */
    public void destroyNPC(int npcid) {     // assumption: there's at most one of the same NPC in a map.
        List<MapObject> npcs = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.NPC));

        chrRLock.lock();
        objectWLock.lock();
        try {
            for (MapObject obj : npcs) {
                if (((NPC) obj).getId() == npcid) {
                    broadcastMessage(PacketCreator.removeNPCController(obj.getObjectId()));
                    broadcastMessage(PacketCreator.removeNPC(obj.getObjectId()));

                    this.mapobjects.remove(obj.getObjectId());
                }
            }
        } finally {
            objectWLock.unlock();
            chrRLock.unlock();
        }
    }

    /**
     * 获取地图对象。
     * @param oid 对象 ID
     * @return MapObject 类型结果
     */
    public MapObject getMapObject(int oid) {
        objectRLock.lock();
        try {
            return mapobjects.get(oid);
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 获取怪物、按、对象 ID。
     * @param oid 对象 ID
     * @return Monster 类型结果
     */
    public Monster getMonsterByOid(int oid) {
        MapObject mmo = getMapObject(oid);
        return (mmo != null && mmo.getType() == MapObjectType.MONSTER) ? (Monster) mmo : null;
    }

    /**
     * 获取反应堆、按、对象 ID。
     * @param oid 对象 ID
     * @return Reactor 类型结果
     */
    public Reactor getReactorByOid(int oid) {
        MapObject mmo = getMapObject(oid);
        return (mmo != null && mmo.getType() == MapObjectType.REACTOR) ? (Reactor) mmo : null;
    }

    /**
     * 获取反应堆按ID。
     * @param Id Id
     * @return Reactor 类型结果
     */
    public Reactor getReactorById(int Id) {
        objectRLock.lock();
        try {
            for (MapObject obj : mapobjects.values()) {
                if (obj.getType() == MapObjectType.REACTOR) {
                    if (((Reactor) obj).getId() == Id) {
                        return (Reactor) obj;
                    }
                }
            }
            return null;
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 获取反应堆按ID范围。
     * @param first 起始编号
     * @param last 结束编号
     * @return List<Reactor> 类型结果
     */
    public List<Reactor> getReactorsByIdRange(final int first, final int last) {
        List<Reactor> list = new LinkedList<>();

        objectRLock.lock();
        try {
            for (MapObject obj : mapobjects.values()) {
                if (obj.getType() == MapObjectType.REACTOR) {
                    Reactor mr = (Reactor) obj;

                    if (mr.getId() >= first && mr.getId() <= last) {
                        list.add(mr);
                    }
                }
            }

            return list;
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 获取反应堆按名称。
     * @param name name
     * @return Reactor 类型结果
     */
    public Reactor getReactorByName(String name) {
        objectRLock.lock();
        try {
            for (MapObject obj : mapobjects.values()) {
                if (obj.getType() == MapObjectType.REACTOR) {
                    if (((Reactor) obj).getName().equals(name)) {
                        return (Reactor) obj;
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
        return null;
    }

    /**
     * 生成怪物、在、Ground、Below。
     * @param id ID
     * @param x x
     * @param y y
     */
    public void spawnMonsterOnGroundBelow(int id, int x, int y) {
        Monster mob = LifeFactory.getMonster(id);
        spawnMonsterOnGroundBelow(mob, new Point(x, y));
    }

    /**
     * 生成怪物、在、Ground、Below。
     * @param mob 怪物
     * @param pos 坐标
     */
    public void spawnMonsterOnGroundBelow(Monster mob, Point pos) {
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y--;
        mob.setPosition(spos);
        spawnMonster(mob);
    }

    /**
     * 生成CPQ怪物。
     * @param mob 怪物
     * @param pos 坐标
     * @param team team
     */
    public void spawnCPQMonster(Monster mob, Point pos, int team) {
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y--;
        mob.setPosition(spos);
        mob.setTeam(team);
        spawnMonster(mob);
    }

    private void monsterItemDrop(final Monster m, long delay) {
        m.dropFromFriendlyMonster(delay);
    }

    /**
     * 生成Fake、怪物、在、Ground、Below。
     * @param mob 怪物
     * @param pos 坐标
     */
    public void spawnFakeMonsterOnGroundBelow(Monster mob, Point pos) {
        Point spos = getGroundBelow(pos);
        mob.setPosition(spos);
        spawnFakeMonster(mob);
    }

    /**
     * 获取Ground、Below。
     * @param pos 坐标
     * @return Point 类型结果
     */
    public Point getGroundBelow(Point pos) {
        Point spos = new Point(pos.x, pos.y - 14); // Using -14 fixes spawning pets causing a lot of issues.
        spos = calcPointBelow(spos);
        spos.y--;//shouldn't be null!
        return spos;
    }

    /**
     * 获取坐标、Below。
     * @param pos 坐标
     * @return Point 类型结果
     */
    public Point getPointBelow(Point pos) {
        return calcPointBelow(pos);
    }

    /**
     * 生成Revives。
     * @param monster 怪物
     */
    public void spawnRevives(final Monster monster) {
        monster.setMap(this);
        if (getEventInstance() != null) {
            getEventInstance().registerMonster(monster);
        }

        spawnAndAddRangedMapObject(monster, c -> c.sendPacket(PacketCreator.spawnMonster(monster, false)));

        monster.aggroUpdateController();
        updateBossSpawn(monster);

        spawnedMonstersOnMap.incrementAndGet();
        addSelfDestructive(monster);
        applyRemoveAfter(monster);
    }

    private void applyRemoveAfter(final Monster monster) {
        final selfDestruction selfDestruction = monster.getStats().selfDestruction();
        if (monster.getStats().removeAfter() > 0 || selfDestruction != null && selfDestruction.getHp() < 0) {
            Runnable removeAfterAction;

            if (selfDestruction == null) {
                removeAfterAction = () -> killMonster(monster, null, false);

                registerMapSchedule(removeAfterAction, SECONDS.toMillis(monster.getStats().removeAfter()));
            } else {
                removeAfterAction = () -> killMonster(monster, null, false, selfDestruction.getAction());

                registerMapSchedule(removeAfterAction, SECONDS.toMillis(selfDestruction.removeAfter()));
            }

            monster.pushRemoveAfterAction(removeAfterAction);
        }
    }

    /**
     * 执行 dismiss、移除、After 操作。
     * @param monster 怪物
     */
    public void dismissRemoveAfter(final Monster monster) {
        Runnable removeAfterAction = monster.popRemoveAfterAction();
        if (removeAfterAction != null) {
            OverallService service = (OverallService) this.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
            service.forceRunOverallAction(mapid, removeAfterAction);
        }
    }

    private List<SpawnPoint> getMonsterSpawn() {
        synchronized (monsterSpawn) {
            return new ArrayList<>(monsterSpawn);
        }
    }

    private List<SpawnPoint> getAllMonsterSpawn() {
        synchronized (allMonsterSpawn) {
            return new ArrayList<>(allMonsterSpawn);
        }
    }

    /**
     * 生成所有、怪物、ID、来自、地图、刷新、List。
     * @param id ID
     */
    public void spawnAllMonsterIdFromMapSpawnList(int id) {
        spawnAllMonsterIdFromMapSpawnList(id, 1, false);
    }

    /**
     * 生成所有、怪物、ID、来自、地图、刷新、List。
     * @param id ID
     * @param difficulty difficulty
     * @param isPq isPq
     */
    public void spawnAllMonsterIdFromMapSpawnList(int id, int difficulty, boolean isPq) {
        for (SpawnPoint sp : getAllMonsterSpawn()) {
            if (sp.getMonsterId() == id && sp.shouldForceSpawn()) {
                spawnMonster(sp.getMonster(), difficulty, isPq);
            }
        }
    }

    /**
     * 生成所有、怪物、来自、地图、刷新、List。
     */
    public void spawnAllMonstersFromMapSpawnList() {
        spawnAllMonstersFromMapSpawnList(1, false);
    }

    /**
     * 生成所有、怪物、来自、地图、刷新、List。
     * @param difficulty difficulty
     * @param isPq isPq
     */
    public void spawnAllMonstersFromMapSpawnList(int difficulty, boolean isPq) {
        for (SpawnPoint sp : getAllMonsterSpawn()) {
            spawnMonster(sp.getMonster(), difficulty, isPq);
        }
    }

    /**
     * 生成怪物。
     * @param monster 怪物
     */
    public void spawnMonster(final Monster monster) {
        spawnMonster(monster, 1, false);
    }

    /**
     * 生成怪物。
     * @param monster 怪物
     * @param difficulty difficulty
     * @param isPq isPq
     */
    public void spawnMonster(final Monster monster, int difficulty, boolean isPq) {
        if (mobCapacity != -1 && mobCapacity == spawnedMonstersOnMap.get()) {
            return;//PyPQ
        }

        monster.changeDifficulty(difficulty, isPq);

        monster.setMap(this);
        if (getEventInstance() != null) {
            getEventInstance().registerMonster(monster);
        }

        spawnAndAddRangedMapObject(monster, c -> c.sendPacket(PacketCreator.spawnMonster(monster, true)), null);

        monster.aggroUpdateController();
        updateBossSpawn(monster);

        if ((monster.getTeam() == 1 || monster.getTeam() == 0) && (isCPQMap() || isCPQMap2())) {
            List<MCSkill> teamS = null;
            if (monster.getTeam() == 0) {
                teamS = redTeamBuffs;
            } else if (monster.getTeam() == 1) {
                teamS = blueTeamBuffs;
            }
            if (teamS != null) {
                for (MCSkill skil : teamS) {
                    if (skil != null) {
                        skil.getSkill().applyEffect(null, monster, false, null);
                    }
                }
            }
        }

        if (monster.getDropPeriodTime() > 0) { //9300102 - Watchhog, 9300061 - Moon Bunny (HPQ), 9300093 - Tylus    //9300102-护卫用小浣猪，9300061-月妙（HPQ），9300093-冒牌泰勒斯
            if (monster.getId() == MobId.WATCH_HOG) {
                monsterItemDrop(monster, monster.getDropPeriodTime());
            } else if (monster.getId() == MobId.MOON_BUNNY) {
                monsterItemDrop(monster, monster.getDropPeriodTime() / 3);
            } else if (monster.getId() == MobId.TYLUS) {
                monsterItemDrop(monster, monster.getDropPeriodTime());
            } else if (monster.getId() == MobId.GIANT_SNOWMAN_LV5_EASY || monster.getId() == MobId.GIANT_SNOWMAN_LV5_MEDIUM || monster.getId() == MobId.GIANT_SNOWMAN_LV5_HARD) {
                monsterItemDrop(monster, monster.getDropPeriodTime());
            } else {
                log.error("[异常刷怪] 检测到未配置定时刷新的怪物: ID={}", monster.getId());
            }
        }

        spawnedMonstersOnMap.incrementAndGet();
        addSelfDestructive(monster);
        applyRemoveAfter(monster);  // thanks LightRyuzaki for pointing issues with spawned CWKPQ mobs not applying this
    }

    /**
     * 生成Dojo、怪物。
     * @param monster 怪物
     */
    public void spawnDojoMonster(final Monster monster) {
        Point[] pts = {new Point(140, 0), new Point(190, 7), new Point(187, 7)};
        spawnMonsterWithEffect(monster, 15, pts[Randomizer.nextInt(3)]);
    }

    /**
     * 生成怪物带效果。
     * @param monster 怪物
     * @param effect effect
     * @param pos 坐标
     */
    public void spawnMonsterWithEffect(final Monster monster, final int effect, Point pos) {
        monster.setMap(this);
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        if (spos == null) {
            return;
        }

        if (getEventInstance() != null) {
            getEventInstance().registerMonster(monster);
        }

        spos.y--;
        monster.setPosition(spos);
        monster.setSpawnEffect(effect);

        spawnAndAddRangedMapObject(monster, c -> c.sendPacket(PacketCreator.spawnMonster(monster, true, effect)));

        monster.aggroUpdateController();
        updateBossSpawn(monster);

        spawnedMonstersOnMap.incrementAndGet();
        addSelfDestructive(monster);
        applyRemoveAfter(monster);
    }

    /**
     * 生成Fake、怪物。
     * @param monster 怪物
     */
    public void spawnFakeMonster(final Monster monster) {
        monster.setMap(this);
        monster.setFake(true);
        spawnAndAddRangedMapObject(monster, c -> c.sendPacket(PacketCreator.spawnFakeMonster(monster, 0)));

        spawnedMonstersOnMap.incrementAndGet();
        addSelfDestructive(monster);
    }

    /**
     * 执行 make、怪物、Real 操作。
     * @param monster 怪物
     */
    public void makeMonsterReal(final Monster monster) {
        monster.setFake(false);
        broadcastMessage(PacketCreator.makeMonsterReal(monster));
        monster.aggroUpdateController();
        updateBossSpawn(monster);
    }

    /**
     * 生成反应堆。
     * @param reactor 反应堆
     */
    public void spawnReactor(final Reactor reactor) {
        reactor.setMap(this);
        spawnAndAddRangedMapObject(reactor, c -> c.sendPacket(reactor.makeSpawnData()));
    }

    /**
     * 生成传送门。
     * @param door door
     */
    public void spawnDoor(final DoorObject door) {
        spawnAndAddRangedMapObject(door, c -> {
            Character chr = c.getPlayer();
            if (chr != null) {
                door.sendSpawnData(c, false);
                chr.addVisibleMapObject(door);
            }
        }, chr -> chr.getMapId() == door.getFrom().getId());
    }

    /**
     * 获取传送门传送门。
     * @param doorid doorid
     * @return Portal 类型结果
     */
    public Portal getDoorPortal(int doorid) {
        Portal doorPortal = portals.get(0x80 + doorid);
        if (doorPortal == null) {
            log.warn("[传动点] 地图 {} (ID:{}) 不存在传送门ID为 {} 的入口", mapName, mapid, doorid);
            return portals.get(0x80);
        }

        return doorPortal;
    }

    /**
     * 生成召唤兽。
     * @param summon summon
     */
    public void spawnSummon(final Summon summon) {
        spawnAndAddRangedMapObject(summon, c -> c.sendPacket(PacketCreator.spawnSummon(summon, true)), null);
    }

    /**
     * 生成迷雾。
     * @param mist mist
     * @param duration duration
     * @param poison poison
     * @param fake fake
     * @param recovery recovery
     */
    public void spawnMist(final Mist mist, final int duration, boolean poison, boolean fake, boolean recovery) {
        addMapObject(mist);
        broadcastMessage(fake ? mist.makeFakeSpawnData(30) : mist.makeSpawnData());
        TimerManager tMan = TimerManager.getInstance();
        final ScheduledFuture<?> poisonSchedule;
        if (poison) {
            Runnable poisonTask = () -> {
                List<MapObject> affectedMonsters = getMapObjectsInBox(mist.getBox(), Collections.singletonList(MapObjectType.MONSTER));
                for (MapObject mo : affectedMonsters) {
                    if (mist.makeChanceResult()) {
                        MonsterStatusEffect poisonEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), mist.getSourceSkill(), null, false);
                        ((Monster) mo).applyStatus(mist.getOwner(), poisonEffect, true, duration);
                    }
                }
            };
            poisonSchedule = tMan.register(poisonTask, 2000, 2500);
        } else if (recovery) {
            Runnable poisonTask = () -> {
                List<MapObject> players = getMapObjectsInBox(mist.getBox(), Collections.singletonList(MapObjectType.PLAYER));
                for (MapObject mo : players) {
                    if (mist.makeChanceResult()) {
                        Character chr = (Character) mo;
                        if (mist.getOwner().getId() == chr.getId() || mist.getOwner().getParty() != null && mist.getOwner().getParty().containsMembers(chr.getMPC())) {
                            chr.addMP(mist.getSourceSkill().getEffect(chr.getSkillLevel(mist.getSourceSkill().getId())).getX() * chr.getMp() / 100);
                        }
                    }
                }
            };
            poisonSchedule = tMan.register(poisonTask, 2000, 2500);
        } else {
            poisonSchedule = null;
        }

        Runnable mistSchedule = () -> {
            removeMapObject(mist);
            if (poisonSchedule != null) {
                poisonSchedule.cancel(false);
            }
            broadcastMessage(mist.makeDestroyData());
        };

        MobMistService service = (MobMistService) this.getChannelServer().getServiceAccess(ChannelServices.MOB_MIST);
        service.registerMobMistCancelAction(mapid, mistSchedule, duration);
    }

    /**
     * 生成风筝。
     * @param kite kite
     */
    public void spawnKite(final Kite kite) {
        addMapObject(kite);
        broadcastMessage(kite.makeSpawnData());

        Runnable expireKite = () -> {
            removeMapObject(kite);
            broadcastMessage(kite.makeDestroyData());
        };

        getWorldServer().registerTimedMapObject(expireKite, GameConfig.getServerLong("kite_expire_time"));
    }

    /**
     * 生成物品掉落。
     * @param dropper 掉落来源
     * @param owner 归属角色
     * @param item item
     * @param pos 坐标
     * @param ffaDrop ffaDrop
     * @param playerDrop 是否玩家丢弃
     */
    public final void spawnItemDrop(final MapObject dropper, final Character owner, final Item item, Point pos, final boolean ffaDrop, final boolean playerDrop) {
        spawnItemDrop(dropper, owner, item, pos, (byte) (ffaDrop ? 2 : 0), playerDrop);
    }

    /**
     * 生成物品掉落。
     * @param dropper 掉落来源
     * @param owner 归属角色
     * @param item item
     * @param pos 坐标
     * @param dropType dropType
     * @param playerDrop 是否玩家丢弃
     */
    public final void spawnItemDrop(final MapObject dropper, final Character owner, final Item item, Point pos, final byte dropType, final boolean playerDrop) {
        if (FieldLimit.DROP_LIMIT.check(this.getFieldLimit())) { // thanks Conrad for noticing some maps shouldn't have loots available
            this.disappearingItemDrop(dropper, owner, item, pos);
            return;
        }

        final Point droppos = calcDropPos(pos, pos);
        final MapItem mdrop = new MapItem(item, droppos, dropper, owner, owner.getClient(), dropType, playerDrop);
        mdrop.setDropTime(Server.getInstance().getCurrentTime());

        spawnAndAddRangedMapObject(mdrop, c -> {
            mdrop.lockItem();
            try {
                c.sendPacket(PacketCreator.dropItemFromMapObject(c.getPlayer(), mdrop, dropper.getPosition(), droppos, (byte) 1));
            } finally {
                mdrop.unlockItem();
            }
        }, null);

        mdrop.lockItem();
        try {
            broadcastItemDropMessage(mdrop, dropper.getPosition(), droppos, (byte) 0);
        } finally {
            mdrop.unlockItem();
        }

        instantiateItemDrop(mdrop);
        activateItemReactors(mdrop, owner.getClient());
    }

    /**
     * 生成物品、掉落、List。
     * @param list 掉落条目列表（Integer 列表/集合）
     * @param dropper 掉落来源
     * @param owner 归属角色
     * @param pos 坐标
     */
    public final void spawnItemDropList(List<Integer> list, final MapObject dropper, final Character owner, Point pos) {
        spawnItemDropList(list, 1, 1, dropper, owner, pos, true, false);
    }

    /**
     * 生成物品、掉落、List。
     * @param list 掉落条目列表（Integer 列表/集合）
     * @param minCopies minCopies
     * @param maxCopies maxCopies
     * @param dropper 掉落来源
     * @param owner 归属角色
     * @param pos 坐标
     */
    public final void spawnItemDropList(List<Integer> list, int minCopies, int maxCopies, final MapObject dropper, final Character owner, Point pos) {
        spawnItemDropList(list, minCopies, maxCopies, dropper, owner, pos, true, false);
    }

    // spawns item instances of all defined item ids on a list
    /**
     * 生成物品、掉落、List。
     * @param list 掉落条目列表（Integer 列表/集合）
     * @param minCopies minCopies
     * @param maxCopies maxCopies
     * @param dropper 掉落来源
     * @param owner 归属角色
     * @param pos 坐标
     * @param ffaDrop ffaDrop
     * @param playerDrop 是否玩家丢弃
     */
    public final void spawnItemDropList(List<Integer> list, int minCopies, int maxCopies, final MapObject dropper, final Character owner, Point pos, final boolean ffaDrop, final boolean playerDrop) {
        int copies = (maxCopies - minCopies) + 1;
        if (copies < 1) {
            return;
        }

        Collections.shuffle(list);

        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Random rnd = new Random();

        final Point dropPos = new Point(pos);
        dropPos.x -= (12 * list.size());

        for (Integer integer : list) {
            if (integer == 0) {
                spawnMesoDrop(owner != null ? NumberTool.floatToInt(10 * owner.getMesoRate()) : 10, calcDropPos(dropPos, pos), dropper, owner, playerDrop, (byte) (ffaDrop ? 2 : 0));
            } else {
                final Item drop;
                int randomedId = integer;

                if (ItemConstants.getInventoryType(randomedId) != InventoryType.EQUIP) {
                    drop = new Item(randomedId, (short) 0, (short) (rnd.nextInt(copies) + minCopies));
                } else {
                    drop = ii.randomizeStats((Equip) ii.getEquipById(randomedId));
                }

                spawnItemDrop(dropper, owner, drop, calcDropPos(dropPos, pos), ffaDrop, playerDrop);
            }

            dropPos.x += 25;
        }
    }

    private void registerMapSchedule(Runnable r, long delay) {
        OverallService service = (OverallService) this.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
        service.registerOverallAction(mapid, r, delay);
    }

    private void activateItemReactors(final MapItem drop, final Client c) {
        final Item item = drop.getItem();

        for (final MapObject o : getReactors()) {
            final Reactor react = (Reactor) o;

            if (react.getReactorType() == 100) {
                if (react.getReactItem(react.getEventState()).getLeft() == item.getItemId() && react.getReactItem(react.getEventState()).getRight() == item.getQuantity()) {

                    if (react.getArea().contains(drop.getPosition())) {
                        registerMapSchedule(new ActivateItemReactor(drop, react, c), 5000);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 搜索物品反应堆。
     * @param react react
     */
    public void searchItemReactors(final Reactor react) {
        if (react.getReactorType() == 100) {
            Pair<Integer, Integer> reactProp = react.getReactItem(react.getEventState());
            int reactItem = reactProp.getLeft(), reactQty = reactProp.getRight();
            Rectangle reactArea = react.getArea();

            List<MapItem> list;
            objectRLock.lock();
            try {
                list = new ArrayList<>(droppedItems.keySet());
            } finally {
                objectRLock.unlock();
            }

            for (final MapItem drop : list) {
                drop.lockItem();
                try {
                    if (!drop.isPickedUp()) {
                        final Item item = drop.getItem();

                        if (item != null && reactItem == item.getItemId() && reactQty == item.getQuantity()) {
                            if (reactArea.contains(drop.getPosition())) {
                                Client owner = drop.getOwnerClient();
                                if (owner != null) {
                                    registerMapSchedule(new ActivateItemReactor(drop, react, owner), 5000);
                                }
                            }
                        }
                    }
                } finally {
                    drop.unlockItem();
                }
            }
        }
    }

    /**
     * 执行 change、环境 操作。
     * @param mapObj mapObj
     * @param newState newState
     */
    public void changeEnvironment(String mapObj, int newState) {
        broadcastMessage(PacketCreator.environmentChange(mapObj, newState));
    }

    /**
     * 执行 start、地图、效果 操作。
     * @param msg msg
     * @param itemId 物品 ID
     */
    public void startMapEffect(String msg, int itemId) {
        startMapEffect(msg, itemId, 30000);
    }

    /**
     * 执行 start、地图、效果 操作。
     * @param msg msg
     * @param itemId 物品 ID
     * @param time time
     */
    public void startMapEffect(String msg, int itemId, long time) {
        if (mapEffect != null) {
            return;
        }
        mapEffect = new MapEffect(msg, itemId);
        broadcastMessage(mapEffect.makeStartData());

        Runnable r = () -> {
            broadcastMessage(mapEffect.makeDestroyData());
            mapEffect = null;
        };

        registerMapSchedule(r, time);
    }

    /**
     * 获取Any角色来自队伍。
     * @param partyid 队伍 ID
     * @return Character 类型结果
     */
    public Character getAnyCharacterFromParty(int partyid) {
        for (Character chr : this.getAllPlayers()) {
            if (chr.getPartyId() == partyid) {
                return chr;
            }
        }

        return null;
    }

    private void addPartyMemberInternal(Character chr, int partyid) {
        if (partyid == -1) {
            return;
        }

        Set<Integer> partyEntry = mapParty.get(partyid);
        if (partyEntry == null) {
            partyEntry = new LinkedHashSet<>();
            partyEntry.add(chr.getId());

            mapParty.put(partyid, partyEntry);
        } else {
            partyEntry.add(chr.getId());
        }
    }

    private void removePartyMemberInternal(Character chr, int partyid) {
        if (partyid == -1) {
            return;
        }

        Set<Integer> partyEntry = mapParty.get(partyid);
        if (partyEntry != null) {
            if (partyEntry.size() > 1) {
                partyEntry.remove(chr.getId());
            } else {
                mapParty.remove(partyid);
            }
        }
    }

    /**
     * 添加队伍成员。
     * @param chr 角色
     * @param partyid 队伍 ID
     */
    public void addPartyMember(Character chr, int partyid) {
        chrWLock.lock();
        try {
            addPartyMemberInternal(chr, partyid);
        } finally {
            chrWLock.unlock();
        }
    }

    /**
     * 移除队伍成员。
     * @param chr 角色
     * @param partyid 队伍 ID
     */
    public void removePartyMember(Character chr, int partyid) {
        chrWLock.lock();
        try {
            removePartyMemberInternal(chr, partyid);
        } finally {
            chrWLock.unlock();
        }
    }

    /**
     * 移除队伍。
     * @param partyid 队伍 ID
     */
    public void removeParty(int partyid) {
        chrWLock.lock();
        try {
            mapParty.remove(partyid);
        } finally {
            chrWLock.unlock();
        }
    }

    /**
     * 添加玩家。
     * @param chr 角色
     */
    public void addPlayer(final Character chr) {
        int chrSize;
        Party party = chr.getParty();
        chrWLock.lock();
        try {
            characters.add(chr);
            chrSize = characters.size();

            if (party != null && party.getMemberById(chr.getId()) != null) {
                addPartyMemberInternal(chr, party.getId());
            }
            itemMonitorTimeout = 1;
        } finally {
            chrWLock.unlock();
        }

        chr.setMapId(mapid);
        chr.updateActiveEffects();

        if (this.getHPDec() > 0) {
            getWorldServer().addPlayerHpDecrease(chr);
        } else {
            getWorldServer().removePlayerHpDecrease(chr);
        }

        MapScriptManager msm = MapScriptManager.getInstance();
        if (chrSize == 1) {
            if (!hasItemMonitor()) {
                startItemMonitor();
                aggroMonitor.startAggroCoordinator();
            }

            if (onFirstUserEnter.length() != 0) {
                msm.runMapScript(chr.getClient(), "onFirstUserEnter/" + onFirstUserEnter, true);
            }
        }
        if (onUserEnter.length() != 0) {
            if (onUserEnter.equals("cygnusTest") && !MapId.isCygnusIntro(mapid)) {
                chr.saveLocation("INTRO");
            }

            msm.runMapScript(chr.getClient(), "onUserEnter/" + onUserEnter, false);
        }
        if (FieldLimit.CANNOTUSEMOUNTS.check(fieldLimit) && chr.getBuffedValue(BuffStat.MONSTER_RIDING) != null) {
            chr.cancelEffectFromBuffStat(BuffStat.MONSTER_RIDING);
            chr.cancelBuffStats(BuffStat.MONSTER_RIDING);
        }

        if (mapid == MapId.FROM_LITH_TO_RIEN) { // To Rien
            int travelTime = getWorldServer().getTransportationTime((int) MINUTES.toMillis(1));
            chr.sendPacket(PacketCreator.getClock(travelTime / 1000));
            TimerManager.getInstance().schedule(() -> {
                if (chr.getMapId() == MapId.FROM_LITH_TO_RIEN) {
                    chr.changeMap(MapId.DANGEROUS_FOREST, 0);
                }
            }, travelTime);
        } else if (mapid == MapId.FROM_RIEN_TO_LITH) { // To Lith Harbor
            int travelTime = getWorldServer().getTransportationTime((int) MINUTES.toMillis(1));
            chr.sendPacket(PacketCreator.getClock(travelTime / 1000));
            TimerManager.getInstance().schedule(() -> {
                if (chr.getMapId() == MapId.FROM_RIEN_TO_LITH) {
                    chr.changeMap(MapId.LITH_HARBOUR, 3);
                }
            }, travelTime);
        } else if (mapid == MapId.FROM_ELLINIA_TO_EREVE) { // To Ereve (SkyFerry)
            int travelTime = getWorldServer().getTransportationTime((int) MINUTES.toMillis(2));
            chr.sendPacket(PacketCreator.getClock(travelTime / 1000));
            TimerManager.getInstance().schedule(() -> {
                if (chr.getMapId() == MapId.FROM_ELLINIA_TO_EREVE) {
                    chr.changeMap(MapId.SKY_FERRY, 0);
                }
            }, travelTime);
        } else if (mapid == MapId.FROM_EREVE_TO_ELLINIA) { // To Victoria Island (SkyFerry)
            int travelTime = getWorldServer().getTransportationTime((int) MINUTES.toMillis(2));
            chr.sendPacket(PacketCreator.getClock(travelTime / 1000));
            TimerManager.getInstance().schedule(() -> {
                if (chr.getMapId() == MapId.FROM_EREVE_TO_ELLINIA) {
                    chr.changeMap(MapId.ELLINIA_SKY_FERRY, 0);
                }
            }, travelTime);
        } else if (mapid == MapId.FROM_EREVE_TO_ORBIS) { // To Orbis (SkyFerry)
            int travelTime = getWorldServer().getTransportationTime((int) MINUTES.toMillis(8));
            chr.sendPacket(PacketCreator.getClock(travelTime / 1000));
            TimerManager.getInstance().schedule(() -> {
                if (chr.getMapId() == MapId.FROM_EREVE_TO_ORBIS) {
                    chr.changeMap(MapId.ORBIS_STATION, 0);
                }
            }, travelTime);
        } else if (mapid == MapId.FROM_ORBIS_TO_EREVE) { // To Ereve From Orbis (SkyFerry)
            int travelTime = getWorldServer().getTransportationTime((int) MINUTES.toMillis(8));
            chr.sendPacket(PacketCreator.getClock(travelTime / 1000));
            TimerManager.getInstance().schedule(() -> {
                if (chr.getMapId() == MapId.FROM_ORBIS_TO_EREVE) {
                    chr.changeMap(MapId.SKY_FERRY, 0);
                }
            }, travelTime);
        } else if (MiniDungeonInfo.isDungeonMap(mapid)) {
            MiniDungeon mmd = chr.getClient().getChannelServer().getMiniDungeon(mapid);
            if (mmd != null) {
                mmd.registerPlayer(chr);
            }
        } else if (GameConstants.isAriantColiseumArena(mapid)) {
            int pqTimer = (int) MINUTES.toMillis(10);
            chr.sendPacket(PacketCreator.getClock(pqTimer / 1000));
        }

        Pet[] pets = chr.getPets();
        for (Pet pet : pets) {
            if (pet != null) {
                pet.setPos(getGroundBelow(chr.getPosition()));
                chr.sendPacket(PacketCreator.showPet(chr, pet, false, false));
            } else {
                break;
            }
        }
        chr.commitExcludedItems();  // thanks OishiiKawaiiDesu for noticing pet item ignore registry erasing upon changing maps

        if (chr.getMonsterCarnival() != null) {
            chr.sendPacket(PacketCreator.getClock(chr.getMonsterCarnival().getTimeLeftSeconds()));
            if (isCPQMap()) {
                int team = -1;
                int oposition = -1;
                if (chr.getTeam() == 0) {
                    team = 0;
                    oposition = 1;
                }
                if (chr.getTeam() == 1) {
                    team = 1;
                    oposition = 0;
                }
                chr.sendPacket(PacketCreator.startMonsterCarnival(chr, team, oposition));
            }
        }

        chr.removeSandboxItems();

        if (chr.getChalkboard() != null) {
            if (!GameConstants.isFreeMarketRoom(mapid)) {
                chr.sendPacket(PacketCreator.useChalkboard(chr, false)); // update player's chalkboard when changing maps found thanks to Vcoc
            } else {
                chr.setChalkboard(null);
            }
        }

        if (chr.isHidden()) {
            broadcastGMSpawnPlayerMapObjectMessage(chr, chr, true);
            chr.sendPacket(PacketCreator.getGMEffect(0x10, (byte) 1));

            List<Pair<BuffStat, Integer>> dsstat = Collections.singletonList(new Pair<>(BuffStat.DARKSIGHT, 0));
            broadcastGMMessage(chr, PacketCreator.giveForeignBuff(chr.getId(), dsstat), false);
        } else {
            broadcastSpawnPlayerMapObjectMessage(chr, chr, true);
        }

        sendObjectPlacement(chr.getClient());

        if (isStartingEventMap() && !eventStarted()) {
            chr.getMap().getPortal("join00").setPortalStatus(false);
        }
        if (hasForcedEquip()) {
            chr.sendPacket(PacketCreator.showForcedEquip(-1));
        }
        if (specialEquip()) {
            chr.sendPacket(PacketCreator.coconutScore(0, 0));
            chr.sendPacket(PacketCreator.showForcedEquip(chr.getTeam()));
        }
        objectWLock.lock();
        try {
            this.mapobjects.put(chr.getObjectId(), chr);
        } finally {
            objectWLock.unlock();
        }

        if (chr.getPlayerShop() != null) {
            addMapObject(chr.getPlayerShop());
        }

        final Dragon dragon = chr.getDragon();
        if (dragon != null) {
            dragon.setPosition(chr.getPosition());
            this.addMapObject(dragon);
            if (chr.isHidden()) {
                this.broadcastGMPacket(chr, PacketCreator.spawnDragon(dragon));
            } else {
                this.broadcastPacket(chr, PacketCreator.spawnDragon(dragon));
            }
        }

        StatEffect summonStat = chr.getStatForBuff(BuffStat.SUMMON);
        if (summonStat != null) {
            Summon summon = chr.getSummonByKey(summonStat.getSourceId());
            summon.setPosition(chr.getPosition());
            chr.getMap().spawnSummon(summon);
            updateMapObjectVisibility(chr, summon);
        }
        if (mapEffect != null) {
            mapEffect.sendStartData(chr.getClient());
        }
        chr.sendPacket(PacketCreator.resetForcedStats());
        if (MapId.isGodlyStatMap(mapid)) {
            chr.sendPacket(PacketCreator.aranGodlyStats());
        }
        if (chr.getEventInstance() != null && chr.getEventInstance().isTimerStarted()) {
            chr.sendPacket(PacketCreator.getClock((int) (chr.getEventInstance().getTimeLeft() / 1000)));
        }
        if (chr.getFitness() != null && chr.getFitness().isTimerStarted()) {
            chr.sendPacket(PacketCreator.getClock((int) (chr.getFitness().getTimeLeft() / 1000)));
        }

        if (chr.getOla() != null && chr.getOla().isTimerStarted()) {
            chr.sendPacket(PacketCreator.getClock((int) (chr.getOla().getTimeLeft() / 1000)));
        }

        if (mapid == MapId.EVENT_SNOWBALL) {
            chr.sendPacket(PacketCreator.rollSnowBall(true, 0, null, null));
        }

        if (hasClock()) {
            Calendar cal = Calendar.getInstance();
            chr.sendPacket(PacketCreator.getClockTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND)));
        }
        if (hasBoat() > 0) {
            if (hasBoat() == 1) {
                chr.sendPacket((PacketCreator.boatPacket(true)));
            } else {
                chr.sendPacket(PacketCreator.boatPacket(false));
            }
        }

        chr.receivePartyMemberHP();
        announcePlayerDiseases(chr.getClient());
    }

    private static void announcePlayerDiseases(final Client c) {
        Server.getInstance().registerAnnouncePlayerDiseases(c);
    }

    /**
     * 获取Random、玩家、Spawnpoint。
     * @return Portal 类型结果
     */
    public Portal getRandomPlayerSpawnpoint() {
        List<Portal> spawnPoints = new ArrayList<>();
        for (Portal portal : portals.values()) {
            if (portal.getType() >= 0 && portal.getType() <= 1 && portal.getTargetMapId() == MapId.NONE) {
                spawnPoints.add(portal);
            }
        }
        Portal portal = spawnPoints.get(new Random().nextInt(spawnPoints.size()));
        return portal != null ? portal : getPortal(0);
    }

    /**
     * 查找Closest、传送、传送门。
     * @param from from
     * @return Portal 类型结果
     */
    public Portal findClosestTeleportPortal(Point from) {
        Portal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (Portal portal : portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (portal.getType() == Portal.TELEPORT_PORTAL && distance < shortestDistance && portal.getTargetMapId() != MapId.NONE) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    /**
     * 查找Closest、玩家、Spawnpoint。
     * @param from from
     * @return Portal 类型结果
     */
    public Portal findClosestPlayerSpawnpoint(Point from) {
        Portal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (Portal portal : portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 1 && distance < shortestDistance && portal.getTargetMapId() == MapId.NONE) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    /**
     * 查找Closest、传送门。
     * @param from from
     * @return Portal 类型结果
     */
    public Portal findClosestPortal(Point from) {
        Portal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (Portal portal : portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (distance < shortestDistance) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    /**
     * 查找Market、传送门。
     * @return Portal 类型结果
     */
    public Portal findMarketPortal() {
        for (Portal portal : portals.values()) {
            String ptScript = portal.getScriptName();
            if (ptScript != null && ptScript.contains("market")) {
                return portal;
            }
        }
        return null;
    }

    /*
    /**
     * 获取Portals。
     * @return Collection<Portal> 类型结果
     */
    public Collection<Portal> getPortals() {
        return Collections.unmodifiableCollection(portals.values());
    }
    */

    public void addPlayerPuppet(Character player) {
        for (Monster mm : this.getAllMonsters()) {
            mm.aggroAddPuppet(player);
        }
    }

    /**
     * 移除玩家、Puppet。
     * @param player 玩家
     */
    public void removePlayerPuppet(Character player) {
        for (Monster mm : this.getAllMonsters()) {
            mm.aggroRemovePuppet(player);
        }
    }

    /**
     * 移除玩家。
     * @param chr 角色
     */
    public void removePlayer(Character chr) {
        Channel cserv = chr.getClient().getChannelServer();
        chr.unregisterChairBuff();

        Party party = chr.getParty();
        chrWLock.lock();
        try {
            if (party != null && party.getMemberById(chr.getId()) != null) {
                removePartyMemberInternal(chr, party.getId());
            }

            characters.remove(chr);
        } finally {
            chrWLock.unlock();
        }

        if (MiniDungeonInfo.isDungeonMap(mapid)) {
            MiniDungeon mmd = cserv.getMiniDungeon(mapid);
            if (mmd != null) {
                if (!mmd.unregisterPlayer(chr)) {
                    cserv.removeMiniDungeon(mapid);
                }
            }
        }

        removeMapObject(chr.getObjectId());
        if (!chr.isHidden()) {
            broadcastMessage(PacketCreator.removePlayerFromMap(chr.getId()));
        } else {
            broadcastGMMessage(PacketCreator.removePlayerFromMap(chr.getId()));
        }

        chr.leaveMap();

        for (Summon summon : new ArrayList<>(chr.getSummonsValues())) {
            if (summon.isStationary()) {
                chr.cancelEffectFromBuffStat(BuffStat.PUPPET);
            } else {
                removeMapObject(summon);
            }
        }

        if (chr.getDragon() != null) {
            removeMapObject(chr.getDragon());
            if (chr.isHidden()) {
                this.broadcastGMPacket(chr, PacketCreator.removeDragon(chr.getId()));
            } else {
                this.broadcastPacket(chr, PacketCreator.removeDragon(chr.getId()));
            }
        }
    }

    /**
     * 无条件地将消息广播给所有玩家。
     *
     * Broadcasts a message to all players without any conditions.
     *
     * @param {Packet} packet - 要广播的数据包。The packet to be broadcasted.
     */
    public void broadcastMessage(Packet packet) {
        broadcastMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }

    /**
     * 无条件地将管理员消息广播给所有玩家。
     *
     * Broadcasts an admin message to all players without any conditions.
     *
     * @param {Packet} packet - 要广播的数据包。The packet to be broadcasted.
     */
    public void broadcastGMMessage(Packet packet) {
        broadcastGMMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }

    /**
     * 根据 repeatToSource 参数决定是否将消息重复发送给源角色，并无范围限制地广播消息。
     *
     * Broadcasts a message based on the repeatToSource parameter, repeating it to the source character if specified,
     * and broadcasts it without any range restrictions.
     *
     * @param {Character} source - 消息的源角色。The source character of the message.
     * @param {Packet} packet - 要广播的数据包。The packet to be broadcasted.
     * @param {boolean} repeatToSource - 是否重复发送给源角色。Whether to repeat the message to the source character.
     */
    public void broadcastMessage(Character source, Packet packet, boolean repeatToSource) {
        broadcastMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }

    /**
     * 根据 repeatToSource 和 ranged 参数决定是否将消息重复发送给源角色以及是否限定在一定范围内广播消息。
     *
     * Broadcasts a message based on the repeatToSource and ranged parameters, repeating it to the source character if specified,
     * and broadcasting it within a certain range if ranged is true.
     *
     * @param {Character} source - 消息的源角色。The source character of the message.
     * @param {Packet} packet - 要广播的数据包。The packet to be broadcasted.
     * @param {boolean} repeatToSource - 是否重复发送给源角色。Whether to repeat the message to the source character.
     * @param {boolean} ranged - 是否限定在一定范围内广播消息。Whether to broadcast the message within a certain range.
     */
    public void broadcastMessage(Character source, Packet packet, boolean repeatToSource, boolean ranged) {
        broadcastMessage(repeatToSource ? null : source, packet, ranged ? getRangedDistance() : Double.POSITIVE_INFINITY, source.getPosition());
    }

    /**
     * 从指定点开始，在一定范围内广播消息。
     *
     * Broadcasts a message starting from a specified point within a certain range.
     *
     * @param {Packet} packet - 要广播的数据包。The packet to be broadcasted.
     * @param {Point} rangedFrom - 广播的起点位置。The starting point for broadcasting.
     */
    public void broadcastMessage(Packet packet, Point rangedFrom) {
        broadcastMessage(null, packet, getRangedDistance(), rangedFrom);
    }

    /**
     * 从指定点开始，在一定范围内广播消息，并且不向源角色发送消息。
     *
     * Broadcasts a message starting from a specified point within a certain range and does not send it to the source character.
     *
     * @param {Character} source - 消息的源角色。The source character of the message.
     * @param {Packet} packet - 要广播的数据包。The packet to be broadcasted.
     * @param {Point} rangedFrom - 广播的起点位置。The starting point for broadcasting.
     */
    public void broadcastMessage(Character source, Packet packet, Point rangedFrom) {
        broadcastMessage(source, packet, getRangedDistance(), rangedFrom);
    }

    /**
     * 核心广播方法，负责实际的消息分发工作。
     *
     * Core method responsible for actually dispatching the message.
     *
     * @param {Character} source - 消息的源角色。The source character of the message.
     * @param {Packet} packet - 要广播的数据包。The packet to be broadcasted.
     * @param {double} rangeSq - 广播的最大距离平方值。The maximum distance squared for broadcasting.
     * @param {Point} rangedFrom - 广播的起点位置。The starting point for broadcasting.
     */
    private void broadcastMessage(Character source, Packet packet, double rangeSq, Point rangedFrom) {
        chrRLock.lock();
        try {
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character chr = iterator.next();
                if (chrDisconnected(iterator, chr)) {
                    continue;
                }
                if (chr != source) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getPosition()) <= rangeSq) {
                            chr.sendPacket(packet);
                        }
                    } else {
                        chr.sendPacket(packet);
                    }
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    private boolean chrDisconnected(Iterator<Character> iterator, Character chr) {
        // 如果玩家已经掉线，则移除地图该玩家，但不确保频道、大区该玩家是否仍会引发异常
        if (chr == null || chr.getClient() == null) {
            iterator.remove();
            return true;
        }
        return false;
    }

    private void updateBossSpawn(Monster monster) {
        if (monster.hasBossHPBar()) {
            broadcastBossHpMessage(monster, monster.hashCode(), monster.makeBossHPBarPacket(), monster.getPosition());
        }
        if (monster.isBoss()) {
            if (unclaimOwnership() != null) {
                String mobName = MonsterInformationProvider.getInstance().getMobNameFromId(monster.getId());
                if (mobName != null) {
                    mobName = mobName.trim();
                    this.dropMessage(5, "这片草坪已被" + mobName + "的部队占领，击败他们才能夺回控制权！");
                }
            }
        }
    }

    /**
     * 向地图广播Boss、HP、Message。
     * @param mm mm
     * @param bossHash bossHash
     * @param packet 网络数据包
     */
    public void broadcastBossHpMessage(Monster mm, int bossHash, Packet packet) {
        broadcastBossHpMessage(mm, bossHash, null, packet, Double.POSITIVE_INFINITY, null);
    }

    /**
     * 向地图广播Boss、HP、Message。
     * @param mm mm
     * @param bossHash bossHash
     * @param packet 网络数据包
     * @param rangedFrom rangedFrom
     */
    public void broadcastBossHpMessage(Monster mm, int bossHash, Packet packet, Point rangedFrom) {
        broadcastBossHpMessage(mm, bossHash, null, packet, getRangedDistance(), rangedFrom);
    }

    private void broadcastBossHpMessage(Monster mm, int bossHash, Character source, Packet packet, double rangeSq, Point rangedFrom) {
        chrRLock.lock();
        try {
            for (Character chr : characters) {
                if (chr != source) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getPosition()) <= rangeSq) {
                            chr.getClient().announceBossHpBar(mm, bossHash, packet);
                        }
                    } else {
                        chr.getClient().announceBossHpBar(mm, bossHash, packet);
                    }
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    private void broadcastItemDropMessage(MapItem mdrop, Point dropperPos, Point dropPos, byte mod, Point rangedFrom) {
        broadcastItemDropMessage(mdrop, dropperPos, dropPos, mod, getRangedDistance(), rangedFrom);
    }

    private void broadcastItemDropMessage(MapItem mdrop, Point dropperPos, Point dropPos, byte mod) {
        broadcastItemDropMessage(mdrop, dropperPos, dropPos, mod, Double.POSITIVE_INFINITY, null);
    }

    private void broadcastItemDropMessage(MapItem mdrop, Point dropperPos, Point dropPos, byte mod, double rangeSq, Point rangedFrom) {
        chrRLock.lock();
        try {
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character chr = iterator.next();
                if (chrDisconnected(iterator, chr)) {
                    continue;
                }
                Packet packet = PacketCreator.dropItemFromMapObject(chr, mdrop, dropperPos, dropPos, mod);

                if (rangeSq < Double.POSITIVE_INFINITY) {
                    if (rangedFrom.distanceSq(chr.getPosition()) <= rangeSq) {
                        chr.sendPacket(packet);
                    }
                } else {
                    chr.sendPacket(packet);
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 向地图广播刷新、玩家、地图、对象、Message。
     * @param source 来源角色
     * @param player 玩家
     * @param enteringField enteringField
     */
    public void broadcastSpawnPlayerMapObjectMessage(Character source, Character player, boolean enteringField) {
        broadcastSpawnPlayerMapObjectMessage(source, player, enteringField, false);
    }

    /**
     * 向地图广播G、M、刷新、玩家、地图、对象、Message。
     * @param source 来源角色
     * @param player 玩家
     * @param enteringField enteringField
     */
    public void broadcastGMSpawnPlayerMapObjectMessage(Character source, Character player, boolean enteringField) {
        broadcastSpawnPlayerMapObjectMessage(source, player, enteringField, true);
    }

    private void broadcastSpawnPlayerMapObjectMessage(Character source, Character player, boolean enteringField, boolean gmBroadcast) {
        chrRLock.lock();
        try {
            if (gmBroadcast) {
                Iterator<Character> iterator = characters.iterator();
                while (iterator.hasNext()) {
                    Character chr = iterator.next();
                    if (chrDisconnected(iterator, chr)) {
                        continue;
                    }
                    if (chr.isGM()) {
                        if (chr != source) {
                            chr.sendPacket(PacketCreator.spawnPlayerMapObject(chr.getClient(), player, enteringField));
                        }
                    }
                }
            } else {
                Iterator<Character> iterator = characters.iterator();
                while (iterator.hasNext()) {
                    Character chr = iterator.next();
                    if (chrDisconnected(iterator, chr)) {
                        continue;
                    }
                    if (chr != source) {
                        chr.sendPacket(PacketCreator.spawnPlayerMapObject(chr.getClient(), player, enteringField));
                    }
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 向地图广播更新、Char、Look、Message。
     * @param source 来源角色
     * @param player 玩家
     */
    public void broadcastUpdateCharLookMessage(Character source, Character player) {
        chrRLock.lock();
        try {
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character chr = iterator.next();
                if (chrDisconnected(iterator, chr)) {
                    continue;
                }
                if (chr != source) {
                    chr.sendPacket(PacketCreator.updateCharLook(chr.getClient(), player));
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 掉落Message。
     * @param type 类型
     * @param message message
     */
    public void dropMessage(int type, String message) {
        broadcastStringMessage(type, message);
    }

    /**
     * 向地图广播String、Message。
     * @param type 类型
     * @param message message
     */
    public void broadcastStringMessage(int type, String message) {
        broadcastMessage(PacketCreator.serverNotice(type, message));
    }

    private static boolean isNonRangedType(MapObjectType type) {
        switch (type) {
            case NPC:
            case PLAYER:
            case HIRED_MERCHANT:
            case PLAYER_NPC:
            case DRAGON:
            case MIST:
            case KITE:
                return true;
            default:
                return false;
        }
    }

    private void sendObjectPlacement(Client c) {
        Character chr = c.getPlayer();
        Collection<MapObject> objects;

        objectRLock.lock();
        try {
            objects = new ArrayList<>(mapobjects.values());
        } finally {
            objectRLock.unlock();
        }

        for (MapObject o : objects) {
            if (isNonRangedType(o.getType())) {
                o.sendSpawnData(c);
            } else if (o.getType() == MapObjectType.SUMMON) {
                Summon summon = (Summon) o;
                if (summon.getOwner() == chr) {
                    if (chr.isSummonsEmpty() || !chr.containsSummon(summon)) {
                        objectWLock.lock();
                        try {
                            mapobjects.remove(o.getObjectId());
                        } finally {
                            objectWLock.unlock();
                        }

                        //continue;
                    }
                }
            }
        }

        if (chr != null) {
            for (MapObject o : getMapObjectsInRange(chr.getPosition(), getRangedDistance(), rangedMapobjectTypes)) {
                if (o.getType() == MapObjectType.REACTOR) {
                    if (((Reactor) o).isAlive()) {
                        o.sendSpawnData(chr.getClient());
                        chr.addVisibleMapObject(o);
                    }
                } else {
                    o.sendSpawnData(chr.getClient());
                    chr.addVisibleMapObject(o);

                    if (o.getType() == MapObjectType.MONSTER) {
                        ((Monster) o).aggroUpdateController();
                    }
                }
            }
        }
    }

    /**
     * 获取地图对象在范围。
     * @param from from
     * @param rangeSq rangeSq
     * @param types 对象类型列表（MapObjectType 列表/集合）
     * @return List<MapObject> 类型结果
     */
    public List<MapObject> getMapObjectsInRange(Point from, double rangeSq, List<MapObjectType> types) {
        List<MapObject> ret = new LinkedList<>();
        objectRLock.lock();
        try {
            for (MapObject l : mapobjects.values()) {
                if (types.contains(l.getType())) {
                    if (from.distanceSq(l.getPosition()) <= rangeSq) {
                        ret.add(l);
                    }
                }
            }
            return ret;
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 获取地图对象在区域。
     * @param box 矩形区域
     * @param types 对象类型列表（MapObjectType 列表/集合）
     * @return List<MapObject> 类型结果
     */
    public List<MapObject> getMapObjectsInBox(Rectangle box, List<MapObjectType> types) {
        List<MapObject> ret = new LinkedList<>();
        objectRLock.lock();
        try {
            for (MapObject l : mapobjects.values()) {
                if (types.contains(l.getType())) {
                    if (box.contains(l.getPosition())) {
                        ret.add(l);
                    }
                }
            }
            return ret;
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 添加传送门。
     * @param myPortal myPortal
     */
    public void addPortal(Portal myPortal) {
        portals.put(myPortal.getId(), myPortal);
    }

    /**
     * 获取传送门。
     * @param portalname portalname
     * @return Portal 类型结果
     */
    public Portal getPortal(String portalname) {
        for (Portal port : portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    /**
     * 获取传送门。
     * @param portalid portalid
     * @return Portal 类型结果
     */
    public Portal getPortal(int portalid) {
        return portals.get(portalid);
    }

    /**
     * 添加冒险岛区域。
     * @param rec rec
     */
    public void addMapleArea(Rectangle rec) {
        areas.add(rec);
    }

    /**
     * 获取Areas。
     * @return List<Rectangle> 类型结果
     */
    public List<Rectangle> getAreas() {
        return new ArrayList<>(areas);
    }

    /**
     * 获取区域。
     * @param index index
     * @return Rectangle 类型结果
     */
    public Rectangle getArea(int index) {
        return areas.get(index);
    }

    /**
     * 设置落脚点。
     * @param footholds footholds
     */
    public void setFootholds(FootholdTree footholds) {
        this.footholds = footholds;
    }

    /**
     * 获取落脚点。
     * @return FootholdTree 类型结果
     */
    public FootholdTree getFootholds() {
        return footholds;
    }

    /**
     * 设置地图、坐标、Boundings。
     * @param px px
     * @param py py
     * @param h h
     * @param w w
     */
    public void setMapPointBoundings(int px, int py, int h, int w) {
        mapArea.setBounds(px, py, w, h);
    }

    /**
     * 设置地图、Line、Boundings。
     * @param vrTop vrTop
     * @param vrBottom vrBottom
     * @param vrLeft vrLeft
     * @param vrRight vrRight
     */
    public void setMapLineBoundings(int vrTop, int vrBottom, int vrLeft, int vrRight) {
        mapArea.setBounds(vrLeft, vrTop, vrRight - vrLeft, vrBottom - vrTop);
    }

    /**
     * 获取仇恨、Coordinator。
     * @return MonsterAggroCoordinator 类型结果
     */
    public MonsterAggroCoordinator getAggroCoordinator() {
        return aggroMonitor;
    }

    /**
     * 添加怪物刷新。
     * @param monster 怪物
     * @param mobTime mobTime
     * @param team team
     */
    public void addMonsterSpawn(Monster monster, int mobTime, int team) {
        Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        SpawnPoint sp = new SpawnPoint(monster, newpos, !monster.isMobile(), mobTime, mobInterval, team);
        monsterSpawn.add(sp);
        if (sp.shouldSpawn() || mobTime == -1) {// -1 does not respawn and should not either but force ONE spawn
            spawnMonster(sp.getMonster());
        }
    }

    /**
     * 添加所有怪物刷新。
     * @param monster 怪物
     * @param mobTime mobTime
     * @param team team
     */
    public void addAllMonsterSpawn(Monster monster, int mobTime, int team) {
        Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        SpawnPoint sp = new SpawnPoint(monster, newpos, !monster.isMobile(), mobTime, mobInterval, team);
        allMonsterSpawn.add(sp);
    }

    /**
     * 移除怪物刷新。
     * @param mobId mobId
     * @param x x
     * @param y y
     */
    public void removeMonsterSpawn(int mobId, int x, int y) {
        // assumption: spawn points identifies by tuple (lifeid, x, y)

        Point checkpos = calcPointBelow(new Point(x, y));
        checkpos.y -= 1;

        List<SpawnPoint> toRemove = new LinkedList<>();
        for (SpawnPoint sp : getMonsterSpawn()) {
            Point pos = sp.getPosition();
            if (sp.getMonsterId() == mobId && checkpos.equals(pos)) {
                toRemove.add(sp);
            }
        }

        if (!toRemove.isEmpty()) {
            synchronized (monsterSpawn) {
                for (SpawnPoint sp : toRemove) {
                    monsterSpawn.remove(sp);
                }
            }
        }
    }

    /**
     * 移除所有怪物刷新。
     * @param mobId mobId
     * @param x x
     * @param y y
     */
    public void removeAllMonsterSpawn(int mobId, int x, int y) {
        // assumption: spawn points identifies by tuple (lifeid, x, y)

        Point checkpos = calcPointBelow(new Point(x, y));
        checkpos.y -= 1;

        List<SpawnPoint> toRemove = new LinkedList<>();
        for (SpawnPoint sp : getAllMonsterSpawn()) {
            Point pos = sp.getPosition();
            if (sp.getMonsterId() == mobId && checkpos.equals(pos)) {
                toRemove.add(sp);
            }
        }

        if (!toRemove.isEmpty()) {
            synchronized (allMonsterSpawn) {
                for (SpawnPoint sp : toRemove) {
                    allMonsterSpawn.remove(sp);
                }
            }
        }
    }

    /**
     * 执行 report、怪物、刷新、Points 操作。
     * @param chr 角色
     */
    public void reportMonsterSpawnPoints(Character chr) {
        // 输出地图刷怪点统计信息头
        chr.dropMessage(6, "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        chr.dropMessage(6, "┃ 地图ID: " + getId() + " | 总刷怪点: " + monsterSpawn.size() +  " | 已刷怪: " + spawnedMonstersOnMap.get());
        chr.dropMessage(6, "┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // 遍历所有刷怪点输出详细信息
        for (SpawnPoint sp : getAllMonsterSpawn()) {
            chr.dropMessage(6,
                    "┃ ID:" + sp.getMonsterId() + " | 可刷怪:" + (sp.getDenySpawn() ? "×" : "√") + " | 现存:" + sp.getSpawned() + "\n" +
                    "┃ 坐标:(" +(int) sp.getPosition().getX() + " , " + (int) sp.getPosition().getY() + ") | 刷新:" + sp.getMobTime() + "ms | 阵营:" + sp.getTeam()
            );
        }
        chr.dropMessage(6, "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    /**
     * 获取地图玩家。
     * @return Map<Integer, Character> 类型结果
     */
    public Map<Integer, Character> getMapPlayers() {
        chrRLock.lock();
        try {
            Map<Integer, Character> mapChars = new HashMap<>(characters.size());

            for (Character chr : characters) {
                mapChars.put(chr.getId(), chr);
            }

            return mapChars;
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 获取Characters。
     * @return Collection<Character> 类型结果
     */
    public Collection<Character> getCharacters() {
        chrRLock.lock();
        try {
            return Collections.unmodifiableCollection(this.characters);
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 获取角色按ID。
     * @param id ID
     * @return Character 类型结果
     */
    public Character getCharacterById(int id) {
        chrRLock.lock();
        try {
            for (Character chr : this.characters) {
                if (chr.getId() == id) {
                    return chr;
                }
            }
        } finally {
            chrRLock.unlock();
        }
        return null;
    }

    private static void updateMapObjectVisibility(Character chr, MapObject mo) {
        if (!chr.isMapObjectVisible(mo)) { // object entered view range
            if (mo.getType() == MapObjectType.SUMMON || mo.getPosition().distanceSq(chr.getPosition()) <= getRangedDistance()) {
                chr.addVisibleMapObject(mo);
                mo.sendSpawnData(chr.getClient());
            }
        } else if (mo.getType() != MapObjectType.SUMMON && mo.getPosition().distanceSq(chr.getPosition()) > getRangedDistance()) {
            chr.removeVisibleMapObject(mo);
            mo.sendDestroyData(chr.getClient());
        }
    }

    /**
     * 执行 move、怪物 操作。
     * @param monster 怪物
     * @param reportedPos reportedPos
     */
    public void moveMonster(Monster monster, Point reportedPos) {
        monster.setPosition(reportedPos);
        for (Character chr : getAllPlayers()) {
            updateMapObjectVisibility(chr, monster);
        }
    }

    /**
     * 执行 move、玩家 操作。
     * @param player 玩家
     * @param newPosition newPosition
     */
    public void movePlayer(Character player, Point newPosition) {
        player.setPosition(newPosition);

        try {
            MapObject[] visibleObjects = player.getVisibleMapObjects();

            Map<Integer, MapObject> mapObjects = getCopyMapObjects();
            for (MapObject mo : visibleObjects) {
                if (mo != null) {
                    if (mapObjects.get(mo.getObjectId()) == mo) {
                        updateMapObjectVisibility(player, mo);
                    } else {
                        player.removeVisibleMapObject(mo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (MapObject mo : getMapObjectsInRange(player.getPosition(), getRangedDistance(), rangedMapobjectTypes)) {
            if (!player.isMapObjectVisible(mo)) {
                mo.sendSpawnData(player.getClient());
                player.addVisibleMapObject(mo);
            }
        }
    }

    /**
     * 切换环境开关状态。
     * @param ms ms
     */
    public final void toggleEnvironment(final String ms) {
        Map<String, Integer> env = getEnvironment();

        if (env.containsKey(ms)) {
            moveEnvironment(ms, env.get(ms) == 1 ? 2 : 1);
        } else {
            moveEnvironment(ms, 1);
        }
    }

    /**
     * 执行 move、环境 操作。
     * @param ms ms
     * @param type 类型
     */
    public final void moveEnvironment(final String ms, final int type) {
        broadcastMessage(PacketCreator.environmentMove(ms, type));

        objectWLock.lock();
        try {
            environment.put(ms, type);
        } finally {
            objectWLock.unlock();
        }
    }

    /**
     * 获取环境。
     * @return Map<String, Integer> 类型结果
     */
    public final Map<String, Integer> getEnvironment() {
        objectRLock.lock();
        try {
            return Collections.unmodifiableMap(environment);
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 获取地图名称。
     * @return String 类型结果
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * 设置地图名称。
     * @param mapName mapName
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * 获取街道名称。
     * @return String 类型结果
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * 设置时钟。
     * @param hasClock hasClock
     */
    public void setClock(boolean hasClock) {
        this.clock = hasClock;
    }

    /**
     * 判断是否拥有时钟。
     * @return boolean 类型结果
     */
    public boolean hasClock() {
        return clock;
    }

    /**
     * 设置Town。
     * @param isTown isTown
     */
    public void setTown(boolean isTown) {
        this.town = isTown;
    }

    /**
     * 判断是否为Town。
     * @return boolean 类型结果
     */
    public boolean isTown() {
        return town;
    }

    /**
     * 判断是否为Muted。
     * @return boolean 类型结果
     */
    public boolean isMuted() {
        return isMuted;
    }

    /**
     * 设置Muted。
     * @param mute mute
     */
    public void setMuted(boolean mute) {
        isMuted = mute;
    }

    /**
     * 设置街道名称。
     * @param streetName streetName
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    /**
     * 设置Everlast。
     * @param everlast everlast
     */
    public void setEverlast(boolean everlast) {
        this.everlast = everlast;
    }

    /**
     * 获取Everlast。
     * @return boolean 类型结果
     */
    public boolean getEverlast() {
        return everlast;
    }

    /**
     * 获取Spawned、怪物、在、地图。
     * @return int 类型结果
     */
    public int getSpawnedMonstersOnMap() {
        return spawnedMonstersOnMap.get();
    }

    /**
     * 设置怪物、Capacity。
     * @param capacity capacity
     */
    public void setMobCapacity(int capacity) {
        this.mobCapacity = capacity;
    }

    /**
     * 设置背景、Types。
     * @param backTypes backTypes
     */
    public void setBackgroundTypes(HashMap<Integer, Integer> backTypes) {
        backgroundTypes.putAll(backTypes);
    }

    // not really costly to keep generating imo
    /**
     * 执行 send、Night、效果 操作。
     * @param chr 角色
     */
    public void sendNightEffect(Character chr) {
        for (Entry<Integer, Integer> types : backgroundTypes.entrySet()) {
            if (types.getValue() >= 3) { // 3 is a special number
                chr.sendPacket(PacketCreator.changeBackgroundEffect(true, types.getKey(), 0));
            }
        }
    }

    /**
     * 向地图广播Night、效果。
     */
    public void broadcastNightEffect() {
        chrRLock.lock();
        try {
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character chr = iterator.next();
                if (chrDisconnected(iterator, chr)) {
                    continue;
                }
                sendNightEffect(chr);
            }
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 获取角色按名称。
     * @param name name
     * @return Character 类型结果
     */
    public Character getCharacterByName(String name) {
        chrRLock.lock();
        try {
            for (Character chr : this.characters) {
                if (chr.getName().equalsIgnoreCase(name)) {
                    return chr;
                }
            }
        } finally {
            chrRLock.unlock();
        }
        return null;
    }

    /**
     * 执行 make、Disappear、物品、来自、地图 操作。
     * @param mapobj mapobj
     * @return boolean 类型结果
     */
    public boolean makeDisappearItemFromMap(MapObject mapobj) {
        if (mapobj instanceof MapItem) {
            return makeDisappearItemFromMap((MapItem) mapobj);
        } else {
            return mapobj == null;  // no drop to make disappear...
        }
    }

    /**
     * 执行 make、Disappear、物品、来自、地图 操作。
     * @param mapitem mapitem
     * @return boolean 类型结果
     */
    public boolean makeDisappearItemFromMap(MapItem mapitem) {
        if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId())) {
            mapitem.lockItem();
            try {
                if (mapitem.isPickedUp()) {
                    return true;
                }

                MapleMap.this.pickItemDrop(PacketCreator.removeItemFromMap(mapitem.getObjectId(), 0, 0), mapitem);
                return true;
            } finally {
                mapitem.unlockItem();
            }
        }

        return false;
    }

    private class MobLootEntry implements Runnable {

        private final byte droptype;
        private final int mobpos;
        private final float chRate;
        private final Point pos;
        private final List<MonsterDropEntry> dropEntry;
        private final List<MonsterDropEntry> visibleQuestEntry;
        private final List<MonsterDropEntry> otherQuestEntry;
        private final List<MonsterGlobalDropEntry> globalEntry;
        private final Character chr;
        private final Monster mob;

        protected MobLootEntry(byte droptype, int mobpos, float chRate, Point pos, List<MonsterDropEntry> dropEntry, List<MonsterDropEntry> visibleQuestEntry, List<MonsterDropEntry> otherQuestEntry, List<MonsterGlobalDropEntry> globalEntry, Character chr, Monster mob) {
            this.droptype = droptype;
            this.mobpos = mobpos;
            this.chRate = chRate;
            this.pos = pos;
            this.dropEntry = dropEntry;
            this.visibleQuestEntry = visibleQuestEntry;
            this.otherQuestEntry = otherQuestEntry;
            this.globalEntry = globalEntry;
            this.chr = chr;
            this.mob = mob;
        }

        /**
         * 执行动作逻辑。
         */
        @Override
        public void run() {
            byte d = 1;

            // 普通掉落
            d = dropItemsFromMonsterOnMap(dropEntry, pos, d, chRate, droptype, mobpos, chr, mob);

            // Global Drops
            d = dropGlobalItemsFromMonsterOnMap(globalEntry, pos, d, droptype, mobpos, chr, mob);

            // Quest Drops
            d = dropItemsFromMonsterOnMap(visibleQuestEntry, pos, d, chRate, droptype, mobpos, chr, mob);
            dropItemsFromMonsterOnMap(otherQuestEntry, pos, d, chRate, droptype, mobpos, chr, mob);
        }
    }

    private class ActivateItemReactor implements Runnable {

        private final MapItem mapitem;
        private final Reactor reactor;
        private final Client c;

        /**
         * 执行 Activate、物品、反应堆 操作。
         * @param mapitem mapitem
         * @param reactor 反应堆
         * @param c c
         * @return ActivateItemReactor 类型结果
         */
        public ActivateItemReactor(MapItem mapitem, Reactor reactor, Client c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        /**
         * 执行动作逻辑。
         */
        @Override
        public void run() {
            reactor.hitLockReactor();
            try {
                if (reactor.getReactorType() == 100) {
                    if (reactor.getShouldCollect() == true && mapitem != null && mapitem == getMapObject(mapitem.getObjectId())) {
                        mapitem.lockItem();
                        try {
                            if (mapitem.isPickedUp()) {
                                return;
                            }
                            mapitem.setPickedUp(true);
                            unregisterItemDrop(mapitem);

                            reactor.setShouldCollect(false);
                            MapleMap.this.broadcastMessage(PacketCreator.removeItemFromMap(mapitem.getObjectId(), 0, 0), mapitem.getPosition());

                            droppedItemCount.decrementAndGet();
                            MapleMap.this.removeMapObject(mapitem);

                            reactor.hitReactor(c);

                            if (reactor.getDelay() > 0) {
                                MapleMap reactorMap = reactor.getMap();

                                OverallService service = (OverallService) reactorMap.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
                                service.registerOverallAction(reactorMap.getId(), () -> {
                                    reactor.lockReactor();
                                    try {
                                        reactor.resetReactorActions(0);
                                        reactor.setAlive(true);
                                        broadcastMessage(PacketCreator.triggerReactor(reactor, 0));
                                    } finally {
                                        reactor.unlockReactor();
                                    }
                                }, reactor.getDelay());
                            }
                        } finally {
                            mapitem.unlockItem();
                        }
                    }
                }
            } finally {
                reactor.hitUnlockReactor();
            }
        }
    }

    /**
     * 执行 instance、地图、First、刷新 操作。
     * @param difficulty difficulty
     * @param isPq isPq
     */
    public void instanceMapFirstSpawn(int difficulty, boolean isPq) {
        for (SpawnPoint spawnPoint : getAllMonsterSpawn()) {
            if (spawnPoint.getMobTime() == -1) {   //just those allowed to be spawned only once
                spawnMonster(spawnPoint.getMonster());
            }
        }
    }

    /**
     * 执行 instance、地图、Respawn 操作。
     */
    public void instanceMapRespawn() {
        if (!allowSummons) {
            return;
        }

        final int numShouldSpawn = (short) ((monsterSpawn.size() - spawnedMonstersOnMap.get()));//Fking lol'd
        if (numShouldSpawn > 0) {
            List<SpawnPoint> randomSpawn = getMonsterSpawn();
            Collections.shuffle(randomSpawn);
            int spawned = 0;
            for (SpawnPoint spawnPoint : randomSpawn) {
                if (spawnPoint.shouldSpawn()) {
                    spawnMonster(spawnPoint.getMonster());
                    spawned++;
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 执行 instance、地图、Force、Respawn 操作。
     */
    public void instanceMapForceRespawn() {
        if (!allowSummons) {
            return;
        }

        final int numShouldSpawn = (short) ((monsterSpawn.size() - spawnedMonstersOnMap.get()));//Fking lol'd
        if (numShouldSpawn > 0) {
            List<SpawnPoint> randomSpawn = getMonsterSpawn();
            Collections.shuffle(randomSpawn);
            int spawned = 0;
            for (SpawnPoint spawnPoint : randomSpawn) {
                if (spawnPoint.shouldForceSpawn()) {
                    spawnMonster(spawnPoint.getMonster());
                    spawned++;
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 执行 close、地图、刷新、Points 操作。
     */
    public void closeMapSpawnPoints() {
        for (SpawnPoint spawnPoint : getMonsterSpawn()) {
            spawnPoint.setDenySpawn(true);
        }
    }

    /**
     * 执行 restore、地图、刷新、Points 操作。
     */
    public void restoreMapSpawnPoints() {
        for (SpawnPoint spawnPoint : getMonsterSpawn()) {
            spawnPoint.setDenySpawn(false);
        }
    }

    /**
     * 设置Allow、刷新、坐标、在、区域。
     * @param allow allow
     * @param box 矩形区域
     */
    public void setAllowSpawnPointInBox(boolean allow, Rectangle box) {
        for (SpawnPoint sp : getMonsterSpawn()) {
            if (box.contains(sp.getPosition())) {
                sp.setDenySpawn(!allow);
            }
        }
    }

    /**
     * 设置Allow、刷新、坐标、在、范围。
     * @param allow allow
     * @param from from
     * @param rangeSq rangeSq
     */
    public void setAllowSpawnPointInRange(boolean allow, Point from, double rangeSq) {
        for (SpawnPoint sp : getMonsterSpawn()) {
            if (from.distanceSq(sp.getPosition()) <= rangeSq) {
                sp.setDenySpawn(!allow);
            }
        }
    }

    /**
     * 查找Closest、Spawnpoint。
     * @param from from
     * @return SpawnPoint 类型结果
     */
    public SpawnPoint findClosestSpawnpoint(Point from) {
        SpawnPoint closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (SpawnPoint sp : getMonsterSpawn()) {
            double distance = sp.getPosition().distanceSq(from);
            if (distance < shortestDistance) {
                closest = sp;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    private static double getCurrentSpawnRate(int numPlayers) {
        return 0.70 + (0.05 * Math.min(6, numPlayers));
    }

    private int getNumShouldSpawn(int numPlayers) {
        /*
        System.out.println("----------------------------------");
        for (SpawnPoint spawnPoint : getMonsterSpawn()) {
            System.out.println("sp " + spawnPoint.getPosition().getX() + ", " + spawnPoint.getPosition().getY() + ": " + spawnPoint.getDenySpawn());
        }
        System.out.println("try " + monsterSpawn.size() + " - " + spawnedMonstersOnMap.get());
        System.out.println("----------------------------------");
        */

        if (GameConfig.getServerBoolean("use_enable_full_respawn")) {
            return (monsterSpawn.size() - spawnedMonstersOnMap.get());
        }

        int maxNumShouldSpawn = (int) Math.ceil(getCurrentSpawnRate(numPlayers) * monsterSpawn.size());
        return maxNumShouldSpawn - spawnedMonstersOnMap.get();
    }

    /**
     * 执行 respawn 操作。
     */
    public void respawn() {
        if (!allowSummons) {
            return;
        }

        int numPlayers;
        chrRLock.lock();
        try {
            numPlayers = characters.size();

            if (numPlayers == 0) {
                return;
            }
        } finally {
            chrRLock.unlock();
        }

        int numShouldSpawn = getNumShouldSpawn(numPlayers);
        if (numShouldSpawn > 0) {
            List<SpawnPoint> randomSpawn = new ArrayList<>(getMonsterSpawn());
            Collections.shuffle(randomSpawn);
            short spawned = 0;
            for (SpawnPoint spawnPoint : randomSpawn) {
                if (spawnPoint.shouldSpawn()) {
                    spawnMonster(spawnPoint.getMonster());
                    spawned++;

                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 执行 mob、MP、Recovery 操作。
     */
    public void mobMpRecovery() {
        for (Monster mob : this.getAllMonsters()) {
            if (mob.isAlive()) {
                mob.heal(0, mob.getLevel());
            }
        }
    }

    /**
     * 获取Num玩家在区域。
     * @param index index
     * @return int 类型结果
     */
    public final int getNumPlayersInArea(final int index) {
        return getNumPlayersInRect(getArea(index));
    }

    /**
     * 获取Num、玩家、在、矩形区域。
     * @param rect rect
     * @return int 类型结果
     */
    public final int getNumPlayersInRect(final Rectangle rect) {
        int ret = 0;

        chrRLock.lock();
        try {
            final Iterator<Character> ltr = characters.iterator();
            while (ltr.hasNext()) {
                if (rect.contains(ltr.next().getPosition())) {
                    ret++;
                }
            }
        } finally {
            chrRLock.unlock();
        }
        return ret;
    }

    /**
     * 获取Num玩家物品在区域。
     * @param index index
     * @return int 类型结果
     */
    public final int getNumPlayersItemsInArea(final int index) {
        return getNumPlayersItemsInRect(getArea(index));
    }

    /**
     * 获取Num、玩家、物品、在、矩形区域。
     * @param rect rect
     * @return int 类型结果
     */
    public final int getNumPlayersItemsInRect(final Rectangle rect) {
        int retP = getNumPlayersInRect(rect);
        int retI = getMapObjectsInBox(rect, Arrays.asList(MapObjectType.ITEM)).size();

        return retP + retI;
    }

    private interface DelayedPacketCreation {

        void sendPackets(Client c);
    }

    private interface SpawnCondition {

        boolean canSpawn(Character chr);
    }

    /**
     * 获取HPDec。
     * @return int 类型结果
     */
    public int getHPDec() {
        return decHP;
    }

    /**
     * 设置HPDec。
     * @param delta delta
     */
    public void setHPDec(int delta) {
        decHP = delta;
    }

    /**
     * 获取H、P、Dec、Protect。
     * @return int 类型结果
     */
    public int getHPDecProtect() {
        return protectItem;
    }

    /**
     * 设置H、P、Dec、Protect。
     * @param delta delta
     */
    public void setHPDecProtect(int delta) {
        this.protectItem = delta;
    }

    /**
     * 获取Recovery。
     * @return float 类型结果
     */
    public float getRecovery() {
        return recovery;
    }

    /**
     * 设置Recovery。
     * @param recRate recRate
     */
    public void setRecovery(float recRate) {
        recovery = recRate;
    }

    private int hasBoat() {
        return !boat ? 0 : (docked ? 1 : 2);
    }

    /**
     * 设置船只。
     * @param hasBoat hasBoat
     */
    public void setBoat(boolean hasBoat) {
        this.boat = hasBoat;
    }

    /**
     * 设置停靠。
     * @param isDocked isDocked
     */
    public void setDocked(boolean isDocked) {
        this.docked = isDocked;
    }

    /**
     * 获取停靠。
     * @return boolean 类型结果
     */
    public boolean getDocked() {
        return this.docked;
    }

    /**
     * 设置座位。
     * @param seats seats
     */
    public void setSeats(int seats) {
        this.seats = seats;
    }

    /**
     * 获取座位。
     * @return int 类型结果
     */
    public int getSeats() {
        return seats;
    }

    /**
     * 向地图广播G、M、Message。
     * @param source 来源角色
     * @param packet 网络数据包
     * @param repeatToSource repeatToSource
     */
    public void broadcastGMMessage(Character source, Packet packet, boolean repeatToSource) {
        broadcastGMMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }

    private void broadcastGMMessage(Character source, Packet packet, double rangeSq, Point rangedFrom) {
        chrRLock.lock();
        try {
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character chr = iterator.next();
                if (chrDisconnected(iterator, chr)) {
                    continue;
                }
                if (chr != source && chr.isGM()) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getPosition()) <= rangeSq) {
                            chr.sendPacket(packet);
                        }
                    } else {
                        chr.sendPacket(packet);
                    }
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 向地图广播N、O、N、G、M、Message。
     * @param source 来源角色
     * @param packet 网络数据包
     * @param repeatToSource repeatToSource
     */
    public void broadcastNONGMMessage(Character source, Packet packet, boolean repeatToSource) {
        chrRLock.lock();
        try {
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character chr = iterator.next();
                if (chrDisconnected(iterator, chr)) {
                    continue;
                }
                if (chr != source && !chr.isGM()) {
                    chr.sendPacket(packet);
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 获取OX。
     * @return OxQuiz 类型结果
     */
    public OxQuiz getOx() {
        return ox;
    }

    /**
     * 设置OX。
     * @param set set
     */
    public void setOx(OxQuiz set) {
        this.ox = set;
    }

    /**
     * 设置OX问答。
     * @param b b
     */
    public void setOxQuiz(boolean b) {
        this.isOxQuiz = b;
    }

    /**
     * 判断是否为OX问答。
     * @return boolean 类型结果
     */
    public boolean isOxQuiz() {
        return isOxQuiz;
    }

    /**
     * 设置在、User、进入。
     * @param onUserEnter onUserEnter
     */
    public void setOnUserEnter(String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }

    /**
     * 获取在、User、进入。
     * @return String 类型结果
     */
    public String getOnUserEnter() {
        return onUserEnter;
    }

    /**
     * 设置在、First、User、进入。
     * @param onFirstUserEnter onFirstUserEnter
     */
    public void setOnFirstUserEnter(String onFirstUserEnter) {
        this.onFirstUserEnter = onFirstUserEnter;
    }

    /**
     * 获取在、First、User、进入。
     * @return String 类型结果
     */
    public String getOnFirstUserEnter() {
        return onFirstUserEnter;
    }

    private boolean hasForcedEquip() {
        return fieldType == 81 || fieldType == 82;
    }

    /**
     * 设置地图类型。
     * @param fieldType fieldType
     */
    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * 执行 clear、掉落 操作。
     * @param player 玩家
     */
    public void clearDrops(Character player) {
        for (MapObject i : getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.ITEM))) {
            droppedItemCount.decrementAndGet();
            removeMapObject(i);
            this.broadcastMessage(PacketCreator.removeItemFromMap(i.getObjectId(), 0, player.getId()));
        }
    }

    /**
     * 执行 clear、掉落 操作。
     */
    public void clearDrops() {
        for (MapObject i : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.ITEM))) {
            droppedItemCount.decrementAndGet();
            removeMapObject(i);
            this.broadcastMessage(PacketCreator.removeItemFromMap(i.getObjectId(), 0, 0));
        }
    }

    /**
     * 设置地图限制。
     * @param fieldLimit fieldLimit
     */
    public void setFieldLimit(int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }

    /**
     * 获取地图限制。
     * @return int 类型结果
     */
    public int getFieldLimit() {
        return fieldLimit;
    }

    /**
     * 执行 allow、召唤兽、状态 操作。
     * @param b b
     */
    public void allowSummonState(boolean b) {
        MapleMap.this.allowSummons = b;
    }

    /**
     * 获取召唤兽状态。
     * @return boolean 类型结果
     */
    public boolean getSummonState() {
        return MapleMap.this.allowSummons;
    }

    /**
     * 传送Everyone。
     * @param to to
     */
    public void warpEveryone(int to) {
        List<Character> players = new ArrayList<>(getCharacters());

        for (Character chr : players) {
            chr.changeMap(to);
        }
    }

    /**
     * 传送Everyone。
     * @param to to
     * @param pto pto
     */
    public void warpEveryone(int to, int pto) {
        List<Character> players = new ArrayList<>(getCharacters());

        for (Character chr : players) {
            chr.changeMap(to, pto);
        }
    }

    // BEGIN EVENTS
    /**
     * 设置雪球。
     * @param team team
     * @param ball ball
     */
    public void setSnowball(int team, Snowball ball) {
        switch (team) {
            case 0:
                this.snowball0 = ball;
                break;
            case 1:
                this.snowball1 = ball;
                break;
            default:
                break;
        }
    }

    /**
     * 获取雪球。
     * @param team team
     * @return Snowball 类型结果
     */
    public Snowball getSnowball(int team) {
        switch (team) {
            case 0:
                return snowball0;
            case 1:
                return snowball1;
            default:
                return null;
        }
    }

    private boolean specialEquip() {//Maybe I shouldn't use fieldType :\
        return fieldType == 4 || fieldType == 19;
    }

    /**
     * 设置椰子。
     * @param nut nut
     */
    public void setCoconut(Coconut nut) {
        this.coconut = nut;
    }

    /**
     * 获取椰子。
     * @return Coconut 类型结果
     */
    public Coconut getCoconut() {
        return coconut;
    }

    /**
     * 传送Out按队伍。
     * @param team team
     * @param mapid 地图 ID
     */
    public void warpOutByTeam(int team, int mapid) {
        List<Character> chars = new ArrayList<>(getCharacters());
        for (Character chr : chars) {
            if (chr != null) {
                if (chr.getTeam() == team) {
                    chr.changeMap(mapid);
                }
            }
        }
    }

    /**
     * 执行 start、事件 操作。
     * @param chr 角色
     */
    public void startEvent(final Character chr) {
        if (this.mapid == MapId.EVENT_COCONUT_HARVEST && getCoconut() == null) {
            setCoconut(new Coconut(this));
            coconut.startEvent();
        } else if (this.mapid == MapId.EVENT_PHYSICAL_FITNESS) {
            chr.setFitness(new Fitness(chr));
            chr.getFitness().startFitness();
        } else if (this.mapid == MapId.EVENT_OLA_OLA_1 || this.mapid == MapId.EVENT_OLA_OLA_2 ||
                this.mapid == MapId.EVENT_OLA_OLA_3 || this.mapid == MapId.EVENT_OLA_OLA_4) {
            chr.setOla(new Ola(chr));
            chr.getOla().startOla();
        } else if (this.mapid == MapId.EVENT_OX_QUIZ && getOx() == null) {
            setOx(new OxQuiz(this));
            getOx().sendQuestion();
            setOxQuiz(true);
        } else if (this.mapid == MapId.EVENT_SNOWBALL && getSnowball(chr.getTeam()) == null) {
            setSnowball(0, new Snowball(0, this));
            setSnowball(1, new Snowball(1, this));
            getSnowball(chr.getTeam()).startEvent();
        }
    }

    /**
     * 执行 event、Started 操作。
     * @return boolean 类型结果
     */
    public boolean eventStarted() {
        return eventstarted;
    }

    /**
     * 执行 start、事件 操作。
     */
    public void startEvent() {
        this.eventstarted = true;
    }

    /**
     * 设置事件、Started。
     * @param event event
     */
    public void setEventStarted(boolean event) {
        this.eventstarted = event;
    }

    /**
     * 获取事件NPC。
     * @return String 类型结果
     */
    public String getEventNPC() {
        StringBuilder sb = new StringBuilder();
        sb.append("请与 "+ mapName + " 的 ");
        if (mapid == MapId.SOUTHPERRY) {
            sb.append("珀尔");
        } else if (mapid == MapId.LITH_HARBOUR) {
            sb.append("江");
        } else if (mapid == MapId.ORBIS) {
            sb.append("马丁");
        } else if (mapid == MapId.LUDIBRIUM) {
            sb.append("托尼");
        } else {
            return null;
        }
        sb.append(" 进行对话。");
        return sb.toString();
    }

    /**
     * 判断是否拥有事件NPC。
     * @return boolean 类型结果
     */
    public boolean hasEventNPC() {
        return this.mapid == 60000 || this.mapid == MapId.LITH_HARBOUR || this.mapid == MapId.ORBIS || this.mapid == MapId.LUDIBRIUM;
    }

    /**
     * 判断是否为Starting、事件、地图。
     * @return boolean 类型结果
     */
    public boolean isStartingEventMap() {
        return this.mapid == MapId.EVENT_PHYSICAL_FITNESS || this.mapid == MapId.EVENT_OX_QUIZ ||
                this.mapid == MapId.EVENT_FIND_THE_JEWEL || this.mapid == MapId.EVENT_OLA_OLA_0 || this.mapid == MapId.EVENT_OLA_OLA_1;
    }

    /**
     * 判断是否为事件地图。
     * @return boolean 类型结果
     */
    public boolean isEventMap() {
        return this.mapid >= MapId.EVENT_FIND_THE_JEWEL && this.mapid < MapId.EVENT_WINNER || this.mapid > MapId.EVENT_EXIT && this.mapid <= 109090000;
    }

    /**
     * 设置时间怪物。
     * @param id ID
     * @param msg msg
     */
    public void setTimeMob(int id, String msg) {
        timeMob = new Pair<>(id, msg);
    }

    /**
     * 获取时间怪物。
     * @return Pair<Integer, String> 类型结果
     */
    public Pair<Integer, String> getTimeMob() {
        return timeMob;
    }

    /**
     * 切换Hidden、N、P、C开关状态。
     * @param id ID
     */
    public void toggleHiddenNPC(int id) {
        chrRLock.lock();
        objectRLock.lock();
        try {
            for (MapObject obj : mapobjects.values()) {
                if (obj.getType() == MapObjectType.NPC) {
                    NPC npc = (NPC) obj;
                    if (npc.getId() == id) {
                        npc.setHide(!npc.isHidden());
                        if (!npc.isHidden()) //Should only be hidden upon changing maps
                        {
                            broadcastMessage(PacketCreator.spawnNPC(npc));
                        }
                    }
                }
            }
        } finally {
            objectRLock.unlock();
            chrRLock.unlock();
        }
    }

    /**
     * 设置怪物间隔。
     * @param interval interval
     */
    public void setMobInterval(short interval) {
        this.mobInterval = interval;
    }

    /**
     * 获取怪物间隔。
     * @return short 类型结果
     */
    public short getMobInterval() {
        return mobInterval;
    }

    /**
     * 执行 clear、地图、对象 操作。
     */
    public void clearMapObjects() {
        clearDrops();
        killAllMonsters();
        resetReactors();
    }

    /**
     * 重置Fully。
     */
    public final void resetFully() {
        resetMapObjects();
    }

    /**
     * 重置地图对象。
     */
    public void resetMapObjects() {
        resetMapObjects(1, false);
    }

    /**
     * 重置PQ。
     */
    public void resetPQ() {
        resetPQ(1);
    }

    /**
     * 重置PQ。
     * @param difficulty difficulty
     */
    public void resetPQ(int difficulty) {
        resetMapObjects(difficulty, true);
    }

    /**
     * 重置地图对象。
     * @param difficulty difficulty
     * @param isPq isPq
     */
    public void resetMapObjects(int difficulty, boolean isPq) {
        clearMapObjects();

        restoreMapSpawnPoints();
        instanceMapFirstSpawn(difficulty, isPq);
    }

    /**
     * 向地图广播Ship。
     * @param state 状态值
     */
    public void broadcastShip(final boolean state) {
        broadcastMessage(PacketCreator.boatPacket(state));
        this.setDocked(state);
    }

    /**
     * 向地图广播Enemy、Ship。
     * @param state 状态值
     */
    public void broadcastEnemyShip(final boolean state) {
        broadcastMessage(PacketCreator.crogBoatPacket(state));
        this.setDocked(state);
    }

    /**
     * 判断是否为黑龙、Defeated。
     * @return boolean 类型结果
     */
    public boolean isHorntailDefeated() {   // all parts of dead horntail can be found here?
        for (int i = MobId.DEAD_HORNTAIL_MIN; i <= MobId.DEAD_HORNTAIL_MAX; i++) {
            if (getMonsterById(i) == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * 生成黑龙、在、Ground、Below。
     */
    public void spawnHorntailOnGroundBelow(final Point targetPoint) {   // ayy lmao
        Monster htIntro = LifeFactory.getMonster(MobId.SUMMON_HORNTAIL);
        spawnMonsterOnGroundBelow(htIntro, targetPoint);    // htintro spawn animation converting into horntail detected thanks to Arnah

        final Monster ht = LifeFactory.getMonster(MobId.HORNTAIL);
        ht.setParentMobOid(htIntro.getObjectId());
        ht.addListener(new MonsterListener() {
            /**
             * 执行 monster、Killed 操作。
             * @param aniTime aniTime
             */
            @Override
            public void monsterKilled(int aniTime) {
            }

            /**
             * 执行 monster、Damaged 操作。
             * @param from from
             * @param trueDmg trueDmg
             */
            @Override
            public void monsterDamaged(Character from, int trueDmg) {
                ht.addHp(trueDmg);
            }

            /**
             * 执行 monster、Healed 操作。
             * @param trueHeal trueHeal
             */
            @Override
            public void monsterHealed(int trueHeal) {
                ht.addHp(-trueHeal);
            }
        });
        spawnMonsterOnGroundBelow(ht, targetPoint);

        for (int mobId = MobId.HORNTAIL_HEAD_A; mobId <= MobId.HORNTAIL_TAIL; mobId++) {
            Monster m = LifeFactory.getMonster(mobId);
            m.setParentMobOid(htIntro.getObjectId());

            m.addListener(new MonsterListener() {
                /**
                 * 执行 monster、Killed 操作。
                 * @param aniTime aniTime
                 */
                @Override
                public void monsterKilled(int aniTime) {
                }

                /**
                 * 执行 monster、Damaged 操作。
                 * @param from from
                 * @param trueDmg trueDmg
                 */
                @Override
                public void monsterDamaged(Character from, int trueDmg) {
                    // 感谢 Halcyon：修复黑龙因传播伤害未登记攻击者导致不掉落的问题
                    ht.applyFakeDamage(from, trueDmg, true);
                }

                /**
                 * 执行 monster、Healed 操作。
                 * @param trueHeal trueHeal
                 */
                @Override
                public void monsterHealed(int trueHeal) {
                    ht.addHp(trueHeal);
                }
            });

            spawnMonsterOnGroundBelow(m, targetPoint);
        }
    }

    /**
     * 执行 claim、Ownership 操作。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean claimOwnership(Character chr) {
        if (mapOwner == null) {
            this.mapOwner = chr;
            chr.setOwnedMap(this);

            mapOwnerLastActivityTime = Server.getInstance().getCurrentTime();

            getChannelServer().registerOwnedMap(this);
            return true;
        } else {
            return chr == mapOwner;
        }
    }

    /**
     * 执行 unclaim、Ownership 操作。
     * @return Character 类型结果
     */
    public Character unclaimOwnership() {
        Character lastOwner = this.mapOwner;
        return unclaimOwnership(lastOwner) ? lastOwner : null;
    }

    /**
     * 执行 unclaim、Ownership 操作。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean unclaimOwnership(Character chr) {
        if (chr != null && mapOwner == chr) {
            this.mapOwner = null;
            chr.setOwnedMap(null);

            mapOwnerLastActivityTime = Long.MAX_VALUE;

            getChannelServer().unregisterOwnedMap(this);
            return true;
        } else {
            return false;
        }
    }

    private void refreshOwnership() {
        mapOwnerLastActivityTime = Server.getInstance().getCurrentTime();
    }

    /**
     * 判断是否为Ownership、Restricted。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean isOwnershipRestricted(Character chr) {
        Character owner = mapOwner;

        if (owner != null) {
            if (owner != chr && !owner.isPartyMember(chr)) {    // thanks Vcoc & BHB for suggesting the map ownership feature
                chr.showMapOwnershipInfo(owner);
                return true;
            } else {
                this.refreshOwnership();
            }
        }

        return false;
    }

    /**
     * 检查地图、归属者、Activity。
     */
    public void checkMapOwnerActivity() {
        long timeNow = Server.getInstance().getCurrentTime();
        if (timeNow - mapOwnerLastActivityTime > 60000) {
            if (unclaimOwnership() != null) {
                this.dropMessage(5, "这里现在是无主之地了。");
            }
        }
    }

    private final List<Point> takenSpawns = new LinkedList<>();
    private final List<GuardianSpawnPoint> guardianSpawns = new LinkedList<>();
    private final List<MCSkill> blueTeamBuffs = new ArrayList();
    private final List<MCSkill> redTeamBuffs = new ArrayList();
    private final List<Integer> skillIds = new ArrayList();
    private final List<Pair<Integer, Integer>> mobsToSpawn = new ArrayList();

    /**
     * 获取Blue、队伍、Buffs。
     * @return List<MCSkill> 类型结果
     */
    public List<MCSkill> getBlueTeamBuffs() {
        return blueTeamBuffs;
    }

    /**
     * 获取Red、队伍、Buffs。
     * @return List<MCSkill> 类型结果
     */
    public List<MCSkill> getRedTeamBuffs() {
        return redTeamBuffs;
    }

    /**
     * 执行 clear、Buff、List 操作。
     */
    public void clearBuffList() {
        redTeamBuffs.clear();
        blueTeamBuffs.clear();
    }

    /**
     * 获取所有玩家。
     * @return List<MapObject> 类型结果
     */
    public List<MapObject> getAllPlayer() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.PLAYER));
    }

    /**
     * 判断是否为CPQ地图。
     * @return boolean 类型结果
     */
    public boolean isCPQMap() {
        switch (this.getId()) {
            case 980000101:
            case 980000201:
            case 980000301:
            case 980000401:
            case 980000501:
            case 980000601:
            case 980031100:
            case 980032100:
            case 980033100:
                return true;
        }
        return false;
    }

    /**
     * 判断是否为C、P、Q、Map2。
     * @return boolean 类型结果
     */
    public boolean isCPQMap2() {
        switch (this.getId()) {
            case 980031100:
            case 980032100:
            case 980033100:
                return true;
        }
        return false;
    }

    /**
     * 判断是否为C、P、Q、Lobby。
     * @return boolean 类型结果
     */
    public boolean isCPQLobby() {
        switch (this.getId()) {
            case 980000100:
            case 980000200:
            case 980000300:
            case 980000400:
            case 980000500:
            case 980000600:
                return true;
        }
        return false;
    }

    /**
     * 判断是否为Blue、C、P、Q、地图。
     * @return boolean 类型结果
     */
    public boolean isBlueCPQMap() {
        switch (this.getId()) {
            case 980000501:
            case 980000601:
            case 980031200:
            case 980032200:
            case 980033200:
                return true;
        }
        return false;
    }

    /**
     * 判断是否为Purple、C、P、Q、地图。
     * @return boolean 类型结果
     */
    public boolean isPurpleCPQMap() {
        switch (this.getId()) {
            case 980000301:
            case 980000401:
            case 980031200:
            case 980032200:
            case 980033200:
                return true;
        }
        return false;
    }

    /**
     * 获取Random、S、P。
     * @param team team
     * @return Point 类型结果
     */
    public Point getRandomSP(int team) {
        if (takenSpawns.size() > 0) {
            for (SpawnPoint sp : monsterSpawn) {
                for (Point pt : takenSpawns) {
                    if ((sp.getPosition().x == pt.x && sp.getPosition().y == pt.y) || (sp.getTeam() != team && !this.isBlueCPQMap())) {
                        continue;
                    } else {
                        takenSpawns.add(pt);
                        return sp.getPosition();
                    }
                }
            }
        } else {
            for (SpawnPoint sp : monsterSpawn) {
                if (sp.getTeam() == team || this.isBlueCPQMap()) {
                    takenSpawns.add(sp.getPosition());
                    return sp.getPosition();
                }
            }
        }
        return null;
    }

    /**
     * 获取Random、守护者、刷新。
     * @param team team
     * @return GuardianSpawnPoint 类型结果
     */
    public GuardianSpawnPoint getRandomGuardianSpawn(int team) {
        boolean alltaken = true;
        for (GuardianSpawnPoint a : this.guardianSpawns) {
            if (!a.isTaken()) {
                alltaken = false;
                break;
            }
        }
        if (alltaken) {
            return null;
        }
        if (!this.guardianSpawns.isEmpty()) {
            while (true) {
                for (GuardianSpawnPoint gsp : this.guardianSpawns) {
                    if (!gsp.isTaken() && Math.random() < 0.3 && (gsp.getTeam() == -1 || gsp.getTeam() == team)) {
                        return gsp;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 添加守护者刷新坐标。
     * @param a a
     */
    public void addGuardianSpawnPoint(GuardianSpawnPoint a) {
        this.guardianSpawns.add(a);
    }

    /**
     * 生成守护者。
     * @param team team
     * @param num 数量
     * @return int 类型结果
     */
    public int spawnGuardian(int team, int num) {
        try {
            if (team == 0 && redTeamBuffs.size() >= 4 || team == 1 && blueTeamBuffs.size() >= 4) {
                return 2;
            }
            final MCSkill skill = CarnivalFactory.getInstance().getGuardian(num);
            if (team == 0 && redTeamBuffs.contains(skill)) {
                return 0;
            } else if (team == 1 && blueTeamBuffs.contains(skill)) {
                return 0;
            }
            GuardianSpawnPoint pt = this.getRandomGuardianSpawn(team);
            if (pt == null) {
                return -1;
            }
            int reactorID = 9980000 + team;
            Reactor reactor = new Reactor(ReactorFactory.getReactorS(reactorID), reactorID);
            pt.setTaken(true);
            reactor.setPosition(pt.getPosition());
            reactor.setName(team + "" + num); //lol
            reactor.resetReactorActions(0);
            this.spawnReactor(reactor);
            reactor.setGuardian(pt);
            this.buffMonsters(team, skill);
            getReactorByOid(reactor.getObjectId()).hitReactor(((Character) this.getAllPlayer().get(0)).getClient());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 执行 buff、怪物 操作。
     * @param team team
     * @param skill skill
     */
    public void buffMonsters(int team, MCSkill skill) {
        if (skill == null) {
            return;
        }

        if (team == 0) {
            redTeamBuffs.add(skill);
        } else if (team == 1) {
            blueTeamBuffs.add(skill);
        }
        for (MapObject mmo : this.mapobjects.values()) {
            if (mmo.getType() == MapObjectType.MONSTER) {
                Monster mob = (Monster) mmo;
                if (mob.getTeam() == team) {
                    skill.getSkill().applyEffect(null, mob, false, null);
                }
            }
        }
    }

    /**
     * 获取技能Ids。
     * @return List<Integer> 类型结果
     */
    public final List<Integer> getSkillIds() {
        return skillIds;
    }

    /**
     * 添加技能ID。
     * @param z z
     */
    public final void addSkillId(int z) {
        this.skillIds.add(z);
    }

    /**
     * 添加怪物刷新。
     * @param mobId mobId
     * @param spendCP spendCP
     */
    public final void addMobSpawn(int mobId, int spendCP) {
        this.mobsToSpawn.add(new Pair<>(mobId, spendCP));
    }

    /**
     * 获取Mobs、到、刷新。
     * @return List<Pair<Integer, Integer>> 类型结果
     */
    public final List<Pair<Integer, Integer>> getMobsToSpawn() {
        return mobsToSpawn;
    }

    /**
     * 判断是否为C、P、Q、Winner、地图。
     * @return boolean 类型结果
     */
    public boolean isCPQWinnerMap() {
        switch (this.getId()) {
            case 980000103:
            case 980000203:
            case 980000303:
            case 980000403:
            case 980000503:
            case 980000603:
            case 980031300:
            case 980032300:
            case 980033300:
                return true;
        }
        return false;
    }

    /**
     * 判断是否为C、P、Q、Loser、地图。
     * @return boolean 类型结果
     */
    public boolean isCPQLoserMap() {
        switch (this.getId()) {
            case 980000104:
            case 980000204:
            case 980000304:
            case 980000404:
            case 980000504:
            case 980000604:
            case 980031400:
            case 980032400:
            case 980033400:
                return true;
        }
        return false;
    }

    /**
     * 执行 run、角色、Stat、更新 操作。
     */
    public void runCharacterStatUpdate() {
        if (!statUpdateRunnables.isEmpty()) {
            List<Runnable> toRun = new ArrayList<>(statUpdateRunnables);
            statUpdateRunnables.clear();

            for (Runnable r : toRun) {
                r.run();
            }
        }
    }

    /**
     * 注册角色、Stat、更新。
     * @param r Runnable 任务
     */
    public void registerCharacterStatUpdate(Runnable r) {
        statUpdateRunnables.add(r);
    }

    /**
     * 执行 dispose 操作。
     */
    public void dispose() {
        for (Monster mm : this.getAllMonsters()) {
            mm.dispose();
        }

        clearMapObjects();

        event = null;
        footholds = null;
        portals.clear();
        mapEffect = null;

        chrWLock.lock();
        try {
            aggroMonitor.dispose();
            aggroMonitor = null;

            if (itemMonitor != null) {
                itemMonitor.cancel(false);
                itemMonitor = null;
            }

            if (expireItemsTask != null) {
                expireItemsTask.cancel(false);
                expireItemsTask = null;
            }

            if (mobSpawnLootTask != null) {
                mobSpawnLootTask.cancel(false);
                mobSpawnLootTask = null;
            }

            if (characterStatUpdateTask != null) {
                characterStatUpdateTask.cancel(false);
                characterStatUpdateTask = null;
            }
        } finally {
            chrWLock.unlock();
        }
    }

    /**
     * 获取Max、Mobs。
     * @return int 类型结果
     */
    public int getMaxMobs() {
        return maxMobs;
    }

    /**
     * 设置Max、Mobs。
     * @param maxMobs maxMobs
     */
    public void setMaxMobs(int maxMobs) {
        this.maxMobs = maxMobs;
    }

    /**
     * 获取Max反应堆。
     * @return int 类型结果
     */
    public int getMaxReactors() {
        return maxReactors;
    }

    /**
     * 设置Max反应堆。
     * @param maxReactors maxReactors
     */
    public void setMaxReactors(int maxReactors) {
        this.maxReactors = maxReactors;
    }

    /**
     * 获取Death、C、P。
     * @return int 类型结果
     */
    public int getDeathCP() {
        return deathCP;
    }

    /**
     * 设置Death、C、P。
     * @param deathCP deathCP
     */
    public void setDeathCP(int deathCP) {
        this.deathCP = deathCP;
    }

    /**
     * 获取时间、Default。
     * @return int 类型结果
     */
    public int getTimeDefault() {
        return timeDefault;
    }

    /**
     * 设置时间、Default。
     * @param timeDefault timeDefault
     */
    public void setTimeDefault(int timeDefault) {
        this.timeDefault = timeDefault;
    }

    /**
     * 获取时间、Expand。
     * @return int 类型结果
     */
    public int getTimeExpand() {
        return timeExpand;
    }

    /**
     * 设置时间、Expand。
     * @param timeExpand timeExpand
     */
    public void setTimeExpand(int timeExpand) {
        this.timeExpand = timeExpand;
    }

}
