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

import org.gms.client.inventory.Item;

/**
 * 个人商店中的商品条目。
 */
public class PlayerShopItem {
    private final Item item;
    private short bundles;
    private final int price;
    private boolean doesExist;

    /**
     * 构造 PlayerShopItem 实例。
     * @param item item
     * @param bundles bundles
     * @param price price
     */
    public PlayerShopItem(Item item, short bundles, int price) {
        this.item = item;
        this.bundles = bundles;
        this.price = price;
        this.doesExist = true;
    }

    /**
     * 设置Does、Exist。
     * @param tf tf
     */
    public void setDoesExist(boolean tf) {
        this.doesExist = tf;
    }

    /**
     * 判断是否为Exist。
     * @return boolean 类型结果
     */
    public boolean isExist() {
        return doesExist;
    }

    /**
     * 获取物品。
     * @return Item 类型结果
     */
    public Item getItem() {
        return item;
    }

    /**
     * 获取Bundles。
     * @return short 类型结果
     */
    public short getBundles() {
        return bundles;
    }

    /**
     * 获取Price。
     * @return int 类型结果
     */
    public int getPrice() {
        return price;
    }

    /**
     * 设置Bundles。
     * @param bundles bundles
     */
    public void setBundles(short bundles) {
        this.bundles = bundles;
    }
}