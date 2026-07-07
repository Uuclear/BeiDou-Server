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

import java.sql.Timestamp;
import java.util.Calendar;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 杜伊快递包裹数据结构，用于离线邮件式物品投递。
 */
public class DueyPackage {
    private String sender = null;
    private Item item = null;
    private int mesos = 0;
    private String message = null;
    private Calendar timestamp;
    private int packageId = 0;
    private Integer receiverId;

    /**
     * 构造 DueyPackage 实例。
     * @param pId pId
     * @param item item
     */
    public DueyPackage(int pId, Item item) {
        this.item = item;
        packageId = pId;
    }

    /**
     * 构造 DueyPackage 实例。
     * @param pId pId
     */
    public DueyPackage(int pId) { // Meso only package.
        this.packageId = pId;
    }

    /**
     * 获取Sender。
     * @return String 类型结果
     */
    public String getSender() {
        return sender;
    }

    /**
     * 设置Sender。
     * @param name name
     */
    public void setSender(String name) {
        sender = name;
    }

    /**
     * 获取物品。
     * @return Item 类型结果
     */
    public Item getItem() {
        return item;
    }

    /**
     * 获取Mesos。
     * @return int 类型结果
     */
    public int getMesos() {
        return mesos;
    }

    /**
     * 设置Mesos。
     * @param set set
     */
    public void setMesos(int set) {
        mesos = set;
    }

    /**
     * 获取Message。
     * @return String 类型结果
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置Message。
     * @param m m
     */
    public void setMessage(String m) {
        message = m;
    }

    /**
     * 获取Package、ID。
     * @return int 类型结果
     */
    public int getPackageId() {
        return packageId;
    }

    /**
     * 获取Receiver、ID。
     * @return Integer 类型结果
     */
    public Integer getReceiverId() {
        return receiverId;
    }

    /**
     * 设置Receiver、ID。
     * @param receiverId receiverId
     */
    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * 执行 sent、时间、在、Milliseconds 操作。
     * @return long 类型结果
     */
    public long sentTimeInMilliseconds() {
        Calendar ts = timestamp;
        if (ts != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(ts.getTime());
            cal.add(Calendar.MONTH, 1);  // duey representation is in an array of months.

            return cal.getTimeInMillis();
        } else {
            return 0;
        }
    }

    /**
     * 判断是否为Delivering、时间。
     * @return boolean 类型结果
     */
    public boolean isDeliveringTime() {
        Calendar ts = timestamp;
        if (ts != null) {
            return ts.getTimeInMillis() >= System.currentTimeMillis();
        } else {
            return false;
        }
    }

    /**
     * 设置Sent、时间。
     * @param ts ts
     * @param quick quick
     */
    public void setSentTime(Timestamp ts, boolean quick) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());

        if (quick) {
            if (System.currentTimeMillis() - ts.getTime() < DAYS.toMillis(1)) {  // thanks inhyuk for noticing quick delivery packages unavailable to retrieve from the get-go
                cal.add(Calendar.DATE, -1);
            }
        }

        this.timestamp = cal;
    }
}
