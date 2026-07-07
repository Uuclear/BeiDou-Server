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

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.client.inventory.manipulator.KarmaManipulator;
import org.gms.net.packet.Packet;
import org.gms.server.Trade;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 玩家个人商店（开店）实例。
 */
public class PlayerShop extends AbstractMapObject {
    private final AtomicBoolean open = new AtomicBoolean(false);
    private final Character owner;
    private final int itemid;

    private final Character[] visitors = new Character[3];
    private final List<PlayerShopItem> items = new ArrayList<>();
    private final List<SoldItem> sold = new LinkedList<>();
    private String description;
    private int boughtnumber = 0;
    private final List<String> bannedList = new ArrayList<>();
    private final List<Pair<Character, String>> chatLog = new LinkedList<>();
    private final Map<Integer, Byte> chatSlot = new LinkedHashMap<>();
    private final Lock visitorLock = new ReentrantLock(true);

    /**
     * 构造 PlayerShop 实例。
     * @param owner 归属角色
     * @param description description
     * @param itemid 物品 ID
     */
    public PlayerShop(Character owner, String description, int itemid) {
        this.setPosition(owner.getPosition());
        this.owner = owner;
        this.description = description;
        this.itemid = itemid;
    }

    /**
     * 获取频道。
     * @return int 类型结果
     */
    public int getChannel() {
        return owner.getClient().getChannel();
    }

    /**
     * 获取地图ID。
     * @return int 类型结果
     */
    public int getMapId() {
        return owner.getMapId();
    }

    /**
     * 获取物品ID。
     * @return int 类型结果
     */
    public int getItemId() {
        return itemid;
    }

    /**
     * 判断是否为Open。
     * @return boolean 类型结果
     */
    public boolean isOpen() {
        return open.get();
    }

    /**
     * 设置Open。
     * @param openShop openShop
     */
    public void setOpen(boolean openShop) {
        open.set(openShop);
    }

