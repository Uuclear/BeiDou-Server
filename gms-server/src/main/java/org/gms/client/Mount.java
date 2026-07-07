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
/**
 * 坐骑数据模型，管理坐骑 ID、等级、疲劳度及技能。
 */
public class Mount {
    private int itemid;
    private int skillid;
    private int tiredness;
    private int exp;
    private int level;
    private Character owner;
    private boolean active;

    /**
     * 坐骑
     * @param owner 所有者
     * @param id ID
     * @param skillid skillid
     */
    public Mount(Character owner, int id, int skillid) {
        this.itemid = id;
        this.skillid = skillid;
        this.tiredness = 0;
        this.level = 1;
        this.exp = 0;
        this.owner = owner;
        active = true;
    }

    /**
     * 获取物品ID
     * @return 返回值
     */
    public int getItemId() {
        return itemid;
    }

    /**
     * 获取技能ID
     * @return 返回值
     */
    public int getSkillId() {
        return skillid;
    }

    /**
     * 1902000 - Hog
     * 1902001 - Silver Mane
     * 1902002 - Red Draco
     * 1902005 - Mimiana
     * 1902006 - Mimio
     * 1902007 - Shinjou
     * 1902008 - Frog
     * 1902009 - Ostrich
     * 1902010 - Frog
     * 1902011 - Turtle
     * 1902012 - Yeti
     *
     * @return the id
     */
    /**
     * 获取ID
     * @return 返回值
     */
    public int getId() {
        if (this.itemid < 1903000) {
            return itemid - 1901999;
        }
        return 5;
    }

    /**
     * 获取Tiredness
     * @return 返回值
     */
    public int getTiredness() {
        return tiredness;
    }

    /**
     * 获取经验
     * @return 返回值
     */
    public int getExp() {
        return exp;
    }

    /**
     * 获取等级
     * @return 返回值
     */
    public int getLevel() {
        return level;
    }

    /**
     * 设置Tiredness
     * @param newtiredness newtiredness
     */
    public void setTiredness(int newtiredness) {
        this.tiredness = newtiredness;
        if (tiredness < 0) {
            tiredness = 0;
        }
    }

    /**
     * incrementAndGetTiredness
     * @return 返回值
     */
    public int incrementAndGetTiredness() {
        this.tiredness++;
        return this.tiredness;
    }

    /**
     * 设置经验
     * @param newexp newexp
     */
    public void setExp(int newexp) {
        this.exp = newexp;
    }

    /**
     * 设置等级
     * @param newlevel newlevel
     */
    public void setLevel(int newlevel) {
        this.level = newlevel;
    }

    /**
     * 设置物品ID
     * @param newitemid newitemid
     */
    public void setItemId(int newitemid) {
        this.itemid = newitemid;
    }

    /**
     * 设置技能ID
     * @param newskillid newskillid
     */
    public void setSkillId(int newskillid) {
        this.skillid = newskillid;
    }

    /**
     * 设置活跃
     * @param set set
     */
    public void setActive(boolean set) {
        this.active = set;
    }

    /**
     * 判断是否为活跃
     * @return 返回值
     */
    public boolean isActive() {
        return active;
    }

    /**
     * empty
     */
    /**
     * empty
     */
    /**
     * 清空数据
     */
    public void empty() {
        if (owner != null) {
            owner.getClient().getWorldServer().unregisterMountHunger(owner);
        }
        this.owner = null;
    }
}
