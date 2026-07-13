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

import org.gms.client.inventory.manipulator.KarmaManipulator;
import org.gms.constants.inventory.ItemConstants;
import org.gms.server.ItemInformationProvider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 物品基类
 * 表示游戏中的所有物品（道具、装备、宠物等）
 * 包含物品ID、位置、数量、所有者、标志位、过期时间等基本属性
 *
 * @author OdinMS Team
 */
public class Item implements Comparable<Item> {

    /** 现金物品ID生成器，宠物和戒指共享此ID序列 */
    private static final AtomicInteger runningCashId = new AtomicInteger(777000000);

    /** 物品ID（对应WZ中的物品ID） */
    private final int id;
    /** 现金物品唯一ID */
    private int cashId;
    /** 物品序列号（商城购买时使用） */
    private int sn;
    /** 物品在背包中的位置 */
    private short position;
    /** 物品数量 */
    private short quantity;
    /** 宠物ID（如果是宠物） */
    private int petid = -1;
    /** 宠物对象 */
    private Pet pet = null;
    /** 物品所有者名称 */
    private String owner = "";
    /** 物品日志记录 */
    protected List<String> itemLog;
    /** 物品标志位（不可交易、账号共享等） */
    private short flag;
    /** 物品过期时间戳，-1表示永不过期 */
    private long expiration = -1;
    /** 礼物赠送者名称 */
    private String giftFrom = "";

    /**
     * 构造函数（普通物品）
     * @param id 物品ID
     * @param position 背包位置
     * @param quantity 数量
     */
    public Item(int id, short position, short quantity) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.itemLog = new LinkedList<>();
        this.flag = 0;
    }

    /**
     * 构造函数（带宠物ID）
     * @param id 物品ID
     * @param position 背包位置
     * @param quantity 数量
     * @param petid 宠物ID
     */
    public Item(int id, short position, short quantity, int petid) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        if (petid > -1) {
            this.pet = Pet.loadFromDb(id, position, petid);
            if (this.pet == null) {
                petid = -1;
            }
        }
        this.petid = petid;
        this.flag = 0;
        this.itemLog = new LinkedList<>();
    }

    /**
     * 复制物品对象
     * @return 复制后的新物品对象
     */
    public Item copy() {
        Item ret = new Item(id, position, quantity, petid);
        ret.flag = flag;
        ret.owner = owner;
        ret.expiration = expiration;
        ret.itemLog = new LinkedList<>(itemLog);
        return ret;
    }

    /**
     * 设置物品在背包中的位置
     * @param position 位置
     */
    public void setPosition(short position) {
        this.position = position;
        if (this.pet != null) {
            this.pet.setPosition(position);
        }
    }

    /**
     * 设置物品数量
     * @param quantity 数量
     */
    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    /**
     * 获取物品ID
     * @return 物品ID
     */
    public int getItemId() {
        return id;
    }

    /**
     * 获取现金物品ID（自动生成）
     * @return 现金物品ID
     */
    public int getCashId() {
        if (cashId == 0) {
            cashId = runningCashId.getAndIncrement();
        }
        return cashId;
    }

    /**
     * 获取物品位置
     * @return 位置
     */
    public short getPosition() {
        return position;
    }

    /**
     * 获取物品数量
     * @return 数量
     */
    public short getQuantity() {
        return quantity;
    }

    /**
     * 获取物品所属的背包类型
     * @return 背包类型枚举
     */
    public InventoryType getInventoryType() {
        return ItemConstants.getInventoryType(id);
    }

    /**
     * 获取物品类型
     * @return 1=装备, 3=宠物, 2=其他
     */
    public byte getItemType() {
        if (getPetId() > -1) {
            return 3;
        }
        return 2;
    }

    /**
     * 获取物品所有者名称
     * @return 所有者名称
     */
    public String getOwner() {
        return owner;
    }

    /**
     * 设置物品所有者名称
     * @param owner 所有者名称
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * 获取宠物ID
     * @return 宠物ID，-1表示不是宠物
     */
    public int getPetId() {
        return petid;
    }

    /**
     * 比较两个物品（按ID排序）
     * @param other 另一个物品
     * @return 比较结果
     */
    @Override
    public int compareTo(Item other) {
        if (this.id < other.getItemId()) {
            return -1;
        } else if (this.id > other.getItemId()) {
            return 1;
        }
        return 0;
    }

    /**
     * 转换为字符串表示
     * @return 物品信息字符串
     */
    @Override
    public String toString() {
        return "Item: " + id + " quantity: " + quantity;
    }

    /**
     * 获取物品日志（不可修改视图）
     * @return 物品日志列表
     */
    public List<String> getItemLog() {
        return Collections.unmodifiableList(itemLog);
    }

    /**
     * 获取物品标志位
     * @return 标志位
     */
    public short getFlag() {
        return flag;
    }

    /**
     * 设置物品标志位（自动处理账号共享标志）
     * @param b 标志位
     */
    public void setFlag(short b) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (ii.isAccountRestricted(id)) {
            b |= ItemConstants.ACCOUNT_SHARING;
        }

        this.flag = b;
    }

    /**
     * 获取物品过期时间
     * @return 过期时间戳
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * 设置物品过期时间（永久物品不会过期）
     * @param expire 过期时间戳
     */
    public void setExpiration(long expire) {
        this.expiration = !ItemConstants.isPermanentItem(id) ? expire : ItemConstants.isPet(id) ? Long.MAX_VALUE : -1;
    }

    /**
     * 获取物品序列号
     * @return 序列号
     */
    public int getSN() {
        return sn;
    }

    /**
     * 设置物品序列号
     * @param sn 序列号
     */
    public void setSN(int sn) {
        this.sn = sn;
    }

    /**
     * 获取礼物赠送者名称
     * @return 赠送者名称
     */
    public String getGiftFrom() {
        return giftFrom;
    }

    /**
     * 设置礼物赠送者名称
     * @param giftFrom 赠送者名称
     */
    public void setGiftFrom(String giftFrom) {
        this.giftFrom = giftFrom;
    }

    /**
     * 获取宠物对象
     * @return 宠物对象
     */
    public Pet getPet() {
        return pet;
    }

    /**
     * 判断物品是否不可交易
     * @return 是否不可交易
     */
    public boolean isUntradeable() {
        return ((this.getFlag() & ItemConstants.UNTRADEABLE) == ItemConstants.UNTRADEABLE) || (ItemInformationProvider.getInstance().isDropRestricted(this.getItemId()) && !KarmaManipulator.hasKarmaFlag(this));
    }
}
