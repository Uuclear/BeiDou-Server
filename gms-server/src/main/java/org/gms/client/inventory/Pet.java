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
import org.gms.util.CashIdGenerator;
import org.gms.constants.game.ExpTable;
import org.gms.server.ItemInformationProvider;
import org.gms.server.movement.AbsoluteLifeMovement;
import org.gms.server.movement.LifeMovement;
import org.gms.server.movement.LifeMovementFragment;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 宠物类
 * 继承自Item类，表示游戏中的宠物道具
 * 包含宠物的名称、等级、亲密度、饱满度、位置、召唤状态等属性
 * 支持宠物从数据库加载、保存、创建、删除等操作
 *
 * @author Matze
 */
public class Pet extends Item {
    /** 宠物名称 */
    private String name;
    /** 宠物唯一ID */
    private int uniqueid;
    /** 亲密度（0-30000） */
    private int tameness = 0;
    /** 宠物等级（1-30） */
    private byte level = 1;
    /** 饱满度（0-100） */
    private int fullness = 100;
    /**  foothold ID */
    private int Fh;
    /** 宠物位置坐标 */
    private Point pos;
    /** 宠物姿态 */
    private int stance;
    /** 是否已召唤 */
    private boolean summoned;
    /** 宠物属性标志位 */
    private int petAttribute = 0;

    /**
     * 宠物属性枚举
     */
    public enum PetAttribute {
        /** 跟随主人速度 */
        OWNER_SPEED(0x01);

        private final int i;

        PetAttribute(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }
    }

    /**
     * 私有构造函数
     * @param id 宠物物品ID
     * @param position 背包位置
     * @param uniqueid 宠物唯一ID
     */
    private Pet(int id, short position, int uniqueid) {
        super(id, position, (short) 1);
        this.uniqueid = uniqueid;
        this.pos = new Point(0, 0);
    }

