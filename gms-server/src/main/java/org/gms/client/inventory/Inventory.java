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
package org.gms.client.inventory;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.inventory.ItemConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.ItemInformationProvider;
import org.gms.server.ThreadManager;
import org.gms.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 背包类
 * 管理角色的单个背包（装备/消耗/设置/其他/现金/已装备）
 * 使用LinkedHashMap按槽位顺序存储物品，支持线程安全的添加、移除、移动、查找等操作
 * 包含背包容量检查、物品堆叠、槽位管理等核心功能
 *
 * @author Matze, Ronan
 */
public class Inventory implements Iterable<Item> {
    private static final Logger log = LoggerFactory.getLogger(Inventory.class);

    /** 物品存储映射：槽位 -> 物品 */
    protected final Map<Short, Item> inventory;
    /** 背包类型 */
    protected final InventoryType type;
    /** 并发锁（公平锁） */
    protected final Lock lock = new ReentrantLock(true);

    /** 背包所有者（角色） */
    protected Character owner;
    /** 槽位上限 */
    protected byte slotLimit;
    /** 是否已检查标记 */
    protected boolean checked = false;

    /**
     * 构造函数
     * @param mc 拥有者角色
     * @param type 背包类型
     * @param slotLimit 初始槽位数
     */
    public Inventory(Character mc, InventoryType type, byte slotLimit) {
        this.owner = mc;
        this.inventory = new LinkedHashMap<>();
        this.type = type;
        this.slotLimit = slotLimit;
    }

    /**
     * 判断是否为可扩展背包
     * @return 是否可扩展
     */
    public boolean isExtendableInventory() {
        return !(type.equals(InventoryType.UNDEFINED) || type.equals(InventoryType.EQUIPPED) || type.equals(InventoryType.CASH));
    }

    /**
     * 判断是否为装备背包（包括已装备栏）
     * @return 是否为装备背包
     */
    public boolean isEquipInventory() {
        return type.equals(InventoryType.EQUIP) || type.equals(InventoryType.EQUIPPED);
    }

