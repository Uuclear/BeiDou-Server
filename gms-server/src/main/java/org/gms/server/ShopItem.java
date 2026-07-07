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

/**
 * 商店中的单个商品条目（物品 ID、价格、库存等）。
 */
public class ShopItem {
    private final short buyable;
    private final int itemId;
    private final int price;
    private final int pitch;

    /**
     * 构造 ShopItem 实例。
     * @param buyable buyable
     * @param itemId 物品 ID
     * @param price price
     * @param pitch pitch
     */
    public ShopItem(short buyable, int itemId, int price, int pitch) {
        this.buyable = buyable;
        this.itemId = itemId;
        this.price = price;
        this.pitch = pitch;
    }

    /**
     * 获取Buyable。
     * @return short 类型结果
     */
    public short getBuyable() {
        return buyable;
    }

    /**
     * 获取物品ID。
     * @return int 类型结果
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * 获取Price。
     * @return int 类型结果
     */
    public int getPrice() {
        return price;
    }

    /**
     * 获取Pitch。
     * @return int 类型结果
     */
    public int getPitch() {
        return pitch;
    }
}