    /**
     * 从数据库加载宠物数据
     * @param itemid 物品ID
     * @param position 背包位置
     * @param petid 宠物ID
     * @return 宠物对象
     */
    public static Pet loadFromDb(int itemid, short position, int petid) {
        Pet ret = new Pet(itemid, position, petid);
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT name, level, closeness, fullness, summoned, flag FROM pets WHERE petid = ?")) {
            ps.setInt(1, petid);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                ret.setName(rs.getString("name"));
                ret.setTameness(Math.min(rs.getInt("closeness"), 30000));
                ret.setLevel((byte) Math.min(rs.getByte("level"), 30));
                ret.setFullness(Math.min(rs.getInt("fullness"), 100));
                ret.setSummoned(rs.getInt("summoned") == 1);
                ret.setPetAttribute(rs.getInt("flag"));
            }
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从数据库删除宠物
     * @param owner 宠物主人
     * @param petid 宠物ID
     */
    public static void deleteFromDb(Character owner, int petid) {
        try {
            owner.deletePetExcludedData(petid);
            CashIdGenerator.freeCashId(petid);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 保存宠物数据到数据库
     */
    public void saveToDb() {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE pets SET name = ?, level = ?, closeness = ?, fullness = ?, summoned = ?, flag = ? WHERE petid = ?")) {
            ps.setString(1, getName());
            ps.setInt(2, getLevel());
            ps.setInt(3, getTameness());
            ps.setInt(4, getFullness());
            ps.setInt(5, isSummoned() ? 1 : 0);
            ps.setInt(6, getPetAttribute());
            ps.setInt(7, getUniqueId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建新宠物（默认状态）
     * @param itemid 物品ID
     * @return 新宠物的唯一ID
     */
    public static int createPet(int itemid) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO pets (petid, name, level, closeness, fullness, summoned, flag) VALUES (?, ?, 1, 0, 100, 0, 0)")) {
            int ret = CashIdGenerator.generateCashId();
            ps.setInt(1, ret);
            ps.setString(2, ItemInformationProvider.getInstance().getName(itemid));
            ps.executeUpdate();
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 创建新宠物（指定状态）
     * @param itemid 物品ID
     * @param level 等级
     * @param tameness 亲密度
     * @param fullness 饱满度
     * @return 新宠物的唯一ID
     */
    public static int createPet(int itemid, byte level, int tameness, int fullness) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO pets (petid, name, level, closeness, fullness, summoned, flag) VALUES (?, ?, ?, ?, ?, 0, 0)")) {
            int ret = CashIdGenerator.generateCashId();
            ps.setInt(1, ret);
            ps.setString(2, ItemInformationProvider.getInstance().getName(itemid));
            ps.setByte(3, level);
            ps.setInt(4, tameness);
            ps.setInt(5, fullness);
            ps.executeUpdate();
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取宠物名称
     * @return 宠物名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置宠物名称
     * @param name 宠物名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取宠物唯一ID
     * @return 唯一ID
     */
    public int getUniqueId() {
        return uniqueid;
    }

    /**
     * 设置宠物唯一ID
     * @param id 唯一ID
     */
    public void setUniqueId(int id) {
        this.uniqueid = id;
    }

    /**
     * 获取亲密度
     * @return 亲密度
     */
    public int getTameness() {
        return tameness;
    }

    /**
     * 设置亲密度
     * @param tameness 亲密度
     */
    public void setTameness(int tameness) {
        this.tameness = tameness;
    }

    /**
     * 获取宠物等级
     * @return 等级
     */
    public byte getLevel() {
        return level;
    }

    /**
     * 增加宠物亲密度和饱满度
     * @param owner 宠物主人
     * @param incTameness 亲密度增量
     * @param incFullness 饱满度增量
     * @param type 类型
     */
    public void gainTamenessFullness(Character owner, int incTameness, int incFullness, int type) {
        gainTamenessFullness(owner, incTameness, incFullness, type, false);
    }

    /**
     * 增加宠物亲密度和饱满度（可强制喂食）
     * @param owner 宠物主人
     * @param incTameness 亲密度增量
     * @param incFullness 饱满度增量
     * @param type 类型
     * @param forceEnjoy 是否强制喂食（商城物品）
     */
    public void gainTamenessFullness(Character owner, int incTameness, int incFullness, int type, boolean forceEnjoy) {
        byte slot = owner.getPetIndex(this);
        boolean enjoyed;

        if (fullness < 100 || incFullness == 0 || forceEnjoy) {
            int newFullness = fullness + incFullness;
            if (newFullness > 100) {
                newFullness = 100;
            }
            fullness = newFullness;

            if (incTameness > 0 && tameness < 30000) {
                int newTameness = tameness + incTameness;
                if (newTameness > 30000) {
                    newTameness = 30000;
                }

                tameness = newTameness;
                while (newTameness >= ExpTable.getTamenessNeededForLevel(level)) {
                    level += 1;
                    owner.sendPacket(PacketCreator.showOwnPetLevelUp(slot));
                    owner.getMap().broadcastMessage(PacketCreator.showPetLevelUp(owner, slot));
                }
            }

            enjoyed = true;
        } else {
            int newTameness = tameness - 1;
            if (newTameness < 0) {
                newTameness = 0;
            }

            tameness = newTameness;
            if (level > 1 && newTameness < ExpTable.getTamenessNeededForLevel(level - 1)) {
                level -= 1;
            }

            enjoyed = false;
        }

        owner.getMap().broadcastMessage(PacketCreator.petFoodResponse(owner.getId(), slot, enjoyed, owner.hasPetChatballoon(slot)));
        saveToDb();

        Item petz = owner.getInventory(InventoryType.CASH).getItem(getPosition());
        if (petz != null) {
            owner.forceUpdateItem(petz);
        }
    }

    /**
     * 设置宠物等级
     * @param level 等级
     */
    public void setLevel(byte level) {
        this.level = level;
    }

    /**
     * 获取饱满度
     * @return 饱满度
     */
    public int getFullness() {
        return fullness;
    }

    /**
     * 设置饱满度
     * @param fullness 饱满度
     */
    public void setFullness(int fullness) {
        this.fullness = fullness;
    }

    /**
     * 获取Foothold ID
     * @return Fh ID
     */
    public int getFh() {
        return Fh;
    }

    /**
     * 设置Foothold ID
     * @param Fh Fh ID
     */
    public void setFh(int Fh) {
        this.Fh = Fh;
    }

    /**
     * 获取宠物位置
     * @return 位置坐标
     */
    public Point getPos() {
        return pos;
    }

    /**
     * 设置宠物位置
     * @param pos 位置坐标
     */
    public void setPos(Point pos) {
        this.pos = pos;
    }

    /**
     * 获取宠物姿态
     * @return 姿态
     */
    public int getStance() {
        return stance;
    }

    /**
     * 设置宠物姿态
     * @param stance 姿态
     */
    public void setStance(int stance) {
        this.stance = stance;
    }

    /**
     * 判断宠物是否已召唤
     * @return 是否已召唤
     */
    public boolean isSummoned() {
        return summoned;
    }

    /**
     * 设置宠物召唤状态
     * @param yes 是否召唤
     */
    public void setSummoned(boolean yes) {
        this.summoned = yes;
    }

    /**
     * 获取宠物属性标志
     * @return 属性标志
     */
    public int getPetAttribute() {
        return this.petAttribute;
    }

    /**
     * 设置宠物属性标志
     * @param flag 属性标志
     */
    private void setPetAttribute(int flag) {
        this.petAttribute = flag;
    }

    /**
     * 添加宠物属性
     * @param owner 宠物主人
     * @param flag 属性标志
     */
    public void addPetAttribute(Character owner, PetAttribute flag) {
        this.petAttribute |= flag.getValue();
        saveToDb();

        Item petz = owner.getInventory(InventoryType.CASH).getItem(getPosition());
        if (petz != null) {
            owner.forceUpdateItem(petz);
        }
    }

    /**
     * 移除宠物属性
     * @param owner 宠物主人
     * @param flag 属性标志
     */
    public void removePetAttribute(Character owner, PetAttribute flag) {
        this.petAttribute &= 0xFFFFFFFF ^ flag.getValue();
        saveToDb();

        Item petz = owner.getInventory(InventoryType.CASH).getItem(getPosition());
        if (petz != null) {
            owner.forceUpdateItem(petz);
        }
    }

    /**
     * 检查宠物是否可以消耗指定物品
     * @param itemId 物品ID
     * @return 消耗结果和好感度增量
     */
    public Pair<Integer, Boolean> canConsume(int itemId) {
        return ItemInformationProvider.getInstance().canPetConsume(this.getItemId(), itemId);
    }

    /**
     * 更新宠物位置（根据移动片段）
     * @param movement 移动片段列表
     */
    public void updatePosition(List<LifeMovementFragment> movement) {
        for (LifeMovementFragment move : movement) {
            if (move instanceof LifeMovement) {
                if (move instanceof AbsoluteLifeMovement) {
                    this.setPos(move.getPosition());
                }
                this.setStance(((LifeMovement) move).getNewstate());
            }
        }
    }
}
