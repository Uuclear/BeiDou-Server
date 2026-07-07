/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ItemFactory;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.scripting.event.EventInstanceManager;
import org.gms.scripting.event.EventManager;
import org.gms.util.DatabaseConnection;
import org.gms.util.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 结婚系统逻辑（戒指、婚礼、夫妻技能等）。
 */
public class Marriage extends EventInstanceManager {
    /**
     * 构造 Marriage 实例。
     * @param em em
     * @param name name
     */
    public Marriage(EventManager em, String name) {
        super(em, name);
    }

    /**
     * 执行 gift、物品、到、Spouse 操作。
     * @param cid cid
     * @return boolean 类型结果
     */
    public boolean giftItemToSpouse(int cid) {
        return this.getIntProperty("wishlistSelection") == 0;
    }

    /**
     * 获取Wishlist、物品。
     * @param groom groom
     * @return List<String> 类型结果
     */
    public List<String> getWishlistItems(boolean groom) {
        String strItems = this.getProperty(groom ? "groomWishlist" : "brideWishlist");
        if (strItems != null) {
            return Arrays.asList(strItems.split("\r\n"));
        }

        return new LinkedList<>();
    }

    /**
     * 执行 initialize、Gift、物品 操作。
     */
    public void initializeGiftItems() {
        List<Item> groomGifts = new ArrayList<>();
        this.setObjectProperty("groomGiftlist", groomGifts);

        List<Item> brideGifts = new ArrayList<>();
        this.setObjectProperty("brideGiftlist", brideGifts);
    }

    /**
     * 获取Gift、物品。
     * @param c c
     * @param groom groom
     * @return List<Item> 类型结果
     */
    public List<Item> getGiftItems(Client c, boolean groom) {
        List<Item> gifts = getGiftItemsList(groom);
        synchronized (gifts) {
            return new LinkedList<>(gifts);
        }
    }

    private List<Item> getGiftItemsList(boolean groom) {
        return (List<Item>) this.getObjectProperty(groom ? "groomGiftlist" : "brideGiftlist");
    }

    /**
     * 获取Gift、物品。
     * @param c c
     * @param groom groom
     * @param idx idx
     * @return Item 类型结果
     */
    public Item getGiftItem(Client c, boolean groom, int idx) {
        try {
            return getGiftItems(c, groom).get(idx);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * 添加Gift、物品。
     * @param groom groom
     * @param item item
     */
    public void addGiftItem(boolean groom, Item item) {
        List<Item> gifts = getGiftItemsList(groom);
        synchronized (gifts) {
            gifts.add(item);
        }
    }

    /**
     * 移除Gift、物品。
     * @param groom groom
     * @param item item
     */
    public void removeGiftItem(boolean groom, Item item) {
        List<Item> gifts = getGiftItemsList(groom);
        synchronized (gifts) {
            gifts.remove(item);
        }
    }

    /**
     * 判断是否为Marriage、Groom。
     * @param chr 角色
     * @return Boolean 类型结果
     */
    public Boolean isMarriageGroom(Character chr) {
        Boolean groom = null;
        try {
            int groomid = this.getIntProperty("groomId"), brideid = this.getIntProperty("brideId");
            if (chr.getId() == groomid) {
                groom = true;
            } else if (chr.getId() == brideid) {
                groom = false;
            }
        } catch (NumberFormatException nfe) {
        }

        return groom;
    }

    /**
     * 执行 claim、Gift、物品 操作。
     * @param c c
     * @param chr 角色
     * @return boolean 类型结果
     */
    public static boolean claimGiftItems(Client c, Character chr) {
        List<Item> gifts = loadGiftItemsFromDb(c, chr.getId());
        if (Inventory.checkSpot(chr, gifts)) {
            try (Connection con = DatabaseConnection.getConnection()) {
                ItemFactory.MARRIAGE_GIFTS.saveItems(new LinkedList<>(), chr.getId(), con);
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }

            for (Item item : gifts) {
                InventoryManipulator.addFromDrop(chr.getClient(), item, false);
            }

            return true;
        }

        return false;
    }

    /**
     * 加载Gift、物品、来自、Db。
     * @param c c
     * @param cid cid
     * @return List<Item> 类型结果
     */
    public static List<Item> loadGiftItemsFromDb(Client c, int cid) {
        List<Item> items = new LinkedList<>();

        try {
            for (Pair<Item, InventoryType> it : ItemFactory.MARRIAGE_GIFTS.loadItems(cid, false)) {
                items.add(it.getLeft());
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return items;
    }

    /**
     * 执行 save、Gift、物品、到、Db 操作。
     * @param c c
     * @param groom groom
     * @param cid cid
     */
    public void saveGiftItemsToDb(Client c, boolean groom, int cid) {
        Marriage.saveGiftItemsToDb(c, getGiftItems(c, groom), cid);
    }

    /**
     * 执行 save、Gift、物品、到、Db 操作。
     * @param c c
     * @param giftItems giftItems（Item 列表/集合）
     * @param cid cid
     */
    public static void saveGiftItemsToDb(Client c, List<Item> giftItems, int cid) {
        List<Pair<Item, InventoryType>> items = new LinkedList<>();
        for (Item it : giftItems) {
            items.add(new Pair<>(it, it.getInventoryType()));
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            ItemFactory.MARRIAGE_GIFTS.saveItems(items, cid, con);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
