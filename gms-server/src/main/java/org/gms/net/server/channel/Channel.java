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
package org.gms.net.server.channel;

import org.gms.client.Character;
import org.gms.config.GameConfig;
import org.gms.constants.id.MapId;
import org.gms.manager.ServerManager;
import org.gms.net.netty.ChannelServer;
import org.gms.net.packet.Packet;
import org.gms.net.server.PlayerStorage;
import org.gms.net.server.Server;
import org.gms.net.server.services.BaseService;
import org.gms.net.server.services.ServicesManager;
import org.gms.net.server.services.type.ChannelServices;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.net.server.world.World;
import org.gms.property.ServiceProperty;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.event.EventScriptManager;
import org.gms.server.TimerManager;
import org.gms.server.events.gm.Event;
import org.gms.server.expeditions.Expedition;
import org.gms.server.expeditions.ExpeditionType;
import org.gms.server.maps.HiredMerchant;
import org.gms.server.maps.MapManager;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.MiniDungeon;
import org.gms.server.maps.MiniDungeonInfo;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 频道服务器类
 * 管理单个游戏频道的所有核心功能，包括：
 * - 玩家连接管理（添加、移除、广播消息）
 * - 地图管理（通过MapManager）
 * - 事件脚本管理
 * - 雇佣商人管理
 * - 远征队管理
 * - 道场（Dojo）管理
 * - 婚礼预约管理
 * - 迷你副本管理
 * - 怪物嘉年华管理
 * - 频道服务调度
 *
 * @author OdinMS开发团队
 */
public final class Channel {
    private static final Logger log = LoggerFactory.getLogger(Channel.class);
    /**
     * 基础端口号，频道实际端口 = BASE_PORT + (channel - 1) + (world * 100)
     */
    private static final int BASE_PORT = 7575;

    /**
     * 频道监听端口
     */
    private final int port;
    
    /**
     * 频道服务器IP地址（含端口）
     */
    private final String ip;
    
    /**
     * 所属世界ID
     */
    private final int world;
    
    /**
     * 频道ID
     */
    private final int channel;

    /**
     * 在线玩家存储
     */
    private PlayerStorage players = new PlayerStorage();
    
    /**
     * Netty频道服务器实例
     */
    private ChannelServer channelServer;
    
    /**
     * 服务器公告消息
     */
    private String serverMessage;
    
    /**
     * 地图管理器，管理频道内所有地图
     */
    private MapManager mapManager;
    
    /**
     * 事件脚本管理器，处理游戏事件脚本
     */
    private EventScriptManager eventSM;
    
    /**
     * 频道服务管理器，管理各种定时服务
     */
    private ServicesManager services;
    
    /**
     * 雇佣商人映射表（角色ID -> 雇佣商人）
     */
    private final Map<Integer, HiredMerchant> hiredMerchants = new HashMap<>();
    
    /**
     * 频道存储变量映射表
     */
    private final Map<Integer, Integer> storedVars = new HashMap<>();
    
    /**
     * 暂离玩家集合（进入现金商城或拍卖场的玩家ID）
     */
    private final Set<Integer> playersAway = new HashSet<>();
    
    /**
     * 远征队映射表（远征类型 -> 远征队）
     */
    private final Map<ExpeditionType, Expedition> expeditions = new HashMap<>();
    
    /**
     * 迷你副本映射表（副本ID -> 迷你副本）
     */
    private final Map<Integer, MiniDungeon> dungeons = new HashMap<>();
    
    /**
     * 远征类型列表
     */
    private final List<ExpeditionType> expedType = new ArrayList<>();
    
    /**
     * 有归属权的地图集合（使用WeakHashMap防止内存泄漏）
     */
    private final Set<MapleMap> ownedMaps = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
    
    /**
     * 当前GM活动事件
     */
    private Event event;
    
    /**
     * 频道是否已完成关闭
     */
    private boolean finishedShutdown = false;
    
    /**
     * 已使用的怪物嘉年华房间集合
     */
    private final Set<Integer> usedMC = new HashSet<>();

    /**
     * 道场使用情况位标记
     */
    private int usedDojo = 0;
    
    /**
     * 各道场槽位当前阶段
     */
    private int[] dojoStage;
    
    /**
     * 各道场槽位完成时间
     */
    private long[] dojoFinishTime;
    
    /**
     * 各道场槽位定时任务
     */
    private ScheduledFuture<?>[] dojoTask;
    
    /**
     * 道场组队映射表（组队hashCode -> 道场槽位）
     */
    private final Map<Integer, Integer> dojoParty = new HashMap<>();

    /**
     * 小教堂婚礼预约队列
     */
    private final List<Integer> chapelReservationQueue = new LinkedList<>();
    
    /**
     * 大教堂婚礼预约队列
     */
    private final List<Integer> cathedralReservationQueue = new LinkedList<>();
    
    /**
     * 小教堂婚礼预约超时任务
     */
    private ScheduledFuture<?> chapelReservationTask;
    