    /**
     * 判断是否拥有Free、Slot。
     * @return boolean 类型结果
     */
    public boolean hasFreeSlot() {
        visitorLock.lock();
        try {
            return visitors[0] == null || visitors[1] == null || visitors[2] == null;
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 获取商店、Room、信息。
     * @return byte[] 类型结果
     */
    public byte[] getShopRoomInfo() {
        visitorLock.lock();
        try {
            byte count = 0;
            //if (this.isOpen()) {
            for (Character visitor : visitors) {
                if (visitor != null) {
                    count++;
                }
            }
            //} else {  shouldn't happen since there isn't a "closed" state for player shops.
            //    count = (byte) (visitors.length + 1);
            //}

            return new byte[]{count, (byte) visitors.length};
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 判断是否为归属者。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean isOwner(Character chr) {
        return owner.equals(chr);
    }

    private void addVisitor(Character visitor) {
        for (int i = 0; i < 3; i++) {
            if (visitors[i] == null) {
                visitors[i] = visitor;
                visitor.setSlot(i);

                this.broadcast(PacketCreator.getPlayerShopNewVisitor(visitor, i + 1));
                owner.getMap().broadcastMessage(PacketCreator.updatePlayerShopBox(this));
                break;
            }
        }
    }

    /**
     * 执行 force、移除、Visitor 操作。
     * @param visitor visitor
     */
    public void forceRemoveVisitor(Character visitor) {
        if (visitor == owner) {
            owner.getMap().removeMapObject(this);
            owner.setPlayerShop(null);
        }

        visitorLock.lock();
        try {
            for (int i = 0; i < 3; i++) {
                if (visitors[i] != null && visitors[i].getId() == visitor.getId()) {
                    visitors[i].setPlayerShop(null);
                    visitors[i] = null;
                    visitor.setSlot(-1);

                    this.broadcast(PacketCreator.getPlayerShopRemoveVisitor(i + 1));
                    owner.getMap().broadcastMessage(PacketCreator.updatePlayerShopBox(this));
                    return;
                }
            }
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 移除Visitor。
     * @param visitor visitor
     */
    public void removeVisitor(Character visitor) {
        if (visitor == owner) {
            owner.getMap().removeMapObject(this);
            owner.setPlayerShop(null);
        } else {
            visitorLock.lock();
            try {
                for (int i = 0; i < 3; i++) {
                    if (visitors[i] != null && visitors[i].getId() == visitor.getId()) {
                        visitor.setSlot(-1);    //absolutely cant remove player slot for late players without dc'ing them... heh

                        for (int j = i; j < 2; j++) {
                            if (visitors[j] != null) {
                                owner.sendPacket(PacketCreator.getPlayerShopRemoveVisitor(j + 1));
                            }
                            visitors[j] = visitors[j + 1];
                            if (visitors[j] != null) {
                                visitors[j].setSlot(j);
                            }
                        }
                        visitors[2] = null;
                        for (int j = i; j < 2; j++) {
                            if (visitors[j] != null) {
                                owner.sendPacket(PacketCreator.getPlayerShopNewVisitor(visitors[j], j + 1));
                            }
                        }

                        this.broadcastRestoreToVisitors();
                        owner.getMap().broadcastMessage(PacketCreator.updatePlayerShopBox(this));
                        return;
                    }
                }
            } finally {
                visitorLock.unlock();
            }

            owner.getMap().broadcastMessage(PacketCreator.updatePlayerShopBox(this));
        }
    }

    /**
     * 判断是否为Visitor。
     * @param visitor visitor
     * @return boolean 类型结果
     */
    public boolean isVisitor(Character visitor) {
        visitorLock.lock();
        try {
            return visitors[0] == visitor || visitors[1] == visitor || visitors[2] == visitor;
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 添加物品。
     * @param item item
     * @return boolean 类型结果
     */
    public boolean addItem(PlayerShopItem item) {
        synchronized (items) {
            if (items.size() >= 16) {
                return false;
            }

            items.add(item);
            return true;
        }
    }

    private void removeFromSlot(int slot) {
        items.remove(slot);
    }

    private static boolean canBuy(Client c, Item newItem) {
        return InventoryManipulator.checkSpace(c, newItem.getItemId(), newItem.getQuantity(), newItem.getOwner()) && InventoryManipulator.addFromDrop(c, newItem, false);
    }

    /**
     * 执行 take、物品、Back 操作。
     * @param slot slot
     * @param chr 角色
     */
    public void takeItemBack(int slot, Character chr) {
        synchronized (items) {
            PlayerShopItem shopItem = items.get(slot);
            if (shopItem.isExist()) {
                if (shopItem.getBundles() > 0) {
                    Item iitem = shopItem.getItem().copy();
                    iitem.setQuantity((short) (shopItem.getItem().getQuantity() * shopItem.getBundles()));

                    if (!Inventory.checkSpot(chr, iitem)) {
                        chr.sendPacket(PacketCreator.serverNotice(1, "Have a slot available on your inventory to claim back the item."));
                        chr.sendPacket(PacketCreator.enableActions());
                        return;
                    }

                    InventoryManipulator.addFromDrop(chr.getClient(), iitem, true);
                }

                removeFromSlot(slot);
                chr.sendPacket(PacketCreator.getPlayerShopItemUpdate(this));
            }
        }
    }

    /**
     * 执行 buy 操作。
     * @param c c
     * @param item item
     * @param quantity quantity
     * @return boolean 类型结果
     */
    public boolean buy(Client c, int item, short quantity) {
        synchronized (items) {
            if (isVisitor(c.getPlayer())) {
                PlayerShopItem pItem = items.get(item);
                Item newItem = pItem.getItem().copy();

                newItem.setQuantity((short) ((pItem.getItem().getQuantity() * quantity)));
                if (quantity < 1 || !pItem.isExist() || pItem.getBundles() < quantity) {
                    c.sendPacket(PacketCreator.enableActions());
                    return false;
                } else if (newItem.getInventoryType().equals(InventoryType.EQUIP) && newItem.getQuantity() > 1) {
                    c.sendPacket(PacketCreator.enableActions());
                    return false;
                }

                KarmaManipulator.toggleKarmaFlagToUntradeable(newItem);

                visitorLock.lock();
                try {
                    int price = (int) Math.min((float) pItem.getPrice() * quantity, Integer.MAX_VALUE);

                    if (c.getPlayer().getMeso() >= price) {
                        if (!owner.canHoldMeso(price)) {    // thanks Rohenn for noticing owner hold check misplaced
                            c.getPlayer().dropMessage(1, "Transaction failed since the shop owner can't hold any more mesos.");
                            c.sendPacket(PacketCreator.enableActions());
                            return false;
                        }

                        if (canBuy(c, newItem)) {
                            c.getPlayer().gainMeso(-price, false);
                            price -= Trade.getFee(price);  // thanks BHB for pointing out trade fees not applying here
                            owner.gainMeso(price, true);

                            SoldItem soldItem = new SoldItem(c.getPlayer().getName(), pItem.getItem().getItemId(), quantity, price);
                            owner.sendPacket(PacketCreator.getPlayerShopOwnerUpdate(soldItem, item));

                            synchronized (sold) {
                                sold.add(soldItem);
                            }

                            pItem.setBundles((short) (pItem.getBundles() - quantity));
                            if (pItem.getBundles() < 1) {
                                pItem.setDoesExist(false);
                                if (++boughtnumber == items.size()) {
                                    owner.setPlayerShop(null);
                                    this.setOpen(false);
                                    this.closeShop();
                                    owner.dropMessage(1, "Your items are sold out, and therefore your shop is closed.");
                                }
                            }
                        } else {
                            c.getPlayer().dropMessage(1, "Your inventory is full. Please clear a slot before buying this item.");
                            c.sendPacket(PacketCreator.enableActions());
                            return false;
                        }
                    } else {
                        c.getPlayer().dropMessage(1, "You don't have enough mesos to purchase this item.");
                        c.sendPacket(PacketCreator.enableActions());
                        return false;
                    }

                    return true;
                } finally {
                    visitorLock.unlock();
                }
            } else {
                return false;
            }
        }
    }

    /**
     * 向地图广播到、Visitors。
     * @param packet 网络数据包
     */
    public void broadcastToVisitors(Packet packet) {
        visitorLock.lock();
        try {
            for (int i = 0; i < 3; i++) {
                if (visitors[i] != null) {
                    visitors[i].sendPacket(packet);
                }
            }
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 向地图广播Restore、到、Visitors。
     */
    public void broadcastRestoreToVisitors() {
        visitorLock.lock();
        try {
            for (int i = 0; i < 3; i++) {
                if (visitors[i] != null) {
                    visitors[i].sendPacket(PacketCreator.getPlayerShopRemoveVisitor(i + 1));
                }
            }

            for (int i = 0; i < 3; i++) {
                if (visitors[i] != null) {
                    visitors[i].sendPacket(PacketCreator.getPlayerShop(this, false));
                }
            }

            recoverChatLog();
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 移除Visitors。
     */
    public void removeVisitors() {
        List<Character> visitorList = new ArrayList<>(3);

        visitorLock.lock();
        try {
            try {
                for (int i = 0; i < 3; i++) {
                    if (visitors[i] != null) {
                        visitors[i].sendPacket(PacketCreator.shopErrorMessage(10, 1));
                        visitorList.add(visitors[i]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            visitorLock.unlock();
        }

        for (Character mc : visitorList) {
            forceRemoveVisitor(mc);
        }
        if (owner != null) {
            forceRemoveVisitor(owner);
        }
    }

    /**
     * 执行 broadcast 操作。
     * @param packet 网络数据包
     */
    public void broadcast(Packet packet) {
        Client client = owner.getClient();
        if (client != null) {
            client.sendPacket(packet);
        }
        broadcastToVisitors(packet);
    }

    private byte getVisitorSlot(Character chr) {
        byte s = 0;
        for (Character mc : getVisitors()) {
            s++;
            if (mc != null) {
                if (mc.getName().equalsIgnoreCase(chr.getName())) {
                    break;
                }
            } else if (s == 3) {
                s = 0;
            }
        }

        return s;
    }

    /**
     * 执行 chat 操作。
     * @param c c
     * @param chat chat
     */
    public void chat(Client c, String chat) {
        byte s = getVisitorSlot(c.getPlayer());

        synchronized (chatLog) {
            chatLog.add(new Pair<>(c.getPlayer(), chat));
            if (chatLog.size() > 25) {
                chatLog.remove(0);
            }
            chatSlot.put(c.getPlayer().getId(), s);
        }

        broadcast(PacketCreator.getPlayerShopChat(c.getPlayer(), chat, s));
    }

    private void recoverChatLog() {
        synchronized (chatLog) {
            for (Pair<Character, String> it : chatLog) {
                Character chr = it.getLeft();
                Byte pos = chatSlot.get(chr.getId());

                broadcastToVisitors(PacketCreator.getPlayerShopChat(chr, it.getRight(), pos));
            }
        }
    }

    private void clearChatLog() {
        synchronized (chatLog) {
            chatLog.clear();
        }
    }

    /**
     * 执行 close、商店 操作。
     */
    public void closeShop() {
        clearChatLog();
        removeVisitors();
        owner.getMap().broadcastMessage(PacketCreator.removePlayerShopBox(this));
    }

    /**
     * 执行 send、商店 操作。
     * @param c c
     */
    public void sendShop(Client c) {
        visitorLock.lock();
        try {
            c.sendPacket(PacketCreator.getPlayerShop(this, isOwner(c.getPlayer())));
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 获取归属者。
     * @return Character 类型结果
     */
    public Character getOwner() {
        return owner;
    }

    /**
     * 获取Visitors。
     * @return Character[] 类型结果
     */
    public Character[] getVisitors() {
        visitorLock.lock();
        try {
            Character[] copy = new Character[3];
            for (int i = 0; i < visitors.length; i++) {
                copy[i] = visitors[i];
            }

            return copy;
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 获取物品。
     * @return List<PlayerShopItem> 类型结果
     */
    public List<PlayerShopItem> getItems() {
        synchronized (items) {
            return Collections.unmodifiableList(items);
        }
    }

    /**
     * 判断是否拥有物品。
     * @param itemid 物品 ID
     * @return boolean 类型结果
     */
    public boolean hasItem(int itemid) {
        for (PlayerShopItem mpsi : getItems()) {
            if (mpsi.getItem().getItemId() == itemid && mpsi.isExist() && mpsi.getBundles() > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取Description。
     * @return String 类型结果
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置Description。
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 执行 ban玩家 操作。
     * @param name name
     */
    public void banPlayer(String name) {
        if (!bannedList.contains(name)) {
            bannedList.add(name);
        }

        Character target = null;
        visitorLock.lock();
        try {
            for (int i = 0; i < 3; i++) {
                if (visitors[i] != null && visitors[i].getName().equals(name)) {
                    target = visitors[i];
                    break;
                }
            }
        } finally {
            visitorLock.unlock();
        }

        if (target != null) {
            target.sendPacket(PacketCreator.shopErrorMessage(5, 1));
            removeVisitor(target);
        }
    }

    /**
     * 判断是否为Banned。
     * @param name name
     * @return boolean 类型结果
     */
    public boolean isBanned(String name) {
        return bannedList.contains(name);
    }

    /**
     * 执行 visit、商店 操作。
     * @param chr 角色
     * @return synchronized boolean 类型结果
     */
    public synchronized boolean visitShop(Character chr) {
        if (this.isBanned(chr.getName())) {
            chr.dropMessage(1, "You have been banned from this store.");
            return false;
        }

        visitorLock.lock();
        try {
            if (!open.get()) {
                chr.dropMessage(1, "This store is not yet open.");
                return false;
            }

            if (this.hasFreeSlot() && !this.isVisitor(chr)) {
                this.addVisitor(chr);
                chr.setPlayerShop(this);
                this.sendShop(chr.getClient());

                return true;
            }

            return false;
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 执行 send、Available、Bundles 操作。
     * @param itemid 物品 ID
     * @return List<PlayerShopItem> 类型结果
     */
    public List<PlayerShopItem> sendAvailableBundles(int itemid) {
        List<PlayerShopItem> list = new LinkedList<>();
        List<PlayerShopItem> all = new ArrayList<>();

        synchronized (items) {
            all.addAll(items);
        }

        for (PlayerShopItem mpsi : all) {
            if (mpsi.getItem().getItemId() == itemid && mpsi.getBundles() > 0 && mpsi.isExist()) {
                list.add(mpsi);
            }
        }
        return list;
    }

    /**
     * 获取Sold。
     * @return List<SoldItem> 类型结果
     */
    public List<SoldItem> getSold() {
        synchronized (sold) {
            return Collections.unmodifiableList(sold);
        }
    }

    /**
     * 执行 send、Destroy、数据 操作。
     * @param client client
     */
    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(PacketCreator.removePlayerShopBox(this));
    }

    /**
     * 执行 send、刷新、数据 操作。
     * @param client client
     */
    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(PacketCreator.updatePlayerShopBox(this));
    }

    /**
     * 获取类型。
     * @return MapObjectType 类型结果
     */
    @Override
    public MapObjectType getType() {
        return MapObjectType.SHOP;
    }

    public class SoldItem {

        int itemid, mesos;
        short quantity;
        String buyer;

        /**
         * 执行 Sold、物品 操作。
         * @param buyer buyer
         * @param itemid 物品 ID
         * @param quantity quantity
         * @param mesos mesos
         * @return SoldItem 类型结果
         */
        public SoldItem(String buyer, int itemid, short quantity, int mesos) {
            this.buyer = buyer;
            this.itemid = itemid;
            this.quantity = quantity;
            this.mesos = mesos;
        }

        /**
         * 获取Buyer。
         * @return String 类型结果
         */
        public String getBuyer() {
            return buyer;
        }

        /**
         * 获取物品ID。
         * @return int 类型结果
         */
        public int getItemId() {
            return itemid;
        }

        /**
         * 获取Quantity。
         * @return short 类型结果
         */
        public short getQuantity() {
            return quantity;
        }

        /**
         * 获取Mesos。
         * @return int 类型结果
         */
        public int getMesos() {
            return mesos;
        }
    }
}
