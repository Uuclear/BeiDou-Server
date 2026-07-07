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
package org.gms.scripting.event;

import org.gms.client.Character;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.config.GameConfig;
import org.gms.constants.inventory.ItemConstants;
import org.gms.net.server.coordinator.world.EventRecallCoordinator;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.util.NumberTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractPlayerInteraction;
import org.gms.scripting.event.scheduler.EventScriptScheduler;
import org.gms.server.ItemInformationProvider;
import org.gms.server.StatEffect;
import org.gms.server.ThreadManager;
import org.gms.server.TimerManager;
import org.gms.server.expeditions.Expedition;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.Monster;
import org.gms.server.life.NPC;
import org.gms.server.maps.MapManager;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.Portal;
import org.gms.server.maps.Reactor;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import javax.script.ScriptException;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 事件实例管理器类，负责管理游戏内事件实例的创建、运行和销毁。
 * 处理玩家加入/退出、怪物生成/击杀、奖励发放、地图切换等事件相关逻辑。
 * 提供线程安全的事件状态管理和脚本调度功能。
 */
public class EventInstanceManager {
    private static final Logger log = LoggerFactory.getLogger(EventInstanceManager.class);
    private final Map<Integer, Character> chars = new HashMap<>();// 存储参与事件的玩家，key为玩家ID
    private int leaderId = -1; // 事件队伍领袖ID
    private final List<Monster> mobs = new LinkedList<>();// 事件中生成的怪物列表
    private final Map<Character, Integer> killCount = new HashMap<>();// 玩家击杀计数
    private EventManager em; // 所属事件管理器
    private EventScriptScheduler ess; // 事件脚本调度器
    private MapManager mapManager; // 地图管理器
    private String name; // 事件实例名称

    //伤害统计排名
    private volatile boolean recordDamage = false;   // 伤害统计记录开关，由脚本调用 startDamageRecording() 开启
    private static final long MAX_DAMAGE_THRESHOLD = Long.MAX_VALUE - 1_000_000_000L;
    private final Map<Integer, Long> playerDamage = new ConcurrentHashMap<>();// 玩家伤害量
    private final Map<Integer, String> playerNames = new ConcurrentHashMap<>(); //玩家角色名

    // 事件属性存储
    private final Properties props = new Properties();
    private final Map<String, Object> objectProps = new HashMap<>();

    // 事件计时相关
    private long timeStarted = 0; // 事件开始时间
    private long eventTime = 0; // 事件总时长

    // 远征队相关
    private Expedition expedition = null;

    // 事件使用的地图ID列表
    private final List<Integer> mapIds = new LinkedList<>();

    // 读写锁控制
    private final Lock readLock;
    private final Lock writeLock;

    private final Lock propertyLock = new ReentrantLock(true);// 属性操作锁
    private final Lock scriptLock = new ReentrantLock(true);// 脚本操作锁

    private ScheduledFuture<?> event_schedule = null;// 事件调度任务

    // 状态标志
    private boolean disposed = false; // 是否已销毁
    private boolean eventCleared = false; // 事件是否完成
    private boolean eventStarted = false; // 事件是否开始

    // 奖励相关配置
    private final Map<Integer, List<Integer>> collectionSet = new HashMap<>(GameConfig.getServerInt("max_event_levels"));
    private final Map<Integer, List<Integer>> collectionQty = new HashMap<>(GameConfig.getServerInt("max_event_levels"));
    private final Map<Integer, Integer> collectionExp = new HashMap<>(GameConfig.getServerInt("max_event_levels"));

    // 清理阶段奖励
    private final List<Integer> onMapClearExp = new ArrayList<>();
    private final List<Integer> onMapClearMeso = new ArrayList<>();

    // 玩家状态网格
    private final Map<Integer, Integer> playerGrid = new HashMap<>();

    // 已开启的门记录
    private final Map<Integer, Pair<String, Integer>> openedGates = new HashMap<>();

    // 事件专属物品
    private final Set<Integer> exclusiveItems = new HashSet<>();

