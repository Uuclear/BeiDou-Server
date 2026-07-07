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

import java.util.HashMap;
import java.util.Map;

/**
 * 商店工厂，从 WZ/数据库加载并缓存 NPC 商店数据。
 */
public class ShopFactory {
    private static final ShopFactory instance = new ShopFactory();

    /**
     * 获取单例实例。
     * @return ShopFactory 类型结果
     */
    public static ShopFactory getInstance() {
        return instance;
    }

    private final Map<Integer, Shop> shops = new HashMap<>();
    private final Map<Integer, Shop> npcShops = new HashMap<>();

    private Shop loadShop(int id, boolean isShopId) {
        Shop ret = Shop.createFromDB(id, isShopId);
        if (ret != null) {
            shops.put(ret.getId(), ret);
            npcShops.put(ret.getNpcId(), ret);
        } else if (isShopId) {
            shops.put(id, null);
        } else {
            npcShops.put(id, null);
        }
        return ret;
    }

    /**
     * 获取商店。
     * @param shopId shopId
     * @return Shop 类型结果
     */
    public Shop getShop(int shopId) {
        if (shops.containsKey(shopId)) {
            return shops.get(shopId);
        }
        return loadShop(shopId, true);
    }

    /**
     * 获取商店为NPC。
     * @param npcId NPC ID
     * @return Shop 类型结果
     */
    public Shop getShopForNPC(int npcId) {
        if (npcShops.containsKey(npcId)) {
            return npcShops.get(npcId);
        }
        return loadShop(npcId, false);
    }

    /**
     * 执行 reload、Shops 操作。
     */
    public void reloadShops() {
        shops.clear();
        npcShops.clear();
    }
}