    /**
     * 大教堂婚礼预约超时任务
     */
    private ScheduledFuture<?> cathedralReservationTask;

    /**
     * 当前进行中的小教堂婚礼ID
     */
    private Integer ongoingChapel = null;
    
    /**
     * 当前进行中的小教堂婚礼是否为高级婚礼
     */
    private Boolean ongoingChapelType = null;
    
    /**
     * 当前进行中的小教堂婚礼宾客ID集合
     */
    private Set<Integer> ongoingChapelGuests = null;
    
    /**
     * 当前进行中的大教堂婚礼ID
     */
    private Integer ongoingCathedral = null;
    
    /**
     * 当前进行中的大教堂婚礼是否为高级婚礼
     */
    private Boolean ongoingCathedralType = null;
    
    /**
     * 当前进行中的大教堂婚礼宾客ID集合
     */
    private Set<Integer> ongoingCathedralGuests = null;
    
    /**
     * 当前婚礼开始时间
     */
    private long ongoingStartTime;

    /**
     * 道场操作锁
     */
    private final Lock lock = new ReentrantLock(true);;
    
    /**
     * 雇佣商人读锁
     */
    private final Lock merchRlock;
    
    /**
     * 雇佣商人写锁
     */
    private final Lock merchWlock;
    
    /**
     * 服务配置属性
     */
    private static final ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);

    /**
     * 构造函数：创建并初始化频道服务器
     *
     * @param world 所属世界ID
     * @param channel 频道ID
     * @param startTime 服务器启动时间
     */
    public Channel(final int world, final int channel, long startTime) {
        this.world = world;
        this.channel = channel;

        // 设置婚礼预约初始开始时间（世界最后一个频道启动时间+10秒，作为首次婚礼预约的占位符）
        this.ongoingStartTime = startTime + 10000;
        // 初始化地图管理器
        this.mapManager = new MapManager(null, world, channel);
        // 计算端口号：基础端口 + (频道号-1) + (世界ID*100)
        this.port = BASE_PORT + (this.channel - 1) + (world * 100);
        this.ip = serviceProperty.getWanHost() + ":" + port;

        // 初始化雇佣商人读写锁
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);
        this.merchRlock = rwLock.readLock();
        this.merchWlock = rwLock.writeLock();

        try {
            // 初始化并启动Netty频道服务器
            this.channelServer = initServer(port, world, channel);
            // 加载所有远征类型
            expedType.addAll(Arrays.asList(ExpeditionType.values()));

            if (Server.getInstance().isOnline()) {
                // 服务器已在线时加载事件脚本（延迟加载以提高启动速度）
                eventSM = new EventScriptManager(this, getEvents());
                eventSM.init();
            } else {
                // 服务器启动阶段只加载示例事件
                String[] ev = {"0_EXAMPLE"};
                eventSM = new EventScriptManager(this, ev);
            }

            // 初始化道场数据（最多20个道场槽位）
            dojoStage = new int[20];
            dojoFinishTime = new long[20];
            dojoTask = new ScheduledFuture<?>[20];
            for (int i = 0; i < 20; i++) {
                dojoStage[i] = 0;
                dojoFinishTime[i] = 0;
                dojoTask[i] = null;
            }

            // 初始化频道服务管理器
            services = new ServicesManager(ChannelServices.OVERALL);

            log.info(I18nUtil.getLogMessage("Channel.info1"), getId(), port);
        } catch (Exception e) {
            log.error(I18nUtil.getLogMessage("Channel.error1"), e);
        }
    }

    /**
     * 初始化并启动Netty频道服务器
     *
     * @param port 监听端口
     * @param world 世界ID
     * @param channel 频道ID
     * @return 启动后的ChannelServer实例
     */
    private ChannelServer initServer(int port, int world, int channel) {
        ChannelServer channelServer = new ChannelServer(port, world, channel);
        channelServer.start();
        return channelServer;
    }

    /**
     * 重新加载事件脚本管理器
     * 同步方法，防止并发重载
     */
    public synchronized void reloadEventScriptManager() {
        if (finishedShutdown) {
            return;
        }

        eventSM.cancel();
        eventSM = null;
        eventSM = new EventScriptManager(this, getEvents());
    }

    /**
     * 关闭频道服务器
     * 执行清理工作：关闭所有雇佣商人、断开暂离玩家、断开所有玩家、释放资源
     */
    public synchronized void shutdown() {
        try {
            if (finishedShutdown) {
                return;
            }

            log.info(I18nUtil.getLogMessage("Channel.shutdown.info1"), world, channel);

            // 关闭所有雇佣商人
            closeAllMerchants();
            // 断开暂离玩家（在现金商城/拍卖场的玩家）
            disconnectAwayPlayers();
            // 断开所有在线玩家
            players.disconnectAll();

            // 释放事件脚本管理器
            eventSM.dispose();
            eventSM = null;

            // 释放地图管理器
            mapManager.dispose();
            mapManager = null;

            // 关闭频道定时任务和服务
            closeChannelSchedules();
            players = null;

            // 停止Netty服务器
            channelServer.stop();

            finishedShutdown = true;
            log.info(I18nUtil.getLogMessage("Channel.shutdown.info2"), world, channel);
        } catch (Exception e) {
            log.info(I18nUtil.getLogMessage("Channel.shutdown.error1"), world, channel, e.getMessage(), e);
        }
    }

    /**
     * 关闭频道所有服务
     */
    private void closeChannelServices() {
        services.shutdown();
    }

    /**
     * 关闭频道所有定时任务
     * 包括道场超时任务等
     */
    private void closeChannelSchedules() {
        lock.lock();
        try {
            for (int i = 0; i < dojoTask.length; i++) {
                if (dojoTask[i] != null) {
                    dojoTask[i].cancel(false);
                    dojoTask[i] = null;
                }
            }
        } finally {
            lock.unlock();
        }

        closeChannelServices();
    }

    /**
     * 强制关闭所有雇佣商人
     */
    private void closeAllMerchants() {
        try {
            List<HiredMerchant> merchs;

            merchWlock.lock();
            try {
                merchs = new ArrayList<>(hiredMerchants.values());
                hiredMerchants.clear();
            } finally {
                merchWlock.unlock();
            }

            for (HiredMerchant merch : merchs) {
                merch.forceClose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取地图工厂（地图管理器）
     *
     * @return 地图管理器实例
     */
    public MapManager getMapFactory() {
        return mapManager;
    }

    /**
     * 获取指定类型的频道服务访问接口
     *
     * @param sv 频道服务类型
     * @return 对应的基础服务实例
     */
    public BaseService getServiceAccess(ChannelServices sv) {
        return services.getAccess(sv).getService();
    }

    /**
     * 获取所属世界ID
     *
     * @return 世界ID
     */
    public int getWorld() {
        return world;
    }

    /**
     * 获取所属世界服务器实例
     *
     * @return World世界服务器实例
     */
    public World getWorldServer() {
        return Server.getInstance().getWorld(world);
    }

    /**
     * 添加玩家到频道
     * 同时发送服务器公告消息
     *
     * @param chr 角色对象
     */
    public void addPlayer(Character chr) {
        players.addPlayer(chr);
        chr.sendPacket(PacketCreator.serverMessage(serverMessage));
    }

    /**
     * 获取服务器公告消息
     *
     * @return 服务器公告消息字符串
     */
    public String getServerMessage() {
        return serverMessage;
    }

    /**
     * 获取玩家存储对象
     *
     * @return PlayerStorage玩家存储实例
     */
    public PlayerStorage getPlayerStorage() {
        return players;
    }

    /**
     * 从频道移除玩家
     *
     * @param chr 角色对象
     * @return 移除成功返回true，否则返回false
     */
    public boolean removePlayer(Character chr) {
        return players.removePlayer(chr.getId()) != null;
    }

    /**
     * 获取频道容量状态（0-800的数值，表示负载程度）
     *
     * @return 频道容量状态值
     */
    public int getChannelCapacity() {
        return (int) (Math.ceil(((float) players.getAllCharacters().size() / GameConfig.getServerInt("channel_capacity")) * 800));
    }

    /**
     * 向频道内所有玩家广播数据包
     *
     * @param packet 要广播的数据包
     */
    public void broadcastPacket(Packet packet) {
        for (Character chr : players.getAllCharacters()) {
            chr.sendPacket(packet);
        }
    }

    /**
     * 获取频道ID
     *
     * @return 频道ID
     */
    public final int getId() {
        return channel;
    }

    /**
     * 获取频道IP地址（含端口）
     *
     * @return IP地址字符串
     */
    public String getIP() {
        return ip;
    }

    /**
     * 获取当前GM活动事件
     *
     * @return Event事件对象
     */
    public Event getEvent() {
        return event;
    }

    /**
     * 设置当前GM活动事件
     *
     * @param event 事件对象
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * 获取事件脚本管理器
     *
     * @return EventScriptManager实例
     */
    public EventScriptManager getEventSM() {
        return eventSM;
    }

    /**
     * 向频道内所有GM广播数据包
     *
     * @param packet 要广播的数据包
     */
    public void broadcastGMPacket(Packet packet) {
        for (Character chr : players.getAllCharacters()) {
            if (chr.isGM()) {
                chr.sendPacket(packet);
            }
        }
    }

    /**
     * 获取组队中在本频道的成员列表
     *
     * @param party 组队对象
     * @return 本频道内的组队成员角色列表
     */
    public List<Character> getPartyMembers(Party party) {
        List<Character> partym = new ArrayList<>(8);
        for (PartyCharacter partychar : party.getMembers()) {
            if (partychar.getChannel() == getId()) {
                Character chr = getPlayerStorage().getCharacterByName(partychar.getName());
                if (chr != null) {
                    partym.add(chr);
                }
            }
        }
        return partym;
    }

    /**
     * 标记玩家为暂离状态（进入现金商城或拍卖场）
     *
     * @param chrId 角色ID
     */
    public void insertPlayerAway(int chrId) {
        playersAway.add(chrId);
    }

    /**
     * 移除玩家暂离状态
     *
     * @param chrId 角色ID
     */
    public void removePlayerAway(int chrId) {
        playersAway.remove(chrId);
    }

    /**
     * 检查频道是否可以卸载（没有在线玩家且没有暂离玩家）
     *
     * @return 可以卸载返回true
     */
    public boolean canUninstall() {
        return players.getSize() == 0 && playersAway.isEmpty();
    }

    /**
     * 断开所有暂离玩家的连接
     */
    private void disconnectAwayPlayers() {
        World wserv = getWorldServer();
        for (Integer cid : playersAway) {
            Character chr = wserv.getPlayerStorage().getCharacterById(cid);
            if (chr != null && chr.isLoggedIn()) {
                chr.getClient().forceDisconnect();
            }
        }
    }

    /**
     * 获取所有雇佣商人（只读视图）
     *
     * @return 不可修改的雇佣商人映射表
     */
    public Map<Integer, HiredMerchant> getHiredMerchants() {
        merchRlock.lock();
        try {
            return Collections.unmodifiableMap(hiredMerchants);
        } finally {
            merchRlock.unlock();
        }
    }

    /**
     * 添加雇佣商人
     *
     * @param chrid 角色ID
     * @param hm 雇佣商人对象
     */
    public void addHiredMerchant(int chrid, HiredMerchant hm) {
        merchWlock.lock();
        try {
            hiredMerchants.put(chrid, hm);
        } finally {
            merchWlock.unlock();
        }
    }

    /**
     * 移除雇佣商人
     *
     * @param chrid 角色ID
     */
    public void removeHiredMerchant(int chrid) {
        merchWlock.lock();
        try {
            hiredMerchants.remove(chrid);
        } finally {
            merchWlock.unlock();
        }
    }

    /**
     * 批量查找好友在线状态
     *
     * @param charIdFrom 请求者角色ID
     * @param characterIds 要查找的角色ID数组
     * @return 在线且对请求者可见的好友ID数组
     */
    public int[] multiBuddyFind(int charIdFrom, int[] characterIds) {
        List<Integer> ret = new ArrayList<>(characterIds.length);
        PlayerStorage playerStorage = getPlayerStorage();
        for (int characterId : characterIds) {
            Character chr = playerStorage.getCharacterById(characterId);
            if (chr != null) {
                if (chr.getBuddylist().containsVisible(charIdFrom)) {
                    ret.add(characterId);
                }
            }
        }
        int[] retArr = new int[ret.size()];
        int pos = 0;
        for (Integer i : ret) {
            retArr[pos++] = i;
        }
        return retArr;
    }

    /**
     * 添加远征队
     *
     * @param exped 远征队对象
     * @return 添加成功返回true，已存在同类型远征队返回false
     */
    public boolean addExpedition(Expedition exped) {
        synchronized (expeditions) {
            if (expeditions.containsKey(exped.getType())) {
                return false;
            }

            expeditions.put(exped.getType(), exped);
            exped.beginRegistration();
            return true;
        }
    }

    /**
     * 移除远征队
     *
     * @param exped 远征队对象
     */
    public void removeExpedition(Expedition exped) {
        synchronized (expeditions) {
            expeditions.remove(exped.getType());
        }
    }

    /**
     * 获取指定类型的远征队
     *
     * @param type 远征队类型
     * @return 远征队对象，不存在返回null
     */
    public Expedition getExpedition(ExpeditionType type) {
        return expeditions.get(type);
    }

    /**
     * 获取所有远征队列表
     *
     * @return 远征队列表副本
     */
    public List<Expedition> getExpeditions() {
        synchronized (expeditions) {
            return new ArrayList<>(expeditions.values());
        }
    }

    /**
     * 检查玩家是否在本频道在线
     *
     * @param name 角色名
     * @return 在线返回true
     */
    public boolean isConnected(String name) {
        return getPlayerStorage().getCharacterByName(name) != null;
    }

    /**
     * 检查频道事件脚本是否处于活动状态
     *
     * @return 活动返回true
     */
    public boolean isActive() {
        EventScriptManager esm = this.getEventSM();
        return esm != null && esm.isActive();
    }

    /**
     * 检查频道是否已完成关闭
     *
     * @return 已关闭返回true
     */
    public boolean finishedShutdown() {
        return finishedShutdown;
    }

    /**
     * 设置服务器公告消息并广播
     *
     * @param message 公告消息
     */
    public void setServerMessage(String message) {
        this.serverMessage = message;
        broadcastPacket(PacketCreator.serverMessage(message));
        getWorldServer().resetDisabledServerMessages();
    }

    private static String[] getEvents() {
        // 事件脚本固定放在 scripts/event 以及对应的语言目录 scripts-语言/event。
        String scriptName = "scripts";
        String eventPath = "event";
        // 读取当前服务端语言配置，用来定位语言事件脚本目录。
        ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);
        String scriptLangName = scriptName + "-" + serviceProperty.getLanguage();

        // 默认目录保留英文原版事件，语言目录只保留已本地化的事件。
        Path scriptPath = Path.of(scriptName, eventPath);
        Path scriptLangPath = Path.of(scriptLangName, eventPath);

        // 先枚举默认事件，保证未翻译事件不会因为语言目录存在而丢失。
        List<String> events = new ArrayList<>();
        addEvents(scriptPath, events);
        // 再枚举语言事件，补充中文专属事件；同名事件会在 addEvents 中去重。
        addEvents(scriptLangPath, events);
        // 这里只返回事件名，真正加载脚本时仍由 AbstractScriptManager 做文件级回退。
        return events.toArray(new String[0]);
    }

    private static void addEvents(Path eventPath, List<String> events) {
        // 某些语言可能没有 event 目录，没有目录时直接跳过，继续使用默认事件。
        if (!Files.isDirectory(eventPath)) {
            return;
        }

        // 只枚举 JS 事件脚本，避免把其他辅助文件当成事件名注册。
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(eventPath, "*.js")) {
            for (Path path : stream) {
                // 事件管理器使用不带 .js 后缀的脚本名。
                String fileName = path.getFileName().toString();
                String eventName = fileName.substring(0, fileName.length() - 3);
                // 默认目录和语言目录可能存在同名事件，同名只注册一次。
                if (!events.contains(eventName)) {
                    events.add(eventName);
                }
            }
        } catch (IOException e) {
            log.warn("Unable to load events !");
            e.printStackTrace();
        }
    }

    public int getStoredVar(int key) {
        if (storedVars.containsKey(key)) {
            return storedVars.get(key);
        }

        return 0;
    }

    public void setStoredVar(int key, int val) {
        this.storedVars.put(key, val);
    }

    public int lookupPartyDojo(Party party) {
        if (party == null) {
            return -1;
        }

        Integer i = dojoParty.get(party.hashCode());
        return (i != null) ? i : -1;
    }

    public int ingressDojo(boolean isPartyDojo, int fromStage) {
        return ingressDojo(isPartyDojo, null, fromStage);
    }

    public int ingressDojo(boolean isPartyDojo, Party party, int fromStage) {
        lock.lock();
        try {
            int dojoList = this.usedDojo;
            int range, slot = 0;

            if (!isPartyDojo) {
                dojoList = dojoList >> 5;
                range = 15;
            } else {
                range = 5;
            }

            while ((dojoList & 1) != 0) {
                dojoList = (dojoList >> 1);
                slot++;
            }

            if (slot < range) {
                int slotMapid = (isPartyDojo ? MapId.DOJO_PARTY_BASE : MapId.DOJO_SOLO_BASE) + (100 * (fromStage + 1)) + slot;
                int dojoSlot = getDojoSlot(slotMapid);

                if (party != null) {
                    if (dojoParty.containsKey(party.hashCode())) {
                        return -2;
                    }
                    dojoParty.put(party.hashCode(), dojoSlot);
                }

                this.usedDojo |= (1 << dojoSlot);

                this.resetDojo(slotMapid);
                this.startDojoSchedule(slotMapid);
                return slot;
            } else {
                return -1;
            }
        } finally {
            lock.unlock();
        }
    }

    private void freeDojoSlot(int slot, Party party) {
        int mask = 0b11111111111111111111;
        mask ^= (1 << slot);

        lock.lock();
        try {
            usedDojo &= mask;
        } finally {
            lock.unlock();
        }

        if (party != null) {
            if (dojoParty.remove(party.hashCode()) != null) {
                return;
            }
        }

        if (dojoParty.containsValue(slot)) {    // strange case, no party there!
            Set<Entry<Integer, Integer>> es = new HashSet<>(dojoParty.entrySet());

            for (Entry<Integer, Integer> e : es) {
                if (e.getValue() == slot) {
                    dojoParty.remove(e.getKey());
                    break;
                }
            }
        }
    }

    private static int getDojoSlot(int dojoMapId) {
        return (dojoMapId % 100) + ((dojoMapId / 10000 == 92502) ? 5 : 0);
    }

    public void resetDojoMap(int fromMapId) {
        for (int i = 0; i < (((fromMapId / 100) % 100 <= 36) ? 5 : 2); i++) {
            this.getMapFactory().getMap(fromMapId + (100 * i)).resetMapObjects();
        }
    }

    public void resetDojo(int dojoMapId) {
        resetDojo(dojoMapId, -1);
    }

    private void resetDojo(int dojoMapId, int thisStg) {
        int slot = getDojoSlot(dojoMapId);
        this.dojoStage[slot] = thisStg;
    }

    public void freeDojoSectionIfEmpty(int dojoMapId) {
        final int slot = getDojoSlot(dojoMapId);
        final int delta = (dojoMapId) % 100;
        final int stage = (dojoMapId / 100) % 100;
        final int dojoBaseMap = (dojoMapId >= MapId.DOJO_PARTY_BASE) ? MapId.DOJO_PARTY_BASE : MapId.DOJO_SOLO_BASE;

        for (int i = 0; i < 5; i++) { //only 32 stages, but 38 maps
            if (stage + i > 38) {
                break;
            }
            MapleMap dojoMap = getMapFactory().getMap(dojoBaseMap + (100 * (stage + i)) + delta);
            if (!dojoMap.getAllPlayers().isEmpty()) {
                return;
            }
        }

        freeDojoSlot(slot, null);
    }

    private void startDojoSchedule(final int dojoMapId) {
        final int slot = getDojoSlot(dojoMapId);
        final int stage = (dojoMapId / 100) % 100;
        if (stage <= dojoStage[slot]) {
            return;
        }

        long clockTime = (stage > 36 ? 15 : (stage / 6) + 5) * 60000;

        lock.lock();
        try {
            if (this.dojoTask[slot] != null) {
                this.dojoTask[slot].cancel(false);
            }
            this.dojoTask[slot] = TimerManager.getInstance().schedule(() -> {
                final int delta = (dojoMapId) % 100;
                final int dojoBaseMap = (slot < 5) ? MapId.DOJO_PARTY_BASE : MapId.DOJO_SOLO_BASE;
                Party party = null;

                for (int i = 0; i < 5; i++) { //only 32 stages, but 38 maps
                    if (stage + i > 38) {
                        break;
                    }

                    MapleMap dojoExit = getMapFactory().getMap(MapId.DOJO_EXIT);
                    for (Character chr : getMapFactory().getMap(dojoBaseMap + (100 * (stage + i)) + delta).getAllPlayers()) {
                        if (MapId.isDojo(chr.getMap().getId())) {
                            chr.changeMap(dojoExit);
                        }
                        party = chr.getParty();
                    }
                }

                freeDojoSlot(slot, party);
            }, clockTime + 3000);   // let the TIMES UP display for 3 seconds, then warp
        } finally {
            lock.unlock();
        }

        dojoFinishTime[slot] = Server.getInstance().getCurrentTime() + clockTime;
    }

    public void dismissDojoSchedule(int dojoMapId, Party party) {
        int slot = getDojoSlot(dojoMapId);
        int stage = (dojoMapId / 100) % 100;
        if (stage <= dojoStage[slot]) {
            return;
        }

        lock.lock();
        try {
            if (this.dojoTask[slot] != null) {
                this.dojoTask[slot].cancel(false);
                this.dojoTask[slot] = null;
            }
        } finally {
            lock.unlock();
        }

        freeDojoSlot(slot, party);
    }

    public boolean setDojoProgress(int dojoMapId) {
        int slot = getDojoSlot(dojoMapId);
        int dojoStg = (dojoMapId / 100) % 100;

        if (this.dojoStage[slot] < dojoStg) {
            this.dojoStage[slot] = dojoStg;
            return true;
        } else {
            return false;
        }
    }

    public long getDojoFinishTime(int dojoMapId) {
        return dojoFinishTime[getDojoSlot(dojoMapId)];
    }

    public boolean addMiniDungeon(int dungeonid) {
        lock.lock();
        try {
            if (dungeons.containsKey(dungeonid)) {
                return false;
            }

            MiniDungeonInfo mmdi = MiniDungeonInfo.getDungeon(dungeonid);
            MiniDungeon mmd = new MiniDungeon(mmdi.getBase(), this.getMapFactory().getMap(mmdi.getDungeonId()).getTimeLimit());   // thanks Conrad for noticing hardcoded time limit for minidungeons

            dungeons.put(dungeonid, mmd);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public MiniDungeon getMiniDungeon(int dungeonid) {
        lock.lock();
        try {
            return dungeons.get(dungeonid);
        } finally {
            lock.unlock();
        }
    }

    public void removeMiniDungeon(int dungeonid) {
        lock.lock();
        try {
            dungeons.remove(dungeonid);
        } finally {
            lock.unlock();
        }
    }

    public Pair<Boolean, Pair<Integer, Set<Integer>>> getNextWeddingReservation(boolean cathedral) {
        Integer ret;

        lock.lock();
        try {
            List<Integer> weddingReservationQueue = (cathedral ? cathedralReservationQueue : chapelReservationQueue);
            if (weddingReservationQueue.isEmpty()) {
                return null;
            }

            ret = weddingReservationQueue.remove(0);
            if (ret == null) {
                return null;
            }
        } finally {
            lock.unlock();
        }

        World wserv = getWorldServer();

        Pair<Integer, Integer> coupleId = wserv.getMarriageQueuedCouple(ret);
        Pair<Boolean, Set<Integer>> typeGuests = wserv.removeMarriageQueued(ret);

        Pair<String, String> couple = new Pair<>(Character.getNameById(coupleId.getLeft()), Character.getNameById(coupleId.getRight()));
        wserv.dropMessage(6, couple.getLeft() + " and " + couple.getRight() + "'s wedding is going to be started at " + (cathedral ? "Cathedral" : "Chapel") + " on Channel " + channel + ".");

        return new Pair<>(typeGuests.getLeft(), new Pair<>(ret, typeGuests.getRight()));
    }

    public boolean isWeddingReserved(Integer weddingId) {
        World wserv = getWorldServer();

        lock.lock();
        try {
            return wserv.isMarriageQueued(weddingId) || weddingId.equals(ongoingCathedral) || weddingId.equals(ongoingChapel);
        } finally {
            lock.unlock();
        }
    }

    public int getWeddingReservationStatus(Integer weddingId, boolean cathedral) {
        if (weddingId == null) {
            return -1;
        }

        lock.lock();
        try {
            if (cathedral) {
                if (weddingId.equals(ongoingCathedral)) {
                    return 0;
                }

                for (int i = 0; i < cathedralReservationQueue.size(); i++) {
                    if (weddingId.equals(cathedralReservationQueue.get(i))) {
                        return i + 1;
                    }
                }
            } else {
                if (weddingId.equals(ongoingChapel)) {
                    return 0;
                }

                for (int i = 0; i < chapelReservationQueue.size(); i++) {
                    if (weddingId.equals(chapelReservationQueue.get(i))) {
                        return i + 1;
                    }
                }
            }

            return -1;
        } finally {
            lock.unlock();
        }
    }

    public int pushWeddingReservation(Integer weddingId, boolean cathedral, boolean premium, Integer groomId, Integer brideId) {
        if (weddingId == null || isWeddingReserved(weddingId)) {
            return -1;
        }

        World wserv = getWorldServer();
        wserv.putMarriageQueued(weddingId, cathedral, premium, groomId, brideId);

        lock.lock();
        try {
            List<Integer> weddingReservationQueue = (cathedral ? cathedralReservationQueue : chapelReservationQueue);

            int delay = GameConfig.getServerInt("wedding_reservation_delay") - 1 - weddingReservationQueue.size();
            for (int i = 0; i < delay; i++) {
                weddingReservationQueue.add(null);  // push empty slots to fill the waiting time
            }

            weddingReservationQueue.add(weddingId);
            return weddingReservationQueue.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean isOngoingWeddingGuest(boolean cathedral, int playerId) {
        lock.lock();
        try {
            if (cathedral) {
                return ongoingCathedralGuests != null && ongoingCathedralGuests.contains(playerId);
            } else {
                return ongoingChapelGuests != null && ongoingChapelGuests.contains(playerId);
            }
        } finally {
            lock.unlock();
        }
    }

    public Integer getOngoingWedding(boolean cathedral) {
        lock.lock();
        try {
            return cathedral ? ongoingCathedral : ongoingChapel;
        } finally {
            lock.unlock();
        }
    }

    public boolean getOngoingWeddingType(boolean cathedral) {
        lock.lock();
        try {
            return cathedral ? ongoingCathedralType : ongoingChapelType;
        } finally {
            lock.unlock();
        }
    }

    public void closeOngoingWedding(boolean cathedral) {
        lock.lock();
        try {
            if (cathedral) {
                ongoingCathedral = null;
                ongoingCathedralType = null;
                ongoingCathedralGuests = null;
            } else {
                ongoingChapel = null;
                ongoingChapelType = null;
                ongoingChapelGuests = null;
            }
        } finally {
            lock.unlock();
        }
    }

    public void setOngoingWedding(final boolean cathedral, Boolean premium, Integer weddingId, Set<Integer> guests) {
        lock.lock();
        try {
            if (cathedral) {
                ongoingCathedral = weddingId;
                ongoingCathedralType = premium;
                ongoingCathedralGuests = guests;
            } else {
                ongoingChapel = weddingId;
                ongoingChapelType = premium;
                ongoingChapelGuests = guests;
            }
        } finally {
            lock.unlock();
        }

        ongoingStartTime = System.currentTimeMillis();
        if (weddingId != null) {
            ScheduledFuture<?> weddingTask = TimerManager.getInstance().schedule(() -> closeOngoingWedding(cathedral), MINUTES.toMillis(GameConfig.getServerLong("wedding_reservation_timeout")));

            if (cathedral) {
                cathedralReservationTask = weddingTask;
            } else {
                chapelReservationTask = weddingTask;
            }
        }
    }

    public synchronized boolean acceptOngoingWedding(final boolean cathedral) {     // couple succeeded to show up and started the ceremony
        if (cathedral) {
            if (cathedralReservationTask == null) {
                return false;
            }

            cathedralReservationTask.cancel(false);
            cathedralReservationTask = null;
        } else {
            if (chapelReservationTask == null) {
                return false;
            }

            chapelReservationTask.cancel(false);
            chapelReservationTask = null;
        }

        return true;
    }

    private static String getTimeLeft(long futureTime) {
        StringBuilder str = new StringBuilder();
        long leftTime = futureTime - System.currentTimeMillis();

        if (leftTime < 0) {
            return null;
        }

        byte mode = 0;
        if (leftTime / (MINUTES.toMillis(1)) > 0) {
            mode++;     //counts minutes

            if (leftTime / (HOURS.toMillis(1)) > 0) {
                mode++;     //counts hours
            }
        }

        switch (mode) {
            case 2:
                int hours = (int) ((leftTime / (HOURS.toMillis(1))));
                str.append(hours + " hours, ");

            case 1:
                int minutes = (int) ((leftTime / (MINUTES.toMillis(1))) % 60);
                str.append(minutes + " minutes, ");

            default:
                int seconds = (int) (leftTime / SECONDS.toMillis(1)) % 60;
                str.append(seconds + " seconds");
        }

        return str.toString();
    }

    public long getWeddingTicketExpireTime(int resSlot) {
        return ongoingStartTime + getRelativeWeddingTicketExpireTime(resSlot);
    }

    public static long getRelativeWeddingTicketExpireTime(int resSlot) {
        return MINUTES.toMillis((long) resSlot * GameConfig.getServerLong("wedding_reservation_interval"));
    }

    public String getWeddingReservationTimeLeft(Integer weddingId) {
        if (weddingId == null) {
            return null;
        }

        lock.lock();
        try {
            boolean cathedral = true;

            int resStatus;
            resStatus = getWeddingReservationStatus(weddingId, true);
            if (resStatus < 0) {
                cathedral = false;
                resStatus = getWeddingReservationStatus(weddingId, false);

                if (resStatus < 0) {
                    return null;
                }
            }

            String venue = (cathedral ? "Cathedral" : "Chapel");
            if (resStatus == 0) {
                return venue + " - RIGHT NOW";
            }

            return venue + " - " + getTimeLeft(ongoingStartTime + MINUTES.toMillis((long) resStatus * GameConfig.getServerLong("wedding_reservation_interval"))) + " from now";
        } finally {
            lock.unlock();
        }
    }

    public Pair<Integer, Integer> getWeddingCoupleForGuest(int guestId, boolean cathedral) {
        lock.lock();
        try {
            return (isOngoingWeddingGuest(cathedral, guestId)) ? getWorldServer().getRelationshipCouple(getOngoingWedding(cathedral)) : null;
        } finally {
            lock.unlock();
        }
    }

    public void dropMessage(int type, String message) {
        for (Character player : getPlayerStorage().getAllCharacters()) {
            player.dropMessage(type, message);
        }
    }

    public void registerOwnedMap(MapleMap map) {
        ownedMaps.add(map);
    }

    public void unregisterOwnedMap(MapleMap map) {
        ownedMaps.remove(map);
    }

    public void runCheckOwnedMapsSchedule() {
        if (!ownedMaps.isEmpty()) {
            List<MapleMap> ownedMapsList;

            synchronized (ownedMaps) {
                ownedMapsList = new ArrayList<>(ownedMaps);
            }

            for (MapleMap map : ownedMapsList) {
                map.checkMapOwnerActivity();
            }
        }
    }

    private static int getMonsterCarnivalRoom(boolean cpq1, int field) {
        return (cpq1 ? 0 : 100) + field;
    }

    public void initMonsterCarnival(boolean cpq1, int field) {
        usedMC.add(getMonsterCarnivalRoom(cpq1, field));
    }

    public void finishMonsterCarnival(boolean cpq1, int field) {
        usedMC.remove(getMonsterCarnivalRoom(cpq1, field));
    }

    public boolean canInitMonsterCarnival(boolean cpq1, int field) {
        return !usedMC.contains(getMonsterCarnivalRoom(cpq1, field));
    }

    public void debugMarriageStatus() {
        log.debug(" ----- WORLD DATA -----");
        getWorldServer().debugMarriageStatus();

        log.debug(" ----- CH. {} -----", channel);
        log.debug(" ----- CATHEDRAL -----");
        log.debug("Current Queue: {}", cathedralReservationQueue);
        log.debug("Cancel Task?: {}", cathedralReservationTask != null);
        log.debug("Ongoing wid: {}", ongoingCathedral);
        log.debug("Ongoing wid: {}, isPremium: {}", ongoingCathedral, ongoingCathedralType);
        log.debug("Guest list: {}", ongoingCathedralGuests);
        log.debug(" ----- CHAPEL -----");
        log.debug("Current Queue: {}", chapelReservationQueue);
        log.debug("Cancel Task?: {}", chapelReservationTask != null);
        log.debug("Ongoing wid: {}", ongoingChapel);
        log.debug("Ongoing wid: {}, isPremium: {}", ongoingChapel, ongoingChapelType);
        log.debug("Guest list: {}", ongoingChapelGuests);
        log.debug("Starttime: {}", ongoingStartTime);
    }
}
