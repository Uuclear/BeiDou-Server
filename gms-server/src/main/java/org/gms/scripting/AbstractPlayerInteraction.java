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
package org.gms.scripting;

import org.gms.client.Character;
import org.gms.client.*;
import org.gms.client.inventory.*;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.config.GameConfig;
import org.gms.constants.game.DelayedQuestUpdate;
import org.gms.constants.game.GameConstants;
import org.gms.constants.id.ItemId;
import org.gms.constants.id.MapId;
import org.gms.constants.id.NpcId;
import org.gms.constants.inventory.ItemConstants;
import org.gms.constants.string.ExtendType;
import org.gms.dao.entity.ExtendValueDO;
import org.gms.model.pojo.SkillEntry;
import org.gms.net.server.Server;
import org.gms.net.server.guild.Guild;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.scripting.event.EventInstanceManager;
import org.gms.scripting.event.EventManager;
import org.gms.scripting.npc.NPCScriptManager;
import org.gms.server.ItemInformationProvider;
import org.gms.server.Marriage;
import org.gms.server.expeditions.Expedition;
import org.gms.server.expeditions.ExpeditionBossLog;
import org.gms.server.expeditions.ExpeditionType;
import org.gms.server.life.*;
import org.gms.server.maps.MapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.server.maps.MapleMap;
import org.gms.server.partyquest.PartyQuest;
import org.gms.server.partyquest.Pyramid;
import org.gms.server.quest.Quest;
import org.gms.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.*;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 脚本与 Java 服务端之间的玩家交互 API 基类。
 * <p>
 * 各类脚本管理器（NPC、任务、传送门、反应堆等）向 GraalJS 注入本类或其子类实例（如 {@code cm}、{@code qm}），
 * 脚本通过调用本类方法操作玩家状态、背包、地图、任务、队伍等游戏逻辑。
 * </p>
 */
public class AbstractPlayerInteraction {

    private static final Logger log = LoggerFactory.getLogger(AbstractPlayerInteraction.class);

    /** 当前脚本关联的客户端连接，脚本中可通过 {@code cm.getClient()} 访问。 */
    public Client c;

    /**
     * 构造与指定客户端绑定的脚本交互对象。
     *
     * @param c 当前玩家客户端
     */
    public AbstractPlayerInteraction(Client c) {
        this.c = c;
    }

    /** @return 当前客户端连接 */
    public Client getClient() {
        return c;
    }

    /** @return 当前在线角色 */
    public Character getPlayer() {
        return c.getPlayer();
    }

    /** @return 当前在线角色（{@code getPlayer()} 的别名，兼容旧脚本） */
    public Character getChar() {
        return c.getPlayer();
    }

    /** @return 当前角色职业 ID */
    public int getJobId() {
        return getPlayer().getJob().getId();
    }

    /** @return 当前角色职业对象 */
    public Job getJob() {
        return getPlayer().getJob();
    }

    /** @return 当前角色等级 */
    public int getLevel() {
        return getPlayer().getLevel();
    }

    /** @return 当前角色所在地图 */
    public MapleMap getMap() {
        return c.getPlayer().getMap();
    }

    /** @return 当前服务器小时（0–23） */
    public int getHourOfDay() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取自由市场地图的传送门 ID。
     *
     * @param mapId 目标地图 ID
     * @return 市场传送门 ID，若无市场传送门则返回随机出生点 ID
     */
    public int getMarketPortalId(int mapId) {
        return getMarketPortalId(getWarpMap(mapId));
    }

    private int getMarketPortalId(MapleMap map) {
        return (map.findMarketPortal() != null) ? map.findMarketPortal().getId() : map.getRandomPlayerSpawnpoint().getId();
    }

    /** 将当前角色传送到指定地图（默认传送门）。 */
    public void warp(int mapid) {
        getPlayer().changeMap(mapid);
    }

    /** 将当前角色传送到指定地图与传送门。 */
    public void warp(int map, int portal) {
        getPlayer().changeMap(map, portal);
    }

    /** 将当前角色传送到指定地图与命名传送门。 */
    public void warp(int map, String portal) {
        getPlayer().changeMap(map, portal);
    }

    /** 将当前地图所有玩家传送到指定地图。 */
    public void warpMap(int map) {
        getPlayer().getMap().warpEveryone(map);
    }

    /** 将队伍成员传送到指定地图（默认传送门 0）。 */
    public void warpParty(int id) {
        warpParty(id, 0);
    }

    /** 将当前地图上的队伍成员传送到指定地图与传送门。 */
    public void warpParty(int id, int portalId) {
        int mapid = getMapId();
        warpParty(id, portalId, mapid, mapid);
    }

    /** 将当前地图上的队伍成员传送到指定地图与命名传送门。 */
    public void warpParty(int map, String portalName) {

        int mapid = getMapId();
        var warpMap = c.getChannelServer().getMapFactory().getMap(map);

        var portal = warpMap.getPortal(portalName);

        if (portal == null) {
            portal = warpMap.getPortal(0);
        }

        var portalId = portal.getId();

        warpParty(map, portalId, mapid, mapid);

    }

    /**
     * 将指定地图 ID 范围内的队伍成员传送到目标地图（默认传送门 0）。
     *
     * @param id        目标地图 ID
     * @param fromMinId 来源地图 ID 下限（含）
     * @param fromMaxId 来源地图 ID 上限（含）
     */
    public void warpParty(int id, int fromMinId, int fromMaxId) {
        warpParty(id, 0, fromMinId, fromMaxId);
    }

    /**
     * 将指定地图 ID 范围内的在线队伍成员传送到目标地图。
     *
     * @param id        目标地图 ID
     * @param portalId  目标传送门 ID
     * @param fromMinId 来源地图 ID 下限（含）
     * @param fromMaxId 来源地图 ID 上限（含）
     */
    public void warpParty(int id, int portalId, int fromMinId, int fromMaxId) {
        for (Character mc : this.getPlayer().getPartyMembersOnline()) {
            if (mc.isLoggedInWorld()) {
                if (mc.getMapId() >= fromMinId && mc.getMapId() <= fromMaxId) {
                    mc.changeMap(id, portalId);
                }
            }
        }
    }

    /** @return 指定地图 ID 对应的可传送地图实例 */
    public MapleMap getWarpMap(int map) {
        return getPlayer().getWarpMap(map);
    }

    /** @return 指定地图 ID 对应的地图实例（{@code getWarpMap} 别名） */
    public MapleMap getMap(int map) {
        return getWarpMap(map);
    }

