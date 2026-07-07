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

import org.gms.client.inventory.Item;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * 拍卖行（MTS）物品信息数据结构。
 */
public class MTSItemInfo {
    private final int price;
    private final Item item;
    private final String seller;
    private final int id;
    private final int year;
    private final int month;
    private int day = 1;

    /**
     * 构造 MTSItemInfo 实例。
     * @param item item
     * @param price price
     * @param id ID
     * @param cid cid
     * @param seller seller
     * @param date date
     */
    public MTSItemInfo(Item item, int price, int id, int cid, String seller, String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sellEnd = LocalDate.parse(date, formatter);

        this.item = item;
        this.price = price;
        this.seller = seller;
        this.id = id;
        this.year = sellEnd.getYear();
        this.month = sellEnd.getMonthValue();
        this.day = sellEnd.getDayOfMonth();
    }

    /**
     * 获取物品。
     * @return Item 类型结果
     */
    public Item getItem() {
        return item;
    }

    /**
     * 获取Price。
     * @return int 类型结果
     */
    public int getPrice() {
        return price;
    }

    /**
     * 获取Taxes。
     * @return int 类型结果
     */
    public int getTaxes() {
        return 100 + price / 10;
    }

    /**
     * 获取ID。
     * @return int 类型结果
     */
    public int getID() {
        return id;
    }

    /**
     * 获取Ending、日期。
     * @return long 类型结果
     */
    public long getEndingDate() {
        Calendar now = Calendar.getInstance();
        now.set(year, month - 1, day);
        return now.getTimeInMillis();
    }

    /**
     * 获取Seller。
     * @return String 类型结果
     */
    public String getSeller() {
        return seller;
    }
}
