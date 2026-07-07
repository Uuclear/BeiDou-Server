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
package org.gms.server;

import org.gms.client.Client;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ItemFactory;
import org.gms.constants.game.GameConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 角色仓库，管理仓库存取与扩容。
 */
public class Storage {
    private static final Logger log = LoggerFactory.getLogger(Storage.class);
    private static final Map<Integer, Integer> trunkGetCache = new HashMap<>();
    private static final Map<Integer, Integer> trunkPutCache = new HashMap<>();

    private final int id;
    private int currentNpcid;
    private int meso;
    private byte slots;
    private final Map<InventoryType, List<Item>> typeItems = new HashMap<>();
    private List<Item> items = new LinkedList<>();
    private final Lock lock = new ReentrantLock(true);

    private Storage(int id, byte slots, int meso) {
        this.id = id;
        this.slots = slots;
        this.meso = meso;
    }

    private static Storage create(int id, int world) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO storages (accountid, world, slots, meso) VALUES (?, ?, 4, 0)")) {
            ps.setInt(1, id);
            ps.setInt(2, world);
            ps.executeUpdate();
        }

        return loadOrCreateFromDB(id, world);
    }

    /**
     * 加载或、Create、来自、D、B。
     * @param id ID
     * @param world world
     * @return Storage 类型结果
     */
    public static Storage loadOrCreateFromDB(int id, int world) {
        Storage ret;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT storageid, slots, meso FROM storages WHERE accountid = ? AND world = ?")) {
            ps.setInt(1, id);
            ps.setInt(2, world);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ret = new Storage(rs.getInt("storageid"), (byte) rs.getInt("slots"), rs.getInt("meso"));
                    for (Pair<Item, InventoryType> item : ItemFactory.STORAGE.loadItems(ret.id, false)) {
                        ret.items.add(item.getLeft());
                    }
                } else {
                    ret = create(id, world);
                }
            }

            return ret;
        } catch (SQLException ex) { // exceptions leading to deploy null storages found thanks to Jefe
            log.error("SQL error occurred when trying to load storage for accId {}, world {}", id, GameConstants.WORLD_NAMES[world], ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 获取Slots。
     * @return byte 类型结果
     */
    public byte getSlots() {
        return slots;
    }

    /**
     * 判断是否可以Gain、Slots。
     * @param slots slots
     * @return boolean 类型结果
     */
    public boolean canGainSlots(int slots) {
        slots += this.slots;
        return slots <= 48;
    }

    /**
     * 执行 gain、Slots 操作。
     * @param slots slots
     * @return boolean 类型结果
     */
    public boolean gainSlots(int slots) {
        lock.lock();
        try {
            if (canGainSlots(slots)) {
                slots += this.slots;
                this.slots = (byte) slots;
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行 save、到、D、B 操作。
     * @param con con
     */
    public void saveToDB(Connection con) {
        try {
            try (PreparedStatement ps = con.prepareStatement("UPDATE storages SET slots = ?, meso = ? WHERE storageid = ?")) {
                ps.setInt(1, slots);
                ps.setInt(2, meso);
                ps.setInt(3, id);
                ps.executeUpdate();
            }
            List<Pair<Item, InventoryType>> itemsWithType = new ArrayList<>();

            List<Item> list = getItems();
            for (Item item : list) {
                itemsWithType.add(new Pair<>(item, item.getInventoryType()));
            }

            ItemFactory.STORAGE.saveItems(itemsWithType, id, con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取物品。
     * @param slot slot
     * @return Item 类型结果
     */
    public Item getItem(byte slot) {
        lock.lock();
        try {
            return items.get(slot);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行 take、Out 操作。
     * @param item item
     * @return boolean 类型结果
     */
    public boolean takeOut(Item item) {
        lock.lock();
        try {
            boolean ret = items.remove(item);

            InventoryType type = item.getInventoryType();
            typeItems.put(type, new ArrayList<>(filterItems(type)));

            return ret;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行 store 操作。
     * @param item item
     * @return boolean 类型结果
     */
    public boolean store(Item item) {
        lock.lock();
        try {
            if (isFull()) { // thanks Optimist for noticing unrestricted amount of insertions here
                return false;
            }

            items.add(item);

            InventoryType type = item.getInventoryType();
            typeItems.put(type, new ArrayList<>(filterItems(type)));

            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取物品。
     * @return List<Item> 类型结果
     */
    public List<Item> getItems() {
        lock.lock();
        try {
            return Collections.unmodifiableList(items);
        } finally {
            lock.unlock();
        }
    }

    private List<Item> filterItems(InventoryType type) {
        List<Item> storageItems = getItems();
        List<Item> ret = new LinkedList<>();

        for (Item item : storageItems) {
            if (item.getInventoryType() == type) {
                ret.add(item);
            }
        }
        return ret;
    }

    /**
     * 获取Slot。
     * @param type 类型
     * @param slot slot
     * @return byte 类型结果
     */
    public byte getSlot(InventoryType type, byte slot) {
        lock.lock();
        try {
            byte ret = 0;
            List<Item> storageItems = getItems();
            for (Item item : storageItems) {
                if (item == typeItems.get(type).get(slot)) {
                    return ret;
                }
                ret++;
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行 send、Storage 操作。
     * @param c c
     * @param npcId NPC ID
     */
    public void sendStorage(Client c, int npcId) {
        if (c.getPlayer().getLevel() < 15) {
            c.getPlayer().dropMessage(1, "15级以后才可以使用仓库服务");
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        lock.lock();
        try {
            items.sort((o1, o2) -> {
                if (o1.getInventoryType().getType() < o2.getInventoryType().getType()) {
                    return -1;
                } else if (o1.getInventoryType() == o2.getInventoryType()) {
                    return 0;
                }
                return 1;
            });

            List<Item> storageItems = getItems();
            for (InventoryType type : InventoryType.values()) {
                typeItems.put(type, new ArrayList<>(storageItems));
            }

            currentNpcid = npcId;
            c.sendPacket(PacketCreator.getStorage(npcId, slots, storageItems, meso));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行 send、Stored 操作。
     * @param c c
     * @param type 类型
     */
    public void sendStored(Client c, InventoryType type) {
        lock.lock();
        try {
            c.sendPacket(PacketCreator.storeStorage(slots, type, typeItems.get(type)));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行 send、Taken、Out 操作。
     * @param c c
     * @param type 类型
     */
    public void sendTakenOut(Client c, InventoryType type) {
        lock.lock();
        try {
            c.sendPacket(PacketCreator.takeOutStorage(slots, type, typeItems.get(type)));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行 arrange、物品 操作。
     * @param c c
     */
    public void arrangeItems(Client c) {
        lock.lock();
        try {
            StorageInventory msi = new StorageInventory(c, items);
            msi.mergeItems();
            items = msi.sortItems();

            for (InventoryType type : InventoryType.values()) {
                typeItems.put(type, new ArrayList<>(items));
            }

            c.sendPacket(PacketCreator.arrangeStorage(slots, items));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取金币。
     * @return int 类型结果
     */
    public int getMeso() {
        return meso;
    }

    /**
     * 设置金币。
     * @param meso 金币数量
     */
    public void setMeso(int meso) {
        if (meso < 0) {
            throw new RuntimeException();
        }
        this.meso = meso;
    }

    /**
     * 执行 send、金币 操作。
     * @param c c
     */
    public void sendMeso(Client c) {
        c.sendPacket(PacketCreator.mesoStorage(slots, meso));
    }

    /**
     * 获取Store、Fee。
     * @return int 类型结果
     */
    public int getStoreFee() {  // thanks to GabrielSin
        int npcId = currentNpcid;
        Integer fee = trunkPutCache.get(npcId);
        if (fee == null) {
            fee = 100;

            DataProvider npc = DataProviderFactory.getDataProvider(WZFiles.NPC);
            Data npcData = npc.getData(npcId + ".img");
            if (npcData != null) {
                fee = DataTool.getIntConvert("info/trunkPut", npcData, 100);
            }

            trunkPutCache.put(npcId, fee);
        }

        return fee;
    }

    /**
     * 获取Take、Out、Fee。
     * @return int 类型结果
     */
    public int getTakeOutFee() {
        int npcId = currentNpcid;
        Integer fee = trunkGetCache.get(npcId);
        if (fee == null) {
            fee = 0;

            DataProvider npc = DataProviderFactory.getDataProvider(WZFiles.NPC);
            Data npcData = npc.getData(npcId + ".img");
            if (npcData != null) {
                fee = DataTool.getIntConvert("info/trunkGet", npcData, 0);
            }

            trunkGetCache.put(npcId, fee);
        }

        return fee;
    }

    /**
     * 判断是否为Full。
     * @return boolean 类型结果
     */
    public boolean isFull() {
        lock.lock();
        try {
            return items.size() >= slots;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行 close 操作。
     */
    public void close() {
        lock.lock();
        try {
            typeItems.clear();
        } finally {
            lock.unlock();
        }
    }

}