    /**
     * 构造函数，初始化事件实例
     * @param em 事件管理器
     * @param name 事件实例名称
     */
    public EventInstanceManager(EventManager em, String name) {
        this.em = em;
        this.name = name;
        this.ess = new EventScriptScheduler();
        this.mapManager = new MapManager(this, em.getWorldServer().getId(), em.getChannelServer().getId());

        // 初始化读写锁
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
    }

/** 设置事件实例名称 */
    public void setName(String name) {
        this.name = name;
    }

/** 获取所属事件管理器 */
    public EventManager getEm() {
        scriptLock.lock();
        try {
            return em;
        } finally {
            scriptLock.unlock();
        }
    }

/** 获取事件玩家职业位掩码 */
    public int getEventPlayersJobs() {
        //Bits -> 0: BEGINNER 1: WARRIOR 2: MAGICIAN
        //        3: BOWMAN 4: THIEF 5: PIRATE

        int mask = 0;
        for (Character chr : getPlayers()) {
            mask |= (1 << chr.getJob().getJobNiche());
        }

        return mask;
    }

/** 对事件内所有玩家应用物品 Buff */
    public void applyEventPlayersItemBuff(int itemId) {
        List<Character> players = getPlayerList();
        StatEffect mse = ItemInformationProvider.getInstance().getItemEffect(itemId);

        if (mse != null) {
            for (Character player : players) {
                mse.applyTo(player);
            }
        }
    }

/** 对事件内所有玩家应用技能 Buff */
    public void applyEventPlayersSkillBuff(int skillId) {
        applyEventPlayersSkillBuff(skillId, Integer.MAX_VALUE);
    }

/** 对事件内所有玩家应用技能 Buff */
    public void applyEventPlayersSkillBuff(int skillId, int skillLv) {
        List<Character> players = getPlayerList();
        Skill skill = SkillFactory.getSkill(skillId);

        if (skill != null) {
            StatEffect mse = skill.getEffect(Math.min(skillLv, skill.getMaxLevel()));
            if (mse != null) {
                for (Character player : players) {
                    mse.applyTo(player);
                }
            }
        }
    }

/** 向事件内玩家发放经验 */
    public void giveEventPlayersExp(int gain) {
        giveEventPlayersExp(gain, -1);
    }

/** 向事件内玩家发放经验 */
    public void giveEventPlayersExp(int gain, int mapId) {
        if (gain == 0) {
            return;
        }

        List<Character> players = getPlayerList();

        if (mapId == -1) {
            for (Character mc : players) {
                mc.gainExp(NumberTool.floatToInt(gain * mc.getExpRate()), true, true);
            }
        } else {
            for (Character mc : players) {
                if (mc.getMapId() == mapId) {
                    mc.gainExp(NumberTool.floatToInt(gain * mc.getExpRate()), true, true);
                }
            }
        }
    }

/** 向事件内玩家发放金币 */
    public void giveEventPlayersMeso(int gain) {
        giveEventPlayersMeso(gain, -1);
    }

/** 向事件内玩家发放金币 */
    public void giveEventPlayersMeso(int gain, int mapId) {
        if (gain == 0) {
            return;
        }

        List<Character> players = getPlayerList();

        if (mapId == -1) {
            for (Character mc : players) {
                mc.gainMeso(NumberTool.floatToInt(gain * mc.getMesoRate()));
            }
        } else {
            for (Character mc : players) {
                if (mc.getMapId() == mapId) {
                    mc.gainMeso(NumberTool.floatToInt(gain * mc.getMesoRate()));
                }
            }
        }

    }

    public Object invokeScriptFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        if (!disposed) {
            return em.getIv().invokeFunction(name, args);
        } else {
            return null;
        }
    }