    /** @return 指定地图上怪物总数 */
    public int countAllMonstersOnMap(int map) {
        return getMap(map).countMonsters();
    }

    /** @return 当前地图上怪物总数 */
    public int countMonster() {
        return getPlayer().getMap().countMonsters();
    }

    /** 重置指定地图上的地图对象（怪物、掉落等）。 */
    public void resetMapObjects(int mapid) {
        getWarpMap(mapid).resetMapObjects();
    }

    /**
     * 获取频道内指定名称的事件管理器。
     *
     * @param event 事件脚本名称
     * @return 对应 {@link EventManager}
     */
    public EventManager getEventManager(String event) {
        return getClient().getEventManager(event);
    }

    /** @return 当前角色参与的事件实例，未参与时可能为 {@code null} */
    public EventInstanceManager getEventInstance() {
        return getPlayer().getEventInstance();
    }

    /**
     * 按背包类型编号获取背包。
     *
     * @param type 背包类型字节值
     */
    public Inventory getInventory(int type) {
        return getPlayer().getInventory(InventoryType.getByType((byte) type));
    }

    /** 按背包类型枚举获取背包。 */
    public Inventory getInventory(InventoryType type) {
        return getPlayer().getInventory(type);
    }

    /** @return 是否持有至少 1 个指定物品 */
    public boolean hasItem(int itemid) {
        return haveItem(itemid, 1);
    }

    /** @return 是否持有不少于指定数量的物品 */
    public boolean hasItem(int itemid, int quantity) {
        return haveItem(itemid, quantity);
    }

    /** @return 是否持有至少 1 个指定物品（{@code hasItem} 别名） */
    public boolean haveItem(int itemid) {
        return haveItem(itemid, 1);
    }

    /** @return 是否持有不少于指定数量的物品 */
    public boolean haveItem(int itemid, int quantity) {
        return getPlayer().getItemQuantity(itemid, false) >= quantity;
    }

    /** @return 背包中指定物品的数量（不含已装备栏） */
    public int getItemQuantity(int itemid) {
        return getPlayer().getItemQuantity(itemid, false);
    }

    /** @return 是否拥有指定物品（默认不检查已装备栏） */
    public boolean haveItemWithId(int itemid) {
        return haveItemWithId(itemid, false);
    }

    /**
     * @param itemid         物品 ID
     * @param checkEquipped  是否同时检查已装备栏
     */
    public boolean haveItemWithId(int itemid, boolean checkEquipped) {
        return getPlayer().haveItemWithId(itemid, checkEquipped);
    }

    /** @return 背包是否有空位容纳 1 个指定物品 */
    public boolean canHold(int itemid) {
        return canHold(itemid, 1);
    }

    /** @return 背包是否有空位容纳指定数量物品 */
    public boolean canHold(int itemid, int quantity) {
        return canHoldAll(Collections.singletonList(itemid), Collections.singletonList(quantity), true);
    }

    /**
     * 检查在移除部分物品后是否能容纳新物品。
     *
     * @param itemid         待添加物品 ID
     * @param quantity       待添加数量
     * @param removeItemid   待移除物品 ID
     * @param removeQuantity 待移除数量
     */
    public boolean canHold(int itemid, int quantity, int removeItemid, int removeQuantity) {
        return canHoldAllAfterRemoving(Collections.singletonList(itemid), Collections.singletonList(quantity), Collections.singletonList(removeItemid), Collections.singletonList(removeQuantity));
    }

    private List<Integer> convertToIntegerList(List<Object> objects) {
        List<Integer> intList = new ArrayList<>();

        for (Object object : objects) {
            intList.add((Integer) object);
        }

        return intList;
    }

    /**
     * 检查是否能同时容纳多组物品（脚本传入 {@link List}{@code <Object>}，内部转为整数列表）。
     *
     * @param itemids  物品 ID 列表
     * @param quantity 对应数量列表，缺省时每项为 1
     */
    public boolean canHoldAll(List<Object> itemids) {
        List<Object> quantity = new LinkedList<>();

        final int intOne = 1;
        for (int i = 0; i < itemids.size(); i++) {
            quantity.add(intOne);
        }

        return canHoldAll(itemids, quantity);
    }

    /** 检查是否能同时容纳多组物品（脚本列表参数版本）。 */
    public boolean canHoldAll(List<Object> itemids, List<Object> quantity) {
        return canHoldAll(convertToIntegerList(itemids), convertToIntegerList(quantity), true);
    }

    private boolean canHoldAll(List<Integer> itemids, List<Integer> quantity, boolean isInteger) {
        int size = Math.min(itemids.size(), quantity.size());

        List<Pair<Item, InventoryType>> addedItems = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            Item it = new Item(itemids.get(i), (short) 0, quantity.get(i).shortValue());
            addedItems.add(new Pair<>(it, ItemConstants.getInventoryType(itemids.get(i))));
        }

        return Inventory.checkSpots(c.getPlayer(), addedItems);
    }

    private List<Pair<Item, InventoryType>> prepareProofInventoryItems(List<Pair<Integer, Integer>> items) {
        List<Pair<Item, InventoryType>> addedItems = new LinkedList<>();
        for (Pair<Integer, Integer> p : items) {
            Item it = new Item(p.getLeft(), (short) 0, p.getRight().shortValue());
            addedItems.add(new Pair<>(it, InventoryType.CANHOLD));
        }

        return addedItems;
    }

    private List<List<Pair<Integer, Integer>>> prepareInventoryItemList(List<Integer> itemids, List<Integer> quantity) {
        int size = Math.min(itemids.size(), quantity.size());

        List<List<Pair<Integer, Integer>>> invList = new ArrayList<>(6);
        for (int i = InventoryType.UNDEFINED.getType(); i <= InventoryType.CASH.getType(); i++) {
            invList.add(new LinkedList<>());
        }

        for (int i = 0; i < size; i++) {
            int itemid = itemids.get(i);
            invList.get(ItemConstants.getInventoryType(itemid).getType()).add(new Pair<>(itemid, quantity.get(i)));
        }

        return invList;
    }

    /**
     * 模拟移除部分物品后，检查是否能容纳待添加物品。
     * 使用 {@link InventoryProof} 克隆背包状态进行无副作用校验。
     */
    public boolean canHoldAllAfterRemoving(List<Integer> toAddItemids, List<Integer> toAddQuantity, List<Integer> toRemoveItemids, List<Integer> toRemoveQuantity) {
        List<List<Pair<Integer, Integer>>> toAddItemList = prepareInventoryItemList(toAddItemids, toAddQuantity);
        List<List<Pair<Integer, Integer>>> toRemoveItemList = prepareInventoryItemList(toRemoveItemids, toRemoveQuantity);

        InventoryProof prfInv = (InventoryProof) this.getInventory(InventoryType.CANHOLD);
        prfInv.lockInventory();
        try {
            for (int i = InventoryType.EQUIP.getType(); i < InventoryType.CASH.getType(); i++) {
                List<Pair<Integer, Integer>> toAdd = toAddItemList.get(i);

                if (!toAdd.isEmpty()) {
                    List<Pair<Integer, Integer>> toRemove = toRemoveItemList.get(i);

                    Inventory inv = this.getInventory(i);
                    prfInv.cloneContents(inv);

                    for (Pair<Integer, Integer> p : toRemove) {
                        InventoryManipulator.removeById(c, InventoryType.CANHOLD, p.getLeft(), p.getRight(), false, false);
                    }

                    List<Pair<Item, InventoryType>> addItems = prepareProofInventoryItems(toAdd);

                    boolean canHold = Inventory.checkSpots(c.getPlayer(), addItems, true);
                    if (!canHold) {
                        return false;
                    }
                }
            }
        } finally {
            prfInv.flushContents();
            prfInv.unlockInventory();
        }

        return true;
    }

    //---- \/ \/ \/ \/ \/ \/ \/  NOT TESTED  \/ \/ \/ \/ \/ \/ \/ \/ \/ ----

    public final QuestStatus getQuestRecord(final int id) {
        return c.getPlayer().getQuestNAdd(Quest.getInstance(id));
    }