    /**
     * 获取槽位上限
     * @return 槽位上限
     */
    public byte getSlotLimit() {
        lock.lock();
        try {
            return slotLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置新的槽位上限
     * 如果新上限小于当前，会移除超出槽位的物品
     * @param newLimit 新槽位数
     */
    public void setSlotLimit(int newLimit) {
        lock.lock();
        try {
            if (newLimit < slotLimit) {
                List<Short> toRemove = new LinkedList<>();
                for (Item it : list()) {
                    if (it.getPosition() > newLimit) {
                        toRemove.add(it.getPosition());
                    }
                }

                for (Short slot : toRemove) {
                    removeSlot(slot);
                }
            }

            slotLimit = (byte) newLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 列出所有物品（线程安全的副本）
     * @return 物品列表
     */
    public Collection<Item> list() {
        lock.lock();
        try {
            return new ArrayList<>(inventory.values());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 根据物品ID查找第一个匹配的物品
     * @param itemId 物品ID
     * @return 找到的物品，未找到返回null
     */
    public Item findById(int itemId) {
        for (Item item : list()) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据物品名称查找
     * @param name 物品名称
     * @return 找到的物品，未找到返回null
     */
    public Item findByName(String name) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        for (Item item : list()) {
            String itemName = ii.getName(item.getItemId());
            if (itemName == null) {
                log.error("[CRITICAL] Item {} has no name", item.getItemId());
                continue;
            }

            if (name.compareToIgnoreCase(itemName) == 0) {
                return item;
            }
        }
        return null;
    }

    /**
     * 统计指定ID物品的总数量
     * @param itemId 物品ID
     * @return 总数量
     */
    public int countById(int itemId) {
        int qty = 0;
        for (Item item : list()) {
            if (item.getItemId() == itemId) {
                qty += item.getQuantity();
            }
        }
        return qty;
    }

    /**
     * 统计无所有者标记的指定ID物品数量
     * @param itemId 物品ID
     * @return 数量
     */
    public int countNotOwnedById(int itemId) {
        int qty = 0;
        for (Item item : list()) {
            if (item.getItemId() == itemId && item.getOwner().equals("")) {
                qty += item.getQuantity();
            }
        }
        return qty;
    }

    /**
     * 计算存放指定数量物品需要的空闲槽位数
     * @param itemId 物品ID
     * @param required 需要存放的数量
     * @return 需要的槽位数，空间不足返回-1
     */
    public int freeSlotCountById(int itemId, int required) {
        List<Item> itemList = listById(itemId);
        int openSlot = 0;

        if (!ItemConstants.isRechargeable(itemId)) {
            for (Item item : itemList) {
                required -= item.getQuantity();

                if (required >= 0) {
                    openSlot++;
                    if (required == 0) {
                        return openSlot;
                    }
                } else {
                    return openSlot;
                }
            }
        } else {
            for (Item item : itemList) {
                required -= 1;

                if (required >= 0) {
                    openSlot++;
                    if (required == 0) {
                        return openSlot;
                    }
                } else {
                    return openSlot;
                }
            }
        }

        return -1;
    }

    /**
     * 获取指定ID的所有物品列表（按槽位排序）
     * @param itemId 物品ID
     * @return 物品列表
     */
    public List<Item> listById(int itemId) {
        List<Item> ret = new ArrayList<>();
        for (Item item : list()) {
            if (item.getItemId() == itemId) {
                ret.add(item);
            }
        }

        if (ret.size() > 1) {
            ret.sort((i1, i2) -> i1.getPosition() - i2.getPosition());
        }

        return ret;
    }

    /**
     * 获取指定ID的所有物品链表（按槽位排序）
     * @param itemId 物品ID
     * @return 物品链表
     */
    public List<Item> linkedListById(int itemId) {
        List<Item> ret = new LinkedList<>();
        for (Item item : list()) {
            if (item.getItemId() == itemId) {
                ret.add(item);
            }
        }

        if (ret.size() > 1) {
            ret.sort((i1, i2) -> i1.getPosition() - i2.getPosition());
        }

        return ret;
    }

    /**
     * 添加物品到背包（自动寻找空槽位）
     * @param item 要添加的物品
     * @return 放入的槽位ID，失败返回-1
     */
    public short addItem(Item item) {
        short slotId = addSlot(item);
        if (slotId == -1) {
            return -1;
        }
        item.setPosition(slotId);
        return slotId;
    }

    /**
     * 从数据库加载物品（按已有位置放置）
     * @param item 要添加的物品
     */
    public void addItemFromDB(Item item) {
        if (item.getPosition() < 0 && !type.equals(InventoryType.EQUIPPED)) {
            return;
        }
        addSlotFromDB(item.getPosition(), item);
    }

    /**
     * 检查两个物品是否有相同的所有者
     */
    private static boolean isSameOwner(Item source, Item target) {
        return source.getOwner().equals(target.getOwner());
    }

    /**
     * 移动物品（支持交换和堆叠）
     * @param sSlot 源槽位
     * @param dSlot 目标槽位
     * @param slotMax 单格最大堆叠数
     */
    public void move(short sSlot, short dSlot, short slotMax) {
        lock.lock();
        try {
            Item source = inventory.get(sSlot);
            Item target = inventory.get(dSlot);
            if (source == null) {
                return;
            }
            if (target == null) {
                source.setPosition(dSlot);
                inventory.put(dSlot, source);
                inventory.remove(sSlot);
            } else if (target.getItemId() == source.getItemId() && !ItemConstants.isRechargeable(source.getItemId()) && isSameOwner(source, target)) {
                if (type.getType() == InventoryType.EQUIP.getType() || type.getType() == InventoryType.CASH.getType()) {
                    swap(target, source);
                } else if (source.getQuantity() + target.getQuantity() > slotMax) {
                    short rest = (short) ((source.getQuantity() + target.getQuantity()) - slotMax);
                    source.setQuantity(rest);
                    target.setQuantity(slotMax);
                } else {
                    target.setQuantity((short) (source.getQuantity() + target.getQuantity()));
                    inventory.remove(sSlot);
                }
            } else {
                swap(target, source);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 交换两个物品的位置
     */
    private void swap(Item source, Item target) {
        inventory.remove(source.getPosition());
        inventory.remove(target.getPosition());
        short swapPos = source.getPosition();
        source.setPosition(target.getPosition());
        target.setPosition(swapPos);
        inventory.put(source.getPosition(), source);
        inventory.put(target.getPosition(), target);
    }

    /**
     * 获取指定槽位的物品
     * @param slot 槽位
     * @return 物品，槽位为空返回null
     */
    public Item getItem(short slot) {
        lock.lock();
        try {
            return inventory.get(slot);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 移除物品（默认数量1）
     * @param slot 槽位
     */
    public void removeItem(short slot) {
        removeItem(slot, (short) 1, false);
    }

    /**
     * 移除指定数量的物品
     * @param slot 槽位
     * @param quantity 数量
     * @param allowZero 是否允许数量为0时保留槽位
     */
    public void removeItem(short slot, short quantity, boolean allowZero) {
        Item item = getItem(slot);
        if (item == null) {
            return;
        }
        item.setQuantity((short) (item.getQuantity() - quantity));
        if (item.getQuantity() < 0) {
            item.setQuantity((short) 0);
        }
        if (item.getQuantity() == 0 && !allowZero) {
            removeSlot(slot);
        }
    }

    /**
     * 添加物品到下一个空槽位（内部方法）
     */
    protected short addSlot(Item item) {
        if (item == null) {
            return -1;
        }

        short slotId;
        lock.lock();
        try {
            slotId = getNextFreeSlot();
            if (slotId < 0) {
                return -1;
            }

            inventory.put(slotId, item);
        } finally {
            lock.unlock();
        }

        if (ItemConstants.isRateCoupon(item.getItemId())) {
            ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
        }

        return slotId;
    }

    /**
     * 从数据库添加物品到指定槽位（内部方法）
     */
    protected void addSlotFromDB(short slot, Item item) {
        lock.lock();
        try {
            inventory.put(slot, item);
        } finally {
            lock.unlock();
        }

        if (ItemConstants.isRateCoupon(item.getItemId())) {
            ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
        }
    }

    /**
     * 移除指定槽位的物品
     * @param slot 槽位
     */
    public void removeSlot(short slot) {
        Item item;
        lock.lock();
        try {
            item = inventory.remove(slot);
        } finally {
            lock.unlock();
        }

        if (item != null && ItemConstants.isRateCoupon(item.getItemId())) {
            ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
        }
    }

    /**
     * 判断背包是否已满
     * @return 是否已满
     */
    public boolean isFull() {
        lock.lock();
        try {
            return inventory.size() >= slotLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 判断背包加上余量后是否已满
     * @param margin 余量
     * @return 是否已满
     */
    public boolean isFull(int margin) {
        lock.lock();
        try {
            return inventory.size() + margin >= slotLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 判断背包加上余量和已用槽位后是否已满
     */
    public boolean isFullAfterSomeItems(int margin, int used) {
        lock.lock();
        try {
            return inventory.size() + margin >= slotLimit - used;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取下一个空槽位
     * @return 空槽位ID，背包已满返回-1
     */
    public short getNextFreeSlot() {
        if (isFull()) {
            return -1;
        }

        lock.lock();
        try {
            for (short i = 1; i <= slotLimit; i++) {
                if (!inventory.containsKey(i)) {
                    return i;
                }
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取空闲槽位数量
     * @return 空闲槽位数
     */
    public short getNumFreeSlot() {
        if (isFull()) {
            return 0;
        }

        lock.lock();
        try {
            short free = 0;
            for (short i = 1; i <= slotLimit; i++) {
                if (!inventory.containsKey(i)) {
                    free++;
                }
            }
            return free;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 检查物品是否有限制（拾取限制物品不可堆叠超过1）
     */
    private static boolean checkItemRestricted(List<Pair<Item, InventoryType>> items) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (Pair<Item, InventoryType> p : items) {
            int itemid = p.getLeft().getItemId();
            if (ii.isPickupRestricted(itemid) && p.getLeft().getQuantity() > 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查角色背包是否有空间放单个物品
     */
    public static boolean checkSpot(Character chr, Item item) {
        return checkSpot(chr, Collections.singletonList(item));
    }

    /**
     * 检查角色背包是否有空间放多个物品
     */
    public static boolean checkSpot(Character chr, List<Item> items) {
        List<Pair<Item, InventoryType>> listItems = new LinkedList<>();
        for (Item item : items) {
            listItems.add(new Pair<>(item, item.getInventoryType()));
        }

        return checkSpotsAndOwnership(chr, listItems);
    }

    /**
     * 检查背包空间（不考虑所有者）
     */
    public static boolean checkSpots(Character chr, List<Pair<Item, InventoryType>> items) {
        return checkSpots(chr, items, false);
    }

    public static boolean checkSpots(Character chr, List<Pair<Item, InventoryType>> items, boolean useProofInv) {
        int invTypesSize = InventoryType.values().length;
        List<Integer> zeroedList = new ArrayList<>(invTypesSize);
        for (byte i = 0; i < invTypesSize; i++) {
            zeroedList.add(0);
        }

        return checkSpots(chr, items, zeroedList, useProofInv);
    }

    /**
     * 检查背包空间的核心方法
     */
    public static boolean checkSpots(Character chr, List<Pair<Item, InventoryType>> items, List<Integer> typesSlotsUsed, boolean useProofInv) {
        if (!checkItemRestricted(items)) {
            return false;
        }

        Map<Integer, List<Integer>> rcvItems = new LinkedHashMap<>();
        Map<Integer, Byte> rcvTypes = new LinkedHashMap<>();

        for (Pair<Item, InventoryType> item : items) {
            Integer itemId = item.left.getItemId();
            List<Integer> qty = rcvItems.get(itemId);

            if (qty == null) {
                List<Integer> itemQtyList = new LinkedList<>();
                itemQtyList.add((int) item.left.getQuantity());

                rcvItems.put(itemId, itemQtyList);
                rcvTypes.put(itemId, item.right.getType());
            } else {
                if (!ItemConstants.isEquipment(itemId) && !ItemConstants.isRechargeable(itemId)) {
                    qty.set(0, qty.get(0) + item.left.getQuantity());
                } else {
                    qty.add((int) item.left.getQuantity());
                }
            }
        }

        Client c = chr.getClient();
        for (Entry<Integer, List<Integer>> it : rcvItems.entrySet()) {
            int itemType = rcvTypes.get(it.getKey()) - 1;

            for (Integer itValue : it.getValue()) {
                int usedSlots = typesSlotsUsed.get(itemType);

                int result = InventoryManipulator.checkSpaceProgressively(c, it.getKey(), itValue, "", usedSlots, useProofInv);
                boolean hasSpace = ((result % 2) != 0);

                if (!hasSpace) {
                    return false;
                }
                typesSlotsUsed.set(itemType, (result >> 1));
            }
        }

        return true;
    }

    /** FNV32哈希算法 */
    private static long fnvHash32(final String k) {
        final int FNV_32_INIT = 0x811c9dc5;
        final int FNV_32_PRIME = 0x01000193;

        int rv = FNV_32_INIT;
        final int len = k.length();
        for (int i = 0; i < len; i++) {
            rv ^= k.charAt(i);
            rv *= FNV_32_PRIME;
        }

        return rv >= 0 ? rv : (2L * Integer.MAX_VALUE) + rv;
    }

    /**
     * 生成物品+所有者的哈希键
     */
    private static Long hashKey(Integer itemId, String owner) {
        return (itemId.longValue() << 32L) + fnvHash32(owner);
    }

    public static boolean checkSpotsAndOwnership(Character chr, List<Pair<Item, InventoryType>> items) {
        return checkSpotsAndOwnership(chr, items, false);
    }

    public static boolean checkSpotsAndOwnership(Character chr, List<Pair<Item, InventoryType>> items, boolean useProofInv) {
        List<Integer> zeroedList = new ArrayList<>(5);
        for (byte i = 0; i < 5; i++) {
            zeroedList.add(0);
        }

        return checkSpotsAndOwnership(chr, items, zeroedList, useProofInv);
    }

    /**
     * 检查背包空间并考虑物品所有者标记
     * 用于交易、个人商店等需要验证物品所有权的场景
     */
    public static boolean checkSpotsAndOwnership(Character chr, List<Pair<Item, InventoryType>> items, List<Integer> typesSlotsUsed, boolean useProofInv) {
        if (!checkItemRestricted(items)) {
            return false;
        }

        Map<Long, List<Integer>> rcvItems = new LinkedHashMap<>();
        Map<Long, Byte> rcvTypes = new LinkedHashMap<>();
        Map<Long, String> rcvOwners = new LinkedHashMap<>();

        for (Pair<Item, InventoryType> item : items) {
            Long itemHash = hashKey(item.left.getItemId(), item.left.getOwner());
            List<Integer> qty = rcvItems.get(itemHash);

            if (qty == null) {
                List<Integer> itemQtyList = new LinkedList<>();
                itemQtyList.add((int) item.left.getQuantity());

                rcvItems.put(itemHash, itemQtyList);
                rcvTypes.put(itemHash, item.right.getType());
                rcvOwners.put(itemHash, item.left.getOwner());
            } else {
                if (!ItemConstants.isEquipment(item.left.getItemId()) && !ItemConstants.isRechargeable(item.left.getItemId())) {
                    qty.set(0, qty.get(0) + item.left.getQuantity());
                } else {
                    qty.add((int) item.left.getQuantity());
                }
            }
        }

        Client c = chr.getClient();
        for (Entry<Long, List<Integer>> it : rcvItems.entrySet()) {
            int itemType = rcvTypes.get(it.getKey()) - 1;
            int itemId = (int) (it.getKey() >> 32L);

            for (Integer itValue : it.getValue()) {
                int usedSlots = typesSlotsUsed.get(itemType);

                int result = InventoryManipulator.checkSpaceProgressively(c, itemId, itValue, rcvOwners.get(it.getKey()), usedSlots, useProofInv);
                boolean hasSpace = ((result % 2) != 0);

                if (!hasSpace) {
                    return false;
                }
                typesSlotsUsed.set(itemType, (result >> 1));
            }
        }

        return true;
    }

    /**
     * 获取背包类型
     * @return 背包类型枚举
     */
    public InventoryType getType() {
        return type;
    }

    @Override
    public Iterator<Item> iterator() {
        return Collections.unmodifiableCollection(list()).iterator();
    }

    /**
     * 根据现金ID查找物品（包括宠物和戒指）
     * @param cashId 现金ID
     * @return 找到的物品
     */
    public Item findByCashId(int cashId) {
        boolean isRing = false;
        Equip equip = null;
        for (Item item : list()) {
            if (item.getInventoryType().equals(InventoryType.EQUIP)) {
                equip = (Equip) item;
                isRing = equip.getRingId() > -1;
            }
            if ((item.getPetId() > -1 ? item.getPetId() : isRing ? equip.getRingId() : item.getCashId()) == cashId) {
                return item;
            }
        }

        return null;
    }

    /**
     * 获取检查标记
     */
    public boolean checked() {
        lock.lock();
        try {
            return checked;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置检查标记
     */
    public void checked(boolean yes) {
        lock.lock();
        try {
            checked = yes;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 手动加锁（用于复杂操作的原子性保证）
     */
    public void lockInventory() {
        lock.lock();
    }

    /**
     * 手动解锁
     */
    public void unlockInventory() {
        lock.unlock();
    }

    /**
     * 清理背包引用
     */
    public void dispose() {
        owner = null;
    }
}