/** 注册玩家到事件实例 */
    public synchronized void registerPlayer(final Character chr) {
        registerPlayer(chr, true);
    }

    /**
     * 注册玩家到事件实例
     * @param chr 要注册的玩家角色
     * @param runEntryScript 是否执行入口脚本
     */
    public synchronized void registerPlayer(final Character chr, boolean runEntryScript) {
        if (chr == null || !chr.isLoggedInWorld() || disposed) {
            return;
        }

        writeLock.lock(); // 获取写锁
        try {
            if (chars.containsKey(chr.getId())) {
                return; // 已注册则返回
            }

            chars.put(chr.getId(), chr); // 添加玩家到集合
            chr.setEventInstance(this); // 设置玩家事件实例
        } finally {
            writeLock.unlock(); // 释放写锁
        }

        if (runEntryScript) {
            try {
                invokeScriptFunction("playerEntry", EventInstanceManager.this, chr);// 调用玩家进入脚本函数
            } catch (ScriptException | NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }
    }

/** 玩家退出事件并触发脚本回调 */
    public void exitPlayer(final Character chr) {
        if (chr == null || !chr.isLoggedIn()) {
            return;
        }

        unregisterPlayer(chr);

        try {
            invokeScriptFunction("playerExit", EventInstanceManager.this, chr);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

/** 向事件内所有玩家发送消息 */
    public void dropMessage(int type, String message) {
        for (Character chr : getPlayers()) {
            chr.dropMessage(type, message);
        }
    }

/** 重启事件倒计时 */
    public void restartEventTimer(long time) {
        stopEventTimer();
        startEventTimer(time);
    }

/** 启动事件倒计时并同步客户端时钟 */
    public void startEventTimer(long time) {
        timeStarted = System.currentTimeMillis();
        eventTime = time;

        for (Character chr : getPlayers()) {
            chr.sendPacket(PacketCreator.getClock((int) (time / 1000)));
        }

        event_schedule = TimerManager.getInstance().schedule(() -> {
            dismissEventTimer();

            try {
                invokeScriptFunction("scheduledTimeout", EventInstanceManager.this);
            } catch (ScriptException | NoSuchMethodException ex) {
                log.error("事件脚本 {} 没有封装scheduledTimeout函数", em.getName(), ex);
            }
        }, time);
    }

/** 延长事件剩余时间 */
    public void addEventTimer(long time) {
        if (event_schedule != null) {
            if (event_schedule.cancel(false)) {
                long nextTime = getTimeLeft() + time;
                eventTime += time;

                event_schedule = TimerManager.getInstance().schedule(() -> {
                    dismissEventTimer();

                    try {
                        invokeScriptFunction("scheduledTimeout", EventInstanceManager.this);
                    } catch (ScriptException | NoSuchMethodException ex) {
                        log.error("事件脚本 {} 没有封装scheduledTimeout函数", em.getName(), ex);
                    }
                }, nextTime);
            }
        } else {
            startEventTimer(time);
        }
    }

    private void dismissEventTimer() {
        for (Character chr : getPlayers()) {
            chr.sendPacket(PacketCreator.removeClock());
        }

        event_schedule = null;
        eventTime = 0;
        timeStarted = 0;
    }

/** 停止事件倒计时 */
    public void stopEventTimer() {
        if (event_schedule != null) {
            event_schedule.cancel(false);
            event_schedule = null;
        }

        dismissEventTimer();
    }

/** 事件倒计时是否已启动 */
    public boolean isTimerStarted() {
        return eventTime > 0 && timeStarted > 0;
    }

/** 获取事件剩余时间（毫秒） */
    public long getTimeLeft() {
        return eventTime - (System.currentTimeMillis() - timeStarted);
    }

/** 注册队伍成员到事件 */
    public void registerParty(Character chr) {
        if (chr.isPartyLeader()) {
            registerParty(chr.getParty(), chr.getMap());
        }
    }

/** 注册队伍成员到事件 */
    public void registerParty(Party party, MapleMap map) {
        for (PartyCharacter mpc : party.getEligibleMembers()) {
            if (mpc.isOnline()) {   // thanks resinate
                Character chr = map.getCharacterById(mpc.getId());
                if (chr != null) {
                    registerPlayer(chr);
                }
            }
        }
    }

/** 注册远征队到事件 */
    public void registerExpedition(Expedition exped) {
        expedition = exped;
        registerExpeditionTeam(exped, exped.getRecruitingMap().getId());
    }

    private void registerExpeditionTeam(Expedition exped, int recruitMap) {
        expedition = exped;

        for (Character chr : exped.getActiveMembers()) {
            if (chr.getMapId() == recruitMap) {
                registerPlayer(chr);
            }
        }
    }

/** 从事件注销玩家 */
    public void unregisterPlayer(final Character chr) {
        try {
            invokeScriptFunction("playerUnregistered", EventInstanceManager.this, chr);
        } catch (ScriptException | NoSuchMethodException ex) {
            log.error("事件脚本 {} 没有封装playerUnregistered函数", em.getName(), ex);
        }

        writeLock.lock();
        try {
            chars.remove(chr.getId());
            chr.setEventInstance(null);
        } finally {
            writeLock.unlock();
        }

        gridRemove(chr);
        dropExclusiveItems(chr);
    }

/** 获取事件内玩家数量 */
    public int getPlayerCount() {
        readLock.lock();
        try {
            return chars.size();
        } finally {
            readLock.unlock();
        }
    }

/** 按角色 ID 获取事件内玩家 */
    public Character getPlayerById(int id) {
        readLock.lock();
        try {
            return chars.get(id);
        } finally {
            readLock.unlock();
        }
    }

/** 获取事件内所有玩家副本列表 */
    public List<Character> getPlayers() {
        readLock.lock();
        try {
            return new ArrayList<>(chars.values());
        } finally {
            readLock.unlock();
        }
    }

    private List<Character> getPlayerList() {
        readLock.lock();
        try {
            return new LinkedList<>(chars.values());
        } finally {
            readLock.unlock();
        }
    }

/** 注册事件生成的怪物 */
    public void registerMonster(Monster mob) {
        if (!mob.getStats().isFriendly()) { //We cannot register moon bunny
            mobs.add(mob);
        }
    }

/** 触发玩家移动脚本回调 */
    public void movePlayer(final Character chr) {
        try {
            invokeScriptFunction("moveMap", EventInstanceManager.this, chr);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

/** 玩家切换地图时触发脚本回调 */
    public void changedMap(final Character chr, final int mapId) {
        try {
            invokeScriptFunction("changedMap", EventInstanceManager.this, chr, mapId);
        } catch (ScriptException | NoSuchMethodException ex) {
        } // optional
    }

/** 玩家切换地图完成后触发脚本回调 */
    public void afterChangedMap(final Character chr, final int mapId) {
        try {
            invokeScriptFunction("afterChangedMap", EventInstanceManager.this, chr, mapId);
        } catch (ScriptException | NoSuchMethodException ex) {
        } // optional
    }

/** 队伍队长变更时触发脚本回调 */
    public synchronized void changedLeader(final PartyCharacter ldr) {
        try {
            invokeScriptFunction("changedLeader", EventInstanceManager.this, ldr);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        leaderId = ldr.getId();
    }

/** 怪物被击杀时触发脚本回调 */
    public void monsterKilled(final Monster mob, final boolean hasKiller) {
        int scriptResult = 0;

        scriptLock.lock();
        try {
            mobs.remove(mob);

            if (eventStarted) {
                scriptResult = 1;

                if (mobs.isEmpty()) {
                    scriptResult = 2;
                }
            }
        } finally {
            scriptLock.unlock();
        }

        if (scriptResult > 0) {
            try {
                invokeScriptFunction("monsterKilled", mob, EventInstanceManager.this, hasKiller);
            } catch (ScriptException | NoSuchMethodException ex) {
                ex.printStackTrace();
            }

            if (scriptResult > 1) {
                try {
                    invokeScriptFunction("allMonstersDead", EventInstanceManager.this, hasKiller);
                } catch (ScriptException | NoSuchMethodException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

/** 友方怪物被击杀时触发脚本回调 */
    public void friendlyKilled(final Monster mob, final boolean hasKiller) {
        try {
            invokeScriptFunction("friendlyKilled", mob, EventInstanceManager.this, hasKiller);
        } catch (ScriptException | NoSuchMethodException ex) {
        } //optional
    }

/** 友方怪物受伤时触发脚本回调 */
    public void friendlyDamaged(final Monster mob) {
        try {
            invokeScriptFunction("friendlyDamaged", EventInstanceManager.this, mob);
        } catch (ScriptException | NoSuchMethodException ex) {
        } // optional
    }

/** 友方怪物掉落物品时触发脚本回调 */
    public void friendlyItemDrop(final Monster mob) {
        try {
            invokeScriptFunction("friendlyItemDrop", EventInstanceManager.this, mob);
        } catch (ScriptException | NoSuchMethodException ex) {
        } // optional
    }

/** 玩家死亡时触发脚本回调 */
    public void playerKilled(final Character chr) {
        ThreadManager.getInstance().newTask(() -> {
            try {
                invokeScriptFunction("playerDead", EventInstanceManager.this, chr);
            } catch (ScriptException | NoSuchMethodException ex) {
            } // optional
        });
    }

/** 怪物复活时触发脚本回调 */
    public void reviveMonster(final Monster mob) {
        try {
            invokeScriptFunction("monsterRevive", EventInstanceManager.this, mob);
        } catch (ScriptException | NoSuchMethodException ex) {
        } // optional
    }

/** 玩家复活时触发脚本回调，返回是否允许复活 */
    public boolean revivePlayer(final Character chr) {
        try {
            Object b = invokeScriptFunction("playerRevive", EventInstanceManager.this, chr);
            if (b instanceof Boolean) {
                return (Boolean) b;
            }
        } catch (ScriptException | NoSuchMethodException ex) {
        } // optional

        return true;
    }

/** 玩家断线时触发脚本回调 */
    public void playerDisconnected(final Character chr) {
        try {
            invokeScriptFunction("playerDisconnected", EventInstanceManager.this, chr);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        EventRecallCoordinator.getInstance().storeEventInstance(chr.getId(), this);
    }

/** 怪物被击杀时触发脚本回调 */
    public void monsterKilled(Character chr, final Monster mob) {
        try {
            final int inc = (int) invokeScriptFunction("monsterValue", EventInstanceManager.this, mob.getId());

            if (inc != 0) {
                Integer kc = killCount.get(chr);
                if (kc == null) {
                    kc = inc;
                } else {
                    kc += inc;
                }
                killCount.put(chr, kc);
                if (expedition != null) {
                    expedition.monsterKilled(chr, mob);
                }
            }
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

/** 获取玩家在事件中的击杀计数 */
    public int getKillCount(Character chr) {
        Integer kc = killCount.get(chr);
        return (kc == null) ? 0 : kc;
    }

/** 销毁事件实例并清理资源 */
    public void dispose() {
        readLock.lock();
        try {
            for (Character chr : chars.values()) {
                chr.setEventInstance(null);
            }
        } finally {
            readLock.unlock();
        }

        dispose(false);
    }

    public synchronized void dispose(boolean shutdown) {    // should not trigger any event script method after disposed
        if (disposed) {
            return;
        }

        try {
            invokeScriptFunction("dispose", EventInstanceManager.this);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        disposed = true;

        ess.dispose();

        writeLock.lock();
        try {
            for (Character chr : chars.values()) {
                chr.setEventInstance(null);
            }
            chars.clear();
            mobs.clear();
            ess = null;
        } finally {
            writeLock.unlock();
        }

        if (event_schedule != null) {
            event_schedule.cancel(false);
            event_schedule = null;
        }

        killCount.clear();
        mapIds.clear();
        props.clear();
        objectProps.clear();

        // 清理伤害统计数据
        clearDamage();

        disposeExpedition();

        scriptLock.lock();
        try {
            if (!eventCleared) {
                em.disposeInstance(name);
            }
        } finally {
            scriptLock.unlock();
        }

        TimerManager.getInstance().schedule(() -> {
            mapManager.dispose();   // issues from instantly disposing some event objects found thanks to MedicOP
            writeLock.lock();
            try {
                mapManager = null;
                em = null;
            } finally {
                writeLock.unlock();
            }
        }, MINUTES.toMillis(1));
    }

/** 获取事件专属地图工厂 */
    public MapManager getMapFactory() {
        return mapManager;
    }

/** 在事件实例内延迟调度脚本函数 */
    public void schedule(final String methodName, long delay) {
        readLock.lock();
        try {
            if (ess != null) {
                Runnable r = () -> {
                    try {
                        invokeScriptFunction(methodName, EventInstanceManager.this);
                    } catch (ScriptException | NoSuchMethodException ex) {
                        ex.printStackTrace();
                    }
                };

                ess.registerEntry(r, delay);
            }
        } finally {
            readLock.unlock();
        }
    }

/** 获取事件实例名称 */
    public String getName() {
        return name;
    }

/** 获取事件地图实例（首次加载可洗牌反应堆） */
    public MapleMap getMapInstance(int mapId) {
        MapleMap map = mapManager.getMap(mapId);
        map.setEventInstance(this);

        if (!mapManager.isMapLoaded(mapId)) {
            scriptLock.lock();
            try {
                if (em.getProperty("shuffleReactors") != null && em.getProperty("shuffleReactors").equals("true")) {
                    map.shuffleReactors();
                }
            } finally {
                scriptLock.unlock();
            }
        }
        return map;
    }

/** 设置整数类型事件属性 */
    public void setIntProperty(String key, Integer value) {
        setProperty(key, value);
    }

/** 设置字符串类型事件属性 */
    public void setProperty(String key, Integer value) {
        setProperty(key, "" + value);
    }

/** 设置字符串类型事件属性 */
    public void setProperty(String key, String value) {
        propertyLock.lock();
        try {
            props.setProperty(key, value);
        } finally {
            propertyLock.unlock();
        }
    }

/** 设置字符串类型事件属性 */
    public Object setProperty(String key, String value, boolean prev) {
        propertyLock.lock();
        try {
            return props.setProperty(key, value);
        } finally {
            propertyLock.unlock();
        }
    }

/** 设置对象类型事件属性 */
    public void setObjectProperty(String key, Object obj) {
        propertyLock.lock();
        try {
            objectProps.put(key, obj);
        } finally {
            propertyLock.unlock();
        }
    }

/** 获取字符串类型事件属性 */
    public String getProperty(String key) {
        propertyLock.lock();
        try {
            return props.getProperty(key);
        } finally {
            propertyLock.unlock();
        }
    }

/** 获取整数类型事件属性 */
    public int getIntProperty(String key) {
        propertyLock.lock();
        try {
            return Integer.parseInt(props.getProperty(key) != null ? props.getProperty(key) : String.valueOf(0));
        } finally {
            propertyLock.unlock();
        }
    }

/** 获取对象类型事件属性 */
    public Object getObjectProperty(String key) {
        propertyLock.lock();
        try {
            return objectProps.get(key);
        } finally {
            propertyLock.unlock();
        }
    }

/** 玩家离开队伍时触发脚本回调 */
    public void leftParty(final Character chr) {
        try {
            invokeScriptFunction("leftParty", EventInstanceManager.this, chr);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

/** 队伍解散时触发脚本回调 */
    public void disbandParty() {
        try {
            invokeScriptFunction("disbandParty", EventInstanceManager.this);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

/** 清除组队任务状态 */
    public void clearPQ() {
        try {
            invokeScriptFunction("clearPQ", EventInstanceManager.this);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

/** 移除玩家并触发退出脚本 */
    public void removePlayer(final Character chr) {
        try {
            invokeScriptFunction("playerExit", EventInstanceManager.this, chr);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

/** 判断玩家是否为队伍队长 */
    public boolean isLeader(Character chr) {
        return (chr.getParty().getLeaderId() == chr.getId());
    }

/** 判断玩家是否为事件队长 */
    public boolean isEventLeader(Character chr) {
        return (chr.getId() == getLeaderId());
    }

/** 获取并记录事件使用的地图实例 */
    public final MapleMap getInstanceMap(final int mapid) {
        if (disposed) {
            return null;
        }
        mapIds.add(mapid);
        return getMapFactory().getMap(mapid);
    }

/** 玩家不足时传送并销毁事件 */
    public final boolean disposeIfPlayerBelow(final byte size, final int towarp) {
        if (disposed) {
            return true;
        }
        if (chars == null) {
            return false;
        }

        MapleMap map = null;
        if (towarp > 0) {
            map = this.getMapFactory().getMap(towarp);
        }

        List<Character> players = getPlayerList();

        try {
            if (players.size() < size) {
                for (Character chr : players) {
                    if (chr == null) {
                        continue;
                    }

                    unregisterPlayer(chr);
                    if (towarp > 0) {
                        chr.changeMap(map, map.getPortal(0));
                    }
                }

                dispose();
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

/** 在事件地图生成 NPC */
    public void spawnNpc(int npcId, Point pos, MapleMap map) {
        NPC npc = LifeFactory.getNPC(npcId);
        if (npc != null) {
            npc.setPosition(pos);
            npc.setCy(pos.y);
            npc.setRx0(pos.x + 50);
            npc.setRx1(pos.x - 50);
            npc.setFh(map.getFootholds().findBelow(pos).getId());
            map.addMapObject(npc);
            map.broadcastMessage(PacketCreator.spawnNPC(npc));
        }
    }

/** 为事件成员更新任务杀怪计数 */
    public void dispatchRaiseQuestMobCount(int mobid, int mapid) {
        Map<Integer, Character> mapChars = getInstanceMap(mapid).getMapPlayers();
        if (!mapChars.isEmpty()) {
            List<Character> eventMembers = getPlayers();

            for (Character evChr : eventMembers) {
                Character chr = mapChars.get(evChr.getId());

                if (chr != null && chr.isLoggedInWorld()) {
                    chr.raiseQuestMobCount(mobid);
                }
            }
        }
    }

/** 获取怪物模板 */
    public Monster getMonster(int mid) {
        return (LifeFactory.getMonster(mid));
    }

    private List<Integer> convertToIntegerList(List<Object> objects) {
        List<Integer> intList = new ArrayList<>();

        for (Object object : objects) {
            intList.add((Integer) object);
        }

        return intList;
    }

/** 设置各阶段通关经验奖励 */
    public void setEventClearStageExp(List<Object> gain) {
        onMapClearExp.clear();
        onMapClearExp.addAll(convertToIntegerList(gain));
    }

/** 设置各阶段通关金币奖励 */
    public void setEventClearStageMeso(List<Object> gain) {
        onMapClearMeso.clear();
        onMapClearMeso.addAll(convertToIntegerList(gain));
    }

    public Integer getClearStageExp(int stage) {    //stage counts from ONE.
        if (stage > onMapClearExp.size()) {
            return 0;
        }
        return onMapClearExp.get(stage - 1);
    }

    public Integer getClearStageMeso(int stage) {   //stage counts from ONE.
        if (stage > onMapClearMeso.size()) {
            return 0;
        }
        return onMapClearMeso.get(stage - 1);
    }

/** 获取指定阶段经验与金币奖励列表 */
    public List<Integer> getClearStageBonus(int stage) {
        List<Integer> list = new ArrayList<>();
        list.add(getClearStageExp(stage));
        list.add(getClearStageMeso(stage));

        return list;
    }

    private void dropExclusiveItems(Character chr) {
        AbstractPlayerInteraction api = chr.getAbstractPlayerInteraction();

        for (Integer item : exclusiveItems) {
            api.removeAll(item);
        }
    }

/** 移除所有玩家的事件专属物品 */
    public void dropAllExclusiveItems() {
        getPlayers().forEach(this::dropExclusiveItems);
    }

/** 设置事件专属物品列表 */
    public final void setExclusiveItems(List<Object> items) {
        List<Integer> exclusive = convertToIntegerList(items);

        writeLock.lock();
        try {
            exclusiveItems.addAll(exclusive);
        } finally {
            writeLock.unlock();
        }
    }

/** 配置事件随机奖励池 */
    public final void setEventRewards(List<Object> rwds, List<Object> qtys, int expGiven) {
        setEventRewards(1, rwds, qtys, expGiven);
    }

/** 配置事件随机奖励池 */
    public final void setEventRewards(List<Object> rwds, List<Object> qtys) {
        setEventRewards(1, rwds, qtys);
    }

/** 配置事件随机奖励池 */
    public final void setEventRewards(int eventLevel, List<Object> rwds, List<Object> qtys) {
        setEventRewards(eventLevel, rwds, qtys, 0);
    }

/** 配置事件随机奖励池 */
    public final void setEventRewards(int eventLevel, List<Object> rwds, List<Object> qtys, int expGiven) {
        // fixed EXP will be rewarded at the same time the random item is given

        if (eventLevel <= 0 || eventLevel > GameConfig.getServerInt("max_event_levels")) {
            return;
        }
        eventLevel--;    //event level starts from 1

        List<Integer> rewardIds = convertToIntegerList(rwds);
        List<Integer> rewardQtys = convertToIntegerList(qtys);

        //rewardsSet and rewardsQty hold temporary values
        writeLock.lock();
        try {
            collectionSet.put(eventLevel, rewardIds);
            collectionQty.put(eventLevel, rewardQtys);
            collectionExp.put(eventLevel, expGiven);
        } finally {
            writeLock.unlock();
        }
    }

    private byte getRewardListRequirements(int level) {
        if (level >= collectionSet.size()) {
            return 0;
        }

        byte rewardTypes = 0;
        List<Integer> list = collectionSet.get(level);

        for (Integer itemId : list) {
            rewardTypes |= (1 << ItemConstants.getInventoryType(itemId).getType());
        }

        return rewardTypes;
    }

    private boolean hasRewardSlot(Character player, int eventLevel) {
        byte listReq = getRewardListRequirements(eventLevel);   //gets all types of items present in the event reward list

        //iterating over all valid inventory types
        for (byte type = 1; type <= 5; type++) {
            if ((listReq >> type) % 2 == 1 && !player.hasEmptySlot(type)) {
                return false;
            }
        }

        return true;
    }

/** 向玩家发放随机事件奖励 */
    public final boolean giveEventReward(Character player) {
        return giveEventReward(player, 1);
    }

    //gives out EXP & a random item in a similar fashion of when clearing KPQ, LPQ, etc.
/** 向玩家发放随机事件奖励 */
    public final boolean giveEventReward(Character player, int eventLevel) {
        List<Integer> rewardsSet, rewardsQty;
        Integer rewardExp;

        readLock.lock();
        try {
            eventLevel--;       //event level starts counting from 1
            if (eventLevel >= collectionSet.size()) {
                return true;
            }

            rewardsSet = collectionSet.get(eventLevel);
            rewardsQty = collectionQty.get(eventLevel);

            rewardExp = collectionExp.get(eventLevel);
        } finally {
            readLock.unlock();
        }

        if (rewardExp == null) {
            rewardExp = 0;
        }

        if (rewardsSet == null || rewardsSet.isEmpty()) {
            if (rewardExp > 0) {
                player.gainExp(rewardExp);
            }
            return true;
        }

        if (!hasRewardSlot(player, eventLevel)) {
            return false;
        }

        AbstractPlayerInteraction api = player.getAbstractPlayerInteraction();
        int rnd = (int) Math.floor(Math.random() * rewardsSet.size());

        api.gainItem(rewardsSet.get(rnd), rewardsQty.get(rnd).shortValue());
        if (rewardExp > 0) {
            player.gainExp(rewardExp);
        }
        return true;
    }

    private void disposeExpedition() {
        if (expedition != null) {
            expedition.dispose(eventCleared);

            scriptLock.lock();
            try {
                expedition.removeChannelExpedition(em.getChannelServer());
            } finally {
                scriptLock.unlock();
            }

            expedition = null;
        }
    }

/** 正式启动事件（调用 afterSetup 脚本） */
    public final synchronized void startEvent() {
        eventStarted = true;

        try {
            invokeScriptFunction("afterSetup", EventInstanceManager.this);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

/** 标记事件已通关并发放任务点 */
    public final void setEventCleared() {
        eventCleared = true;

        for (Character chr : getPlayers()) {
            chr.awardQuestPoint(GameConfig.getServerInt("quest_point_per_event_clear"));
        }

        scriptLock.lock();
        try {
            em.disposeInstance(name);
        } finally {
            scriptLock.unlock();
        }

        disposeExpedition();
    }

/** 事件是否已通关 */
    public final boolean isEventCleared() {
        return eventCleared;
    }

/** 事件实例是否已销毁 */
    public final boolean isEventDisposed() {
        return disposed;
    }

    private boolean isEventTeamLeaderOn() {
        for (Character chr : getPlayers()) {
            if (chr.getId() == getLeaderId()) {
                return true;
            }
        }

        return false;
    }

/** 检查事件队伍人数是否不足 */
    public final boolean checkEventTeamLacking(boolean leavingEventMap, int minPlayers) {
        if (eventCleared && getPlayerCount() > 1) {
            return false;
        }

        if (!eventCleared && leavingEventMap && !isEventTeamLeaderOn()) {
            return true;
        }
        return getPlayerCount() < minPlayers;
    }

/** 检查远征队人数是否不足（即时） */
    public final boolean isExpeditionTeamLackingNow(boolean leavingEventMap, int minPlayers, Character quitter) {
        if (eventCleared) {
            return leavingEventMap && getPlayerCount() <= 1;
        } else {
            // thanks Conrad for noticing expeditions don't need to have neither the leader nor meet the minimum requirement inside the event
            return getPlayerCount() <= 1;
        }
    }

/** 检查事件队伍人数是否不足（即时） */
    public final boolean isEventTeamLackingNow(boolean leavingEventMap, int minPlayers, Character quitter) {
        if (eventCleared) {
            return leavingEventMap && getPlayerCount() <= 1;
        } else {
            if (leavingEventMap && getLeaderId() == quitter.getId()) {
                return true;
            }
            return getPlayerCount() <= minPlayers;
        }
    }

/** 检查事件队伍是否在同一地图 */
    public final boolean isEventTeamTogether() {
        readLock.lock();
        try {
            if (chars.size() <= 1) {
                return true;
            }

            Iterator<Character> iterator = chars.values().iterator();
            Character mc = iterator.next();
            int mapId = mc.getMapId();

            for (; iterator.hasNext(); ) {
                mc = iterator.next();
                if (mc.getMapId() != mapId) {
                    return false;
                }
            }

            return true;
        } finally {
            readLock.unlock();
        }
    }

/** 将事件队伍成员传送到指定地图 */
    public final void warpEventTeam(int warpFrom, int warpTo) {
        List<Character> players = getPlayerList();

        for (Character chr : players) {
            if (chr.getMapId() == warpFrom) {
                chr.changeMap(warpTo);
            }
        }
    }

/** 将事件队伍成员传送到指定地图 */
    public final void warpEventTeam(int warpTo) {
        List<Character> players = getPlayerList();

        for (Character chr : players) {
            chr.changeMap(warpTo);
        }
    }

/** 将事件队伍传送到地图出生点 */
    public final void warpEventTeamToMapSpawnPoint(int warpFrom, int warpTo, int toSp) {
        List<Character> players = getPlayerList();

        for (Character chr : players) {
            if (chr.getMapId() == warpFrom) {
                chr.changeMap(warpTo, toSp);
            }
        }
    }

/** 将事件队伍传送到地图出生点 */
    public final void warpEventTeamToMapSpawnPoint(int warpTo, int toSp) {
        List<Character> players = getPlayerList();

        for (Character chr : players) {
            chr.changeMap(warpTo, toSp);
        }
    }

/** 获取事件队长角色 ID */
    public final int getLeaderId() {
        readLock.lock();
        try {
            return leaderId;
        } finally {
            readLock.unlock();
        }
    }

/** 获取事件队长角色对象 */
    public Character getLeader() {
        readLock.lock();
        try {
            return chars.get(leaderId);
        } finally {
            readLock.unlock();
        }
    }

/** 设置事件队长 */
    public final void setLeader(Character chr) {
        writeLock.lock();
        try {
            leaderId = chr.getId();
        } finally {
            writeLock.unlock();
        }
    }

/** 播放组队任务失败特效 */
    public final void showWrongEffect() {
        showWrongEffect(getLeader().getMapId());
    }

/** 播放组队任务失败特效 */
    public final void showWrongEffect(int mapId) {
        MapleMap map = getMapInstance(mapId);
        map.broadcastMessage(PacketCreator.showEffect("quest/party/wrong_kor"));
        map.broadcastMessage(PacketCreator.playSound("Party1/Failed"));
    }

/** 播放组队任务通关特效 */
    public final void showClearEffect() {
        showClearEffect(false);
    }

/** 播放组队任务通关特效 */
    public final void showClearEffect(boolean hasGate) {
        Character leader = getLeader();
        if (leader != null) {
            showClearEffect(hasGate, leader.getMapId());
        }
    }

/** 播放组队任务通关特效 */
    public final void showClearEffect(int mapId) {
        showClearEffect(false, mapId);
    }

/** 播放组队任务通关特效 */
    public final void showClearEffect(boolean hasGate, int mapId) {
        showClearEffect(hasGate, mapId, "gate", 2);
    }

/** 播放组队任务通关特效 */
    public final void showClearEffect(int mapId, String mapObj, int newState) {
        showClearEffect(true, mapId, mapObj, newState);
    }

/** 播放组队任务通关特效 */
    public final void showClearEffect(boolean hasGate, int mapId, String mapObj, int newState) {
        MapleMap map = getMapInstance(mapId);
        map.broadcastMessage(PacketCreator.showEffect("quest/party/clear"));
        map.broadcastMessage(PacketCreator.playSound("Party1/Clear"));
        if (hasGate) {
            map.broadcastMessage(PacketCreator.environmentChange(mapObj, newState));
            writeLock.lock();
            try {
                openedGates.put(map.getId(), new Pair<>(mapObj, newState));
            } finally {
                writeLock.unlock();
            }
        }
    }

/** 为重进地图的玩家恢复已开启的门状态 */
    public final void recoverOpenedGate(Character chr, int thisMapId) {
        Pair<String, Integer> gateData = null;

        readLock.lock();
        try {
            if (openedGates.containsKey(thisMapId)) {
                gateData = openedGates.get(thisMapId);
            }
        } finally {
            readLock.unlock();
        }

        if (gateData != null) {
            chr.sendPacket(PacketCreator.environmentChange(gateData.getLeft(), gateData.getRight()));
        }
    }

/** 发放当前阶段通关奖励 */
    public final void giveEventPlayersStageReward(int thisStage) {
        List<Integer> list = getClearStageBonus(thisStage);     // will give bonus exp & mesos to everyone in the event
        giveEventPlayersExp(list.get(0));
        giveEventPlayersMeso(list.get(1));
    }

/** 链接下一阶段传送门脚本 */
    public final void linkToNextStage(int thisStage, String eventFamily, int thisMapId) {
        giveEventPlayersStageReward(thisStage);
        thisStage--;    //stages counts from ONE, scripts from ZERO

        MapleMap nextStage = getMapInstance(thisMapId);
        Portal portal = nextStage.getPortal("next00");
        if (portal != null) {
            portal.setScriptName(eventFamily + thisStage);
        }
    }

/** 将指定传送门绑定到脚本名 */
    public final void linkPortalToScript(int thisStage, String portalName, String scriptName, int thisMapId) {
        giveEventPlayersStageReward(thisStage);
        thisStage--;    //stages counts from ONE, scripts from ZERO

        MapleMap nextStage = getMapInstance(thisMapId);
        Portal portal = nextStage.getPortal(portalName);
        if (portal != null) {
            portal.setScriptName(scriptName);
        }
    }

    // registers a player status in an event
/** 在事件网格中记录玩家状态 */
    public final void gridInsert(Character chr, int newStatus) {
        writeLock.lock();
        try {
            playerGrid.put(chr.getId(), newStatus);
        } finally {
            writeLock.unlock();
        }
    }

    // unregisters a player status in an event
/** 从事件网格移除玩家状态 */
    public final void gridRemove(Character chr) {
        writeLock.lock();
        try {
            playerGrid.remove(chr.getId());
        } finally {
            writeLock.unlock();
        }
    }

    // checks a player status
/** 查询玩家在事件网格中的状态 */
    public final int gridCheck(Character chr) {
        readLock.lock();
        try {
            Integer i = playerGrid.get(chr.getId());
            return (i != null) ? i : -1;
        } finally {
            readLock.unlock();
        }
    }

/** 获取事件网格记录数量 */
    public final int gridSize() {
        readLock.lock();
        try {
            return playerGrid.size();
        } finally {
            readLock.unlock();
        }
    }

/** 清空事件网格 */
    public final void gridClear() {
        writeLock.lock();
        try {
            playerGrid.clear();
        } finally {
            writeLock.unlock();
        }
    }

/** 检查地图上指定 ID 范围反应堆是否全部激活 */
    public boolean activatedAllReactorsOnMap(int mapId, int minReactorId, int maxReactorId) {
        return activatedAllReactorsOnMap(this.getMapInstance(mapId), minReactorId, maxReactorId);
    }

/** 检查地图上指定 ID 范围反应堆是否全部激活 */
    public boolean activatedAllReactorsOnMap(MapleMap map, int minReactorId, int maxReactorId) {
        if (map == null) {
            return true;
        }

        for (Reactor mr : map.getReactorsByIdRange(minReactorId, maxReactorId)) {
            if (mr.getReactorType() != -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 开始记录伤害（仅在全局开关开启时生效）
     * 需要在副本初始化时调用（如 setup 或 playerEntry）
     */
    public void startDamageRecording() {
        if (recordDamage) return;
        if (GameConfig.getServerBoolean("damage_ranking")) {
            recordDamage = true;
        } else {
            log.debug("全局伤害统计未启用，无法记录伤害");
        }
    }

/** 累计玩家造成伤害 */
    public void addDamage(Character chr, int damage) {
        if (!recordDamage || chr == null || damage <= 0) return;

        long newValue = playerDamage.merge(chr.getId(), (long) damage, Long::sum);
        playerNames.putIfAbsent(chr.getId(), chr.getName()); //保存角色名
        // 防止溢出
        if (newValue < 0) {
            log.warn("玩家 {} 伤害累计溢出，重置为最大值", chr.getName());
            playerDamage.put(chr.getId(), Long.MAX_VALUE);
        } else if (newValue > MAX_DAMAGE_THRESHOLD) {
            log.warn("玩家 {} 伤害累计接近最大值", chr.getName());
        }
    }

    // 添加通报伤害排名的方法
/** 广播伤害排名到事件内玩家 */
    public synchronized void broadcastDamageRanking() {
        if (!GameConfig.getServerBoolean("damage_ranking")) {
            log.debug("伤害统计功能已被服务器禁用。");
            return;
        }
        if (!recordDamage) {
            log.debug("该副本未开启伤害统计。");
            return;
        }
        if (playerDamage.isEmpty()) {
            log.debug("尚无伤害数据。");
            return;
        }

        // 按伤害值降序排序
        List<Map.Entry<Integer, Long>> sorted = new ArrayList<>(playerDamage.entrySet());
        sorted.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        long totalDamage = sorted.stream().mapToLong(Map.Entry::getValue).sum();

        dropMessage(6, "========== 伤害统计 ==========");
        dropMessage(6, "总伤害: " + totalDamage);
        int rank = 1;
        for (Map.Entry<Integer, Long> entry : sorted) {
            if (rank > 5) break;
            int cid = entry.getKey();
            String name = playerNames.get(cid);
            if (name == null) {
                name = "未知玩家";   // fallback，理论上不会发生
            }
            dropMessage(6, rank + "名: " + name + " - " + entry.getValue() + " 伤害");
            rank++;
        }
        dropMessage(6, "==============================");
    }
/** 清空伤害统计数据 */
    public synchronized void clearDamage() {
        recordDamage = false;
        playerDamage.clear();
        playerNames.clear();
    }
}