/** 获取任务记录（不存在时不自动添加） */
    public final QuestStatus getQuestNoRecord(final int id) {
        return c.getPlayer().getQuestNoAdd(Quest.getInstance(id));
    }

    //---- /\ /\ /\ /\ /\ /\ /\  NOT TESTED  /\ /\ /\ /\ /\ /\ /\ /\ /\ ----

    public void openNpc(int npcid) {
        openNpc(npcid, null);
    }

/** 打开 NPC 对话脚本 */
    public void openNpc(int npcid, String script) {
        if (c.getCM() != null) {
            return;
        }

        c.removeClickedNPC();
        NPCScriptManager.getInstance().dispose(c);
        NPCScriptManager.getInstance().start(c, npcid, script, null);
    }

/** 获取任务状态 ID */
    public int getQuestStatus(int id) {
        return c.getPlayer().getQuest(Quest.getInstance(id)).getStatus().getId();
    }

    private QuestStatus.Status getQuestStat(int id) {
        return c.getPlayer().getQuest(Quest.getInstance(id)).getStatus();
    }

/** 判断任务是否已完成 */
    public boolean isQuestCompleted(int id) {
        try {
            return getQuestStat(id) == QuestStatus.Status.COMPLETED;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

/** 判断任务是否进行中（isQuestStarted 别名） */
    public boolean isQuestActive(int id) {
        return isQuestStarted(id);
    }

/** 判断任务是否已开始 */
    public boolean isQuestStarted(int id) {
        try {
            return getQuestStat(id) == QuestStatus.Status.STARTED;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

/** 设置任务进度字符串 */
    public void setQuestProgress(int id, String progress) {
        setQuestProgress(id, 0, progress);
    }

/** 设置任务进度字符串 */
    public void setQuestProgress(int id, int progress) {
        setQuestProgress(id, 0, "" + progress);
    }

/** 设置任务进度字符串 */
    public void setQuestProgress(int id, int infoNumber, int progress) {
        setQuestProgress(id, infoNumber, "" + progress);
    }

/** 设置任务进度字符串 */
    public void setQuestProgress(int id, int infoNumber, String progress) {
        c.getPlayer().setQuestProgress(id, infoNumber, progress);
    }

/** 获取任务进度字符串 */
    public String getQuestProgress(int id) {
        return getQuestProgress(id, 0);
    }

/** 获取任务进度字符串 */
    public String getQuestProgress(int id, int infoNumber) {
        QuestStatus qs = getPlayer().getQuest(Quest.getInstance(id));

        if (qs.getInfoNumber() == infoNumber && infoNumber > 0) {
            qs = getPlayer().getQuest(Quest.getInstance(infoNumber));
            infoNumber = 0;
        }

        if (qs != null) {
            return qs.getProgress(infoNumber);
        } else {
            return "";
        }
    }

/** 获取任务进度整数值 */
    public int getQuestProgressInt(int id) {
        try {
            return Integer.parseInt(getQuestProgress(id));
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

/** 获取任务进度整数值 */
    public int getQuestProgressInt(int id, int infoNumber) {
        try {
            return Integer.parseInt(getQuestProgress(id, infoNumber));
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

/** 重置任务全部进度 */
    public void resetAllQuestProgress(int id) {
        QuestStatus qs = getPlayer().getQuest(Quest.getInstance(id));
        if (qs != null) {
            qs.resetAllProgress();
            getPlayer().announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, false);
        }
    }

/** 重置任务指定 info 进度 */
    public void resetQuestProgress(int id, int infoNumber) {
        QuestStatus qs = getPlayer().getQuest(Quest.getInstance(id));
        if (qs != null) {
            qs.resetProgress(infoNumber);
            getPlayer().announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, false);
        }
    }

/** 强制开始任务 */
    public boolean forceStartQuest(int id) {
        return forceStartQuest(id, NpcId.MAPLE_ADMINISTRATOR);
    }

/** 强制开始任务 */
    public boolean forceStartQuest(int id, int npc) {
        return startQuest(id, npc);
    }

/** 强制完成任务 */
    public boolean forceCompleteQuest(int id) {
        return forceCompleteQuest(id, NpcId.MAPLE_ADMINISTRATOR);
    }

/** 强制完成任务 */
    public boolean forceCompleteQuest(int id, int npc) {
        return completeQuest(id, npc);
    }

/** 开始任务 */
    public boolean startQuest(short id) {
        return startQuest((int) id);
    }

/** 完成任务 */
    public boolean completeQuest(short id) {
        return completeQuest((int) id);
    }

/** 开始任务 */
    public boolean startQuest(int id) {
        return startQuest(id, NpcId.MAPLE_ADMINISTRATOR);
    }

/** 完成任务 */
    public boolean completeQuest(int id) {
        return completeQuest(id, NpcId.MAPLE_ADMINISTRATOR);
    }

/** 开始任务 */
    public boolean startQuest(short id, int npc) {
        return startQuest((int) id, npc);
    }

/** 完成任务 */
    public boolean completeQuest(short id, int npc) {
        return completeQuest((int) id, npc);
    }

/** 开始任务 */
    public boolean startQuest(int id, int npc) {
        try {
            return Quest.getInstance(id).forceStart(getPlayer(), npc);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return false;
        }
    }

/** 完成任务 */
    public boolean completeQuest(int id, int npc) {
        try {
            return Quest.getInstance(id).forceComplete(getPlayer(), npc);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return false;
        }
    }

/** 进化宠物并继承属性 */
    public Item evolvePet(byte slot, int afterId) {
        Pet evolved = null;
        Pet target;

        long period = DAYS.toMillis(90);    //refreshes expiration date: 90 days


        target = getPlayer().getPet(slot);
        if (target == null) {
            getPlayer().message("Pet could not be evolved...");
            return (null);
        }

        Item tmp = gainItem(afterId, (short) 1, false, true, period, target);
            
            /*
            evolved = Pet.loadFromDb(tmp.getItemId(), tmp.getPosition(), tmp.getPetId());
            
            evolved = tmp.getPet();
            if(evolved == null) {
                getPlayer().message("Pet structure non-existent for " + tmp.getItemId() + "...");
                return(null);
            }
            else if(tmp.getPetId() == -1) {
                getPlayer().message("Pet id -1");
                return(null);
            }
            
            getPlayer().addPet(evolved);
            
            getPlayer().getMap().broadcastMessage(c.getPlayer(), PacketCreator.showPet(c.getPlayer(), evolved, false, false), true);
            c.sendPacket(PacketCreator.petStatUpdate(c.getPlayer()));
            c.sendPacket(PacketCreator.enableActions());
            chr.getClient().getWorldServer().registerPetHunger(chr, chr.getPetIndex(evolved));
            */

        InventoryManipulator.removeFromSlot(c, InventoryType.CASH, target.getPosition(), (short) 1, false);

        return evolved;
    }

/** 给予或扣除物品 */
    public void gainItem(int id, short quantity) {
        gainItem(id, quantity, false, true);
    }

    public void gainItem(int id, short quantity, boolean show) {//this will fk randomStats equip :P
        gainItem(id, quantity, false, show);
    }

/** 给予或扣除物品 */
    public void gainItem(int id, boolean show) {
        gainItem(id, (short) 1, false, show);
    }

/** 给予或扣除物品 */
    public void gainItem(int id) {
        gainItem(id, (short) 1, false, true);
    }

/** 给予或扣除物品 */
    public Item gainItem(int id, short quantity, boolean randomStats, boolean showMessage) {
        return gainItem(id, quantity, randomStats, showMessage, -1);
    }

/** 给予或扣除物品 */
    public Item gainItem(int id, short quantity, boolean randomStats, boolean showMessage, long expires) {
        return gainItem(id, quantity, randomStats, showMessage, expires, null);
    }

/** 给予或扣除物品 */
    public Item gainItem(int id, short quantity, boolean randomStats, boolean showMessage, long expires, Pet from) {
        Item item = null;
        Pet evolved;
        int petId = -1;

        if (quantity >= 0) {
            if (ItemConstants.isPet(id)) {
                petId = Pet.createPet(id);

                if (from != null) {
                    evolved = Pet.loadFromDb(id, (short) 0, petId);

                    Point pos = getPlayer().getPosition();
                    pos.y -= 12;
                    evolved.setPos(pos);
                    evolved.setFh(getPlayer().getMap().getFootholds().findBelow(evolved.getPos()).getId());
                    evolved.setStance(0);
                    evolved.setSummoned(true);

                    evolved.setName(from.getName().compareTo(ItemInformationProvider.getInstance().getName(from.getItemId())) != 0 ? from.getName() : ItemInformationProvider.getInstance().getName(id));
                    evolved.setTameness(from.getTameness());
                    evolved.setFullness(from.getFullness());
                    evolved.setLevel(from.getLevel());
                    evolved.setExpiration(System.currentTimeMillis() + expires);
                    evolved.saveToDb();
                }

                //InventoryManipulator.addById(c, id, (short) 1, null, petId, expires == -1 ? -1 : System.currentTimeMillis() + expires);
            }

            ItemInformationProvider ii = ItemInformationProvider.getInstance();

            if (ItemConstants.getInventoryType(id).equals(InventoryType.EQUIP)) {
                item = ii.getEquipById(id);

                if (item != null) {
                    Equip it = (Equip) item;
                    if (ItemConstants.isAccessory(item.getItemId()) && it.getUpgradeSlots() <= 0) {
                        it.setUpgradeSlots(3);
                    }

                    if (GameConfig.getServerBoolean("use_enhanced_crafting") && c.getPlayer().isUseCS()) {
                        Equip eqp = (Equip) item;
                        if (!(c.getPlayer().isGM() && GameConfig.getServerBoolean("use_perfect_gm_scroll"))) {
                            eqp.setUpgradeSlots((byte) (eqp.getUpgradeSlots() + 1));
                        }
                        item = ItemInformationProvider.getInstance().scrollEquipWithId(item, ItemId.CHAOS_SCROll_60, true, ItemId.CHAOS_SCROll_60, c.getPlayer().isGM());
                    }
                }
            } else {
                item = new Item(id, (short) 0, quantity, petId);
            }

            if (expires >= 0) {
                item.setExpiration(System.currentTimeMillis() + expires);
            }

            if (!InventoryManipulator.checkSpace(c, id, quantity, "")) {
                c.getPlayer().dropMessage(1, "您的背包已满，请从" + ItemConstants.getInventoryType(id).name() + "栏移除一件物品。");
                return null;
            }
            if (ItemConstants.getInventoryType(id) == InventoryType.EQUIP) {
                if (randomStats) {
                    InventoryManipulator.addFromDrop(c, ii.randomizeStats((Equip) item), false, petId);
                } else {
                    InventoryManipulator.addFromDrop(c, item, false, petId);
                }
            } else {
                InventoryManipulator.addFromDrop(c, item, false, petId);
            }
        } else {
            InventoryManipulator.removeById(c, ItemConstants.getInventoryType(id), id, -quantity, true, false);
        }
        if (showMessage) {
            c.sendPacket(PacketCreator.getShowItemGain(id, quantity, true));
        }

        return item;
    }

/** 增减人气值 */
    public void gainFame(int delta) {
        getPlayer().gainFame(delta);
    }

/** 切换当前地图背景音乐 */
    public void changeMusic(String songName) {
        getPlayer().getMap().broadcastMessage(PacketCreator.musicChange(songName));
    }

/** 向玩家发送公告类型消息 */
    public void playerMessage(int type, String message) {
        c.sendPacket(PacketCreator.serverNotice(type, message));
    }

/** 向玩家发送聊天消息 */
    public void message(String message) {
        getPlayer().message(message);
    }

/** 向玩家发送 drop 消息 */
    public void dropMessage(int type, String message) {
        getPlayer().dropMessage(type, message);
    }

/** 向当前地图广播公告 */
    public void mapMessage(int type, String message) {
        getPlayer().getMap().broadcastMessage(PacketCreator.serverNotice(type, message));
    }

/** 播放地图特效 */
    public void mapEffect(String path) {
        c.sendPacket(PacketCreator.mapEffect(path));
    }

/** 播放地图音效 */
    public void mapSound(String path) {
        c.sendPacket(PacketCreator.mapSound(path));
    }

/** 播放战神职业引导动画 */
    public void displayAranIntro() {
        String intro = switch (c.getPlayer().getMapId()) {
            case MapId.ARAN_TUTO_1 -> "Effect/Direction1.img/aranTutorial/Scene0";
            case MapId.ARAN_TUTO_2 ->
                    "Effect/Direction1.img/aranTutorial/Scene1" + (c.getPlayer().getGender() == 0 ? "0" : "1");
            case MapId.ARAN_TUTO_3 ->
                    "Effect/Direction1.img/aranTutorial/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
            case MapId.ARAN_TUTO_4 -> "Effect/Direction1.img/aranTutorial/Scene3";
            case MapId.ARAN_POLEARM ->
                    "Effect/Direction1.img/aranTutorial/HandedPoleArm" + (c.getPlayer().getGender() == 0 ? "0" : "1");
            case MapId.ARAN_MAHA -> "Effect/Direction1.img/aranTutorial/Maha";
            default -> "";
        };
        showIntro(intro);
    }

/** 播放引导动画 */
    public void showIntro(String path) {
        c.sendPacket(PacketCreator.showIntro(path));
    }

/** 播放信息动画并恢复操作 */
    public void showInfo(String path) {
        c.sendPacket(PacketCreator.showInfo(path));
        c.sendPacket(PacketCreator.enableActions());
    }

/** 向公会广播消息 */
    public void guildMessage(int type, String message) {
        if (getGuild() != null) {
            getGuild().guildMessage(PacketCreator.serverNotice(type, message));
        }
    }

/** 获取当前角色所属公会 */
    public Guild getGuild() {
        try {
            return Server.getInstance().getGuild(getPlayer().getGuildId(), getPlayer().getWorld(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

/** 获取当前队伍 */
    public Party getParty() {
        return getPlayer().getParty();
    }

/** 是否为队伍队长（isPartyLeader 别名） */
    public boolean isLeader() {
        return isPartyLeader();
    }

/** 是否为公会会长 */
    public boolean isGuildLeader() {
        return getPlayer().isGuildLeader();
    }

/** 是否为队伍队长 */
    public boolean isPartyLeader() {
        if (getParty() == null) {
            return false;
        }

        return getParty().getLeaderId() == getPlayer().getId();
    }

/** 是否为事件实例队长 */
    public boolean isEventLeader() {
        return getEventInstance() != null && getPlayer().getId() == getEventInstance().getLeaderId();
    }

/** 向队伍成员发放或扣除物品 */
    public void givePartyItems(int id, short quantity, List<Character> party) {
        for (Character chr : party) {
            Client cl = chr.getClient();
            if (quantity >= 0) {
                InventoryManipulator.addById(cl, id, quantity);
            } else {
                InventoryManipulator.removeById(cl, ItemConstants.getInventoryType(id), id, -quantity, true, false);
            }
            cl.sendPacket(PacketCreator.getShowItemGain(id, quantity, true));
        }
    }

/** 移除队伍中所有 HPQ 种子物品 */
    public void removeHPQItems() {
        int[] items = {ItemId.GREEN_PRIMROSE_SEED, ItemId.PURPLE_PRIMROSE_SEED, ItemId.PINK_PRIMROSE_SEED,
                ItemId.BROWN_PRIMROSE_SEED, ItemId.YELLOW_PRIMROSE_SEED, ItemId.BLUE_PRIMROSE_SEED};
        for (int item : items) {
            removePartyItems(item);
        }
    }

/** 移除队伍成员指定物品 */
    public void removePartyItems(int id) {
        if (getParty() == null) {
            removeAll(id);
            return;
        }
        for (PartyCharacter mpc : getParty().getMembers()) {
            if (mpc == null || !mpc.isOnline()) {
                continue;
            }

            Character chr = mpc.getPlayer();
            if (chr != null && chr.getClient() != null) {
                removeAll(id, chr.getClient());
            }
        }
    }

/** 给予单个角色经验 */
    public void giveCharacterExp(int amount, Character chr) {
        chr.gainExp(NumberTool.floatToInt(amount * chr.getExpRate()), true, true);
    }

/** 向队伍成员发放经验 */
    public void givePartyExp(int amount, List<Character> party) {
        for (Character chr : party) {
            giveCharacterExp(amount, chr);
        }
    }

/** 向队伍成员发放经验 */
    public void givePartyExp(String PQ) {
        givePartyExp(PQ, true);
    }

/** 向队伍成员发放经验 */
    public void givePartyExp(String PQ, boolean instance) {
        //1 player  =  +0% bonus (100)
        //2 players =  +0% bonus (100)
        //3 players =  +0% bonus (100)
        //4 players = +10% bonus (110)
        //5 players = +20% bonus (120)
        //6 players = +30% bonus (130)
        Party party = getPlayer().getParty();
        int size = party.getMembers().size();

        if (instance) {
            for (PartyCharacter member : party.getMembers()) {
                if (member == null || !member.isOnline()) {
                    size--;
                } else {
                    Character chr = member.getPlayer();
                    if (chr != null && chr.getEventInstance() == null) {
                        size--;
                    }
                }
            }
        }

        int bonus = size < 4 ? 100 : 70 + (size * 10);
        for (PartyCharacter member : party.getMembers()) {
            if (member == null || !member.isOnline()) {
                continue;
            }
            Character player = member.getPlayer();
            if (player == null) {
                continue;
            }
            if (instance && player.getEventInstance() == null) {
                continue; // They aren't in the instance, don't give EXP.
            }
            int base = PartyQuest.getExp(PQ, player.getLevel());
            int exp = base * bonus / 100;
            if (GameConfig.getServerFloat("pq_bonus_exp_rate") > 0) {
                player.gainExp((int) (exp * GameConfig.getServerFloat("pq_bonus_exp_rate")), true, true);
            } else {
                player.gainExp(exp, true, true);
            }
        }
    }

/** 从队伍成员背包移除指定物品 */
    public void removeFromParty(int id, List<Character> party) {
        for (Character chr : party) {
            InventoryType type = ItemConstants.getInventoryType(id);
            Inventory iv = chr.getInventory(type);
            int possesed = iv.countById(id);
            if (possesed > 0) {
                InventoryManipulator.removeById(c, ItemConstants.getInventoryType(id), id, possesed, true, false);
                chr.sendPacket(PacketCreator.getShowItemGain(id, (short) -possesed, true));
            }
        }
    }

/** 移除角色全部指定物品 */
    public void removeAll(int id) {
        removeAll(id, c);
    }

/** 移除角色全部指定物品 */
    public void removeAll(int id, Client cl) {
        InventoryType invType = ItemConstants.getInventoryType(id);
        int possessed = cl.getPlayer().getInventory(invType).countById(id);
        if (possessed > 0) {
            InventoryManipulator.removeById(cl, ItemConstants.getInventoryType(id), id, possessed, true, false);
            cl.sendPacket(PacketCreator.getShowItemGain(id, (short) -possessed, true));
        }

        if (invType == InventoryType.EQUIP) {
            if (cl.getPlayer().getInventory(InventoryType.EQUIPPED).countById(id) > 0) {
                InventoryManipulator.removeById(cl, InventoryType.EQUIPPED, id, 1, true, false);
                cl.sendPacket(PacketCreator.getShowItemGain(id, (short) -1, true));
            }
        }
    }

/** 清空指定类型背包 */
    public void removeAllByInventory(int invType) {
        Inventory inv = getInventory(invType);
        for (Item item : new ArrayList<>(inv.list())) {
            InventoryManipulator.removeFromSlot(c, inv.getType(), item.getPosition(), item.getQuantity(), false);
        }
    }

/** 移除指定背包槽位物品 */
    public void removeAllByInventorySlot(int invType, short slot) {
        Inventory inv = getInventory(invType);
        Item item = inv.getItem(slot);
        if (item != null) {
            InventoryManipulator.removeFromSlot(c, inv.getType(), item.getPosition(), item.getQuantity(), false);
        }
    }

/** 获取当前地图 ID */
    public int getMapId() {
        return c.getPlayer().getMap().getId();
    }

/** 获取指定地图在线玩家数 */
    public int getPlayerCount(int mapid) {
        return c.getChannelServer().getMapFactory().getMap(mapid).getCharacters().size();
    }

/** 显示屏幕提示文字 */
    public void showInstruction(String msg, int width, int height) {
        c.sendPacket(PacketCreator.sendHint(msg, width, height));
        c.sendPacket(PacketCreator.enableActions());
    }

/** 禁用小地图 */
    public void disableMinimap() {
        c.sendPacket(PacketCreator.disableMinimap());
    }

/** 检查地图上指定反应堆是否均为某状态 */
    public boolean isAllReactorState(final int reactorId, final int state) {
        return c.getPlayer().getMap().isAllReactorState(reactorId, state);
    }

/** 重置地图（反应堆、怪物、掉落物） */
    public void resetMap(int mapid) {
        getMap(mapid).resetReactors();
        getMap(mapid).killAllMonsters();
        for (MapObject i : getMap(mapid).getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.ITEM))) {
            getMap(mapid).removeMapObject(i);
            getMap(mapid).broadcastMessage(PacketCreator.removeItemFromMap(i.getObjectId(), 0, c.getPlayer().getId()));
        }
    }

/** 使用消耗品效果 */
    public void useItem(int id) {
        ItemInformationProvider.getInstance().getItemEffect(id).applyTo(c.getPlayer());
        c.sendPacket(PacketCreator.getItemMessage(id));//Useful shet :3
    }

/** 取消消耗品效果 */
    public void cancelItem(final int id) {
        getPlayer().cancelEffect(ItemInformationProvider.getInstance().getItemEffect(id), false, -1);
    }

/** 教授或更新技能等级 */
    public void teachSkill(int skillid, byte level, byte masterLevel, long expiration) {
        teachSkill(skillid, level, masterLevel, expiration, false);
    }

/** 教授或更新技能等级 */
    public void teachSkill(int skillid, byte level, byte masterLevel, long expiration, boolean force) {
        Skill skill = SkillFactory.getSkill(skillid);
        SkillEntry skillEntry = getPlayer().getSkills().get(skill);
        if (skillEntry != null) {
            if (!force && level > -1) {
                getPlayer().changeSkillLevel(skill, (byte) Math.max(skillEntry.skillLevel, level), Math.max(skillEntry.masterLevel, masterLevel), expiration == -1 ? -1 : Math.max(skillEntry.expiration, expiration));
                return;
            }
        } else if (GameConstants.isAranSkills(skillid)) {
            c.sendPacket(PacketCreator.showInfo("Effect/BasicEff.img/AranGetSkill"));
        }

        getPlayer().changeSkillLevel(skill, level, masterLevel, expiration);
    }

/** 卸下已装备栏指定槽位装备 */
    public void removeEquipFromSlot(short slot) {
        Item tempItem = c.getPlayer().getInventory(InventoryType.EQUIPPED).getItem(slot);
        InventoryManipulator.removeFromSlot(c, InventoryType.EQUIPPED, slot, tempItem.getQuantity(), false, false);
    }

/** 获得并直接装备到指定槽位 */
    public void gainAndEquip(int itemid, short slot) {
        final Item old = c.getPlayer().getInventory(InventoryType.EQUIPPED).getItem(slot);
        if (old != null) {
            InventoryManipulator.removeFromSlot(c, InventoryType.EQUIPPED, slot, old.getQuantity(), false, false);
        }
        final Item newItem = ItemInformationProvider.getInstance().getEquipById(itemid);
        newItem.setPosition(slot);
        c.getPlayer().getInventory(InventoryType.EQUIPPED).addItemFromDB(newItem);
        c.sendPacket(PacketCreator.modifyInventory(false, Collections.singletonList(new ModifyInventory(0, newItem))));
    }

/** 在地图上生成 NPC */
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

/** 在当前地图生成怪物 */
    public void spawnMonster(int id, int x, int y) {
        Monster monster = LifeFactory.getMonster(id);
        monster.setPosition(new Point(x, y));
        getPlayer().getMap().spawnMonster(monster);
    }

/** 获取怪物模板对象 */
    public Monster getMonsterLifeFactory(int mid) {
        return LifeFactory.getMonster(mid);
    }

/** 显示引导精灵 */
    public void spawnGuide() {
        c.sendPacket(PacketCreator.spawnGuide(true));
    }

/** 隐藏引导精灵 */
    public void removeGuide() {
        c.sendPacket(PacketCreator.spawnGuide(false));
    }

/** 显示教程 UI */
    public void displayGuide(int num) {
        c.sendPacket(PacketCreator.showInfo("UI/tutorial.img/" + num));
    }

/** 武陵道场上升一层 */
    public void goDojoUp() {
        c.sendPacket(PacketCreator.dojoWarpUp());
    }

/** 重置角色道场能量 */
    public void resetDojoEnergy() {
        c.getPlayer().setDojoEnergy(0);
    }

/** 重置同地图队伍成员道场能量 */
    public void resetPartyDojoEnergy() {
        for (Character pchr : c.getPlayer().getPartyMembersOnSameMap()) {
            pchr.setDojoEnergy(0);
        }
    }

/** 恢复玩家操作 */
    public void enableActions() {
        c.sendPacket(PacketCreator.enableActions());
    }

/** 播放客户端特效 */
    public void showEffect(String effect) {
        c.sendPacket(PacketCreator.showEffect(effect));
    }

/** 同步道场能量 UI */
    public void dojoEnergy() {
        c.sendPacket(PacketCreator.getEnergy("energy", getPlayer().getDojoEnergy()));
    }

/** 引导精灵说话 */
    public void talkGuide(String message) {
        c.sendPacket(PacketCreator.talkGuide(message));
    }

/** 显示引导提示编号 */
    public void guideHint(int hint) {
        c.sendPacket(PacketCreator.guideHint(hint));
    }

/** 更新区域探索信息 */
    public void updateAreaInfo(Short area, String info) {
        c.getPlayer().updateAreaInfo(area, info);
        c.sendPacket(PacketCreator.enableActions());//idk, nexon does the same :P
    }

/** 检查区域信息是否包含指定内容 */
    public boolean containsAreaInfo(short area, String info) {
        return c.getPlayer().containsAreaInfo(area, info);
    }

/** 显示称号获得消息 */
    public void earnTitle(String msg) {
        c.sendPacket(PacketCreator.earnTitleMessage(msg));
    }

/** 显示信息文本 */
    public void showInfoText(String msg) {
        c.sendPacket(PacketCreator.showInfoText(msg));
    }

/** 打开客户端 UI 面板 */
    public void openUI(byte ui) {
        c.sendPacket(PacketCreator.openUI(ui));
    }

/** 锁定玩家 UI 与操作 */
    public void lockUI() {
        c.sendPacket(PacketCreator.disableUI(true));
        c.sendPacket(PacketCreator.lockUI(true));
    }

/** 解锁玩家 UI 与操作 */
    public void unlockUI() {
        c.sendPacket(PacketCreator.disableUI(false));
        c.sendPacket(PacketCreator.lockUI(false));
    }

/** 播放环境音效 */
    public void playSound(String sound) {
        getPlayer().getMap().broadcastMessage(PacketCreator.environmentChange(sound, 4));
    }

/** 广播环境变化（背景层等） */
    public void environmentChange(String env, int mode) {
        getPlayer().getMap().broadcastMessage(PacketCreator.environmentChange(env, mode));
    }

/** 格式化数字为千分位字符串 */
    public String numberWithCommas(int number) {
        return GameConstants.numberWithCommas(number);
    }

/** 获取金字塔组队任务实例 */
    public Pyramid getPyramid() {
        return (Pyramid) getPlayer().getPartyQuest();
    }

/** 创建远征队 */
    public int createExpedition(ExpeditionType type) {
        return createExpedition(type, false, 0, 0);
    }

/** 创建远征队 */
    public int createExpedition(ExpeditionType type, boolean silent, int minPlayers, int maxPlayers) {
        Character player = getPlayer();
        Expedition exped = new Expedition(player, type, silent, minPlayers, maxPlayers);

        int channel = player.getMap().getChannelServer().getId();
        if (!ExpeditionBossLog.attemptBoss(player.getId(), channel, exped, false)) {    // thanks Conrad for noticing missing expeditions entry limit
            return 1;
        }

        if (exped.addChannelExpedition(player.getClient().getChannelServer())) {
            return 0;
        } else {
            return -1;
        }
    }

/** 结束并销毁远征队 */
    public void endExpedition(Expedition exped) {
        exped.dispose(true);
        exped.removeChannelExpedition(getPlayer().getClient().getChannelServer());
    }

/** 获取频道内指定类型远征队 */
    public Expedition getExpedition(ExpeditionType type) {
        return getPlayer().getClient().getChannelServer().getExpedition(type);
    }

/** 获取远征队成员名称列表 */
    public String getExpeditionMemberNames(ExpeditionType type) {
        String members = "";
        Expedition exped = getExpedition(type);
        for (String memberName : exped.getMembers().values()) {
            members += "" + memberName + ", ";
        }
        return members;
    }

/** 当前角色是否为远征队队长 */
    public boolean isLeaderExpedition(ExpeditionType type) {
        Expedition exped = getExpedition(type);
        return exped.isLeader(getPlayer());
    }

/** 获取监禁剩余时间（毫秒） */
    public long getJailTimeLeft() {
        return getPlayer().getJailExpirationTimeLeft();
    }

/** 获取已过期宠物列表 */
    public List<Pet> getDriedPets() {
        List<Pet> list = new LinkedList<>();

        long curTime = System.currentTimeMillis();
        for (Item it : getPlayer().getInventory(InventoryType.CASH).list()) {
            if (ItemConstants.isPet(it.getItemId()) && it.getExpiration() < curTime) {
                Pet pet = it.getPet();
                if (pet != null) {
                    list.add(pet);
                }
            }
        }

        return list;
    }

/** 获取未领取的结婚礼物 */
    public List<Item> getUnclaimedMarriageGifts() {
        return Marriage.loadGiftItemsFromDb(this.getClient(), this.getPlayer().getId());
    }

/** 启动迷你地下城实例 */
    public boolean startDungeonInstance(int dungeonid) {
        return c.getChannelServer().addMiniDungeon(dungeonid);
    }

/** 检查是否满足一转属性要求 */
    public boolean canGetFirstJob(int jobType) {
        if (GameConfig.getServerBoolean("use_auto_assign_starters_ap")) {
            return true;
        }

        Character chr = this.getPlayer();

        switch (jobType) {
            case 1:
                return chr.getStr() >= 35;

            case 2:
                return chr.getInt() >= 20;

            case 3:
            case 4:
                return chr.getDex() >= 25;

            case 5:
                return chr.getDex() >= 20;

            default:
                return true;
        }
    }

/** 获取一转所需属性描述文本 */
    public String getFirstJobStatRequirement(int jobType) {
        switch (jobType) {
            case 1:
                return "力量 " + 35;

            case 2:
                return "智力 " + 20;

            case 3:
            case 4:
                return "敏捷 " + 25;

            case 5:
                return "敏捷 " + 20;
        }

        return null;
    }

/** 显示 NPC 静态对话（无脚本交互） */
    public void npcTalk(int npcid, String message) {
        c.sendPacket(PacketCreator.getNPCTalk(npcid, (byte) 0, message, "00 00", (byte) 0));
    }

/** 获取服务器当前时间戳 */
    public long getCurrentTime() {
        return Server.getInstance().getCurrentTime();
    }

/** 削弱区域 Boss（封印技能、降低回避） */
    public void weakenAreaBoss(int monsterId, String message) {
        MapleMap map = c.getPlayer().getMap();
        Monster monster = map.getMonsterById(monsterId);
        if (monster == null) {
            return;
        }

        applySealSkill(monster);
        applyReduceAvoid(monster);
        sendBlueNotice(map, message);
    }

    private void applySealSkill(Monster monster) {
        MobSkill sealSkill = MobSkillFactory.getMobSkillOrThrow(MobSkillType.SEAL_SKILL, 1);
        sealSkill.applyEffect(monster);
    }

    private void applyReduceAvoid(Monster monster) {
        MobSkill reduceAvoidSkill = MobSkillFactory.getMobSkillOrThrow(MobSkillType.EVA, 2);
        reduceAvoidSkill.applyEffect(monster);
    }

    private void sendBlueNotice(MapleMap map, String message) {
        map.dropMessage(6, message);
    }

/////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取角色扩展表某字段的值
     *
     * @param extendName 扩展字段名
     * @return 扩展字段值
     */
    public String getCharacterExtendValue(String extendName) {
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(String.valueOf(getPlayer().getId()), ExtendType.CHARACTER_EXTEND.getType(), extendName);
        return extendValueDO == null ? null : extendValueDO.getExtendValue();
    }

    /**
     * 获取每日/每周角色扩展表某字段的值
     *
     * @param extendName 扩展字段名
     * @param isDaily    是否是每日，否则为每周
     * @return 扩展字段值
     */
    public String getCharacterExtendValue(String extendName, boolean isDaily) {
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(String.valueOf(getPlayer().getId()),
                isDaily ? ExtendType.CHARACTER_EXTEND_DAILY.getType() : ExtendType.CHARACTER_EXTEND_WEEKLY.getType(),
                extendName);
        return extendValueDO == null ? null : extendValueDO.getExtendValue();
    }

    /**
     * 获取账号扩展表某字段的值
     *
     * @param extendName 扩展字段名
     * @return 扩展字段值
     */
    public String getAccountExtendValue(String extendName) {
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(String.valueOf(getPlayer().getAccountId()), ExtendType.ACCOUNT_EXTEND.getType(), extendName);
        return extendValueDO == null ? null : extendValueDO.getExtendValue();
    }

    /**
     * 获取每日/每周账号扩展表某字段的值
     *
     * @param extendName 扩展字段名
     * @param isDaily    是否是每日，否则为每周
     * @return 扩展字段值
     */
    public String getAccountExtendValue(String extendName, boolean isDaily) {
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(String.valueOf(getPlayer().getAccountId()),
                isDaily ? ExtendType.ACCOUNT_EXTEND_DAILY.getType() : ExtendType.ACCOUNT_EXTEND_WEEKLY.getType(),
                extendName);
        return extendValueDO == null ? null : extendValueDO.getExtendValue();
    }
///////////////////////////////////////////////////////////////////////////////////////////////////

    /***
     * 永久保存或者更新角色扩展表指定的值
     * @param extendName
     * @param extendValue
     */
    public void saveOrUpdateCharacterExtendValue(String extendName, String extendValue) {
        ExtendUtil.saveOrUpdateExtendValue(String.valueOf(getPlayer().getId()), ExtendType.CHARACTER_EXTEND.getType(), extendName, extendValue);
    }

    /***
     * 保存每日/每周账号扩展表某字段的值
     * @param extendName
     * @param extendValue
     * @param isDaily 是否为每日刷新，否则为周刷新
     */
    public void saveOrUpdateCharacterExtendValue(String extendName, String extendValue, boolean isDaily) {
        ExtendUtil.saveOrUpdateExtendValue(String.valueOf(getPlayer().getId()), isDaily ? ExtendType.CHARACTER_EXTEND_DAILY.getType() : ExtendType.CHARACTER_EXTEND_WEEKLY.getType(),
                extendName, extendValue);
    }

/** 保存或更新账号扩展字段 */
    public void saveOrUpdateAccountExtendValue(String extendName, String extendValue) {
        ExtendUtil.saveOrUpdateExtendValue(String.valueOf(getPlayer().getAccountId()), ExtendType.ACCOUNT_EXTEND.getType(), extendName, extendValue);
    }

/** 保存或更新账号扩展字段 */
    public void saveOrUpdateAccountExtendValue(String extendName, String extendValue, boolean isDaily) {
        ExtendUtil.saveOrUpdateExtendValue(String.valueOf(getPlayer().getAccountId()), isDaily ? ExtendType.ACCOUNT_EXTEND_DAILY.getType() : ExtendType.ACCOUNT_EXTEND_WEEKLY.getType(),
                extendName, extendValue);
    }

/** 将装备放入背包 */
    public void gainEquip(Equip equip) {
        if (!InventoryManipulator.checkSpace(getClient(), equip.getItemId(), 1, equip.getOwner())) {
            message(I18nUtil.getMessage("AbstractPlayerInteraction.gainEquip.message2", InventoryType.EQUIP.getName()));
        }
        InventoryManipulator.addFromDrop(getClient(), equip, false);
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////
    /***
     * 获取账户在线时间
     * @return 返回当前账户角色在线时间，单位分钟
     */
    public int getOnlineTime()
    {
        return getPlayer().getCurrentOnlineTime();
    }





}