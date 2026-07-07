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
 * 宠物数据模型，管理宠物的名称、等级、亲密度、饥饿度及技能。
 */
public class Pet extends Item {
    private String name;
    private int uniqueid;
    private int tameness = 0;
    private byte level = 1;
    private int fullness = 100;
    private int Fh;
    private Point pos;
    private int stance;
    private boolean summoned;
    private int petAttribute = 0;

    /**
     * PetAttribute枚举，定义相关常量值
     */
    public enum PetAttribute {
        OWNER_SPEED(0x01);

        private final int i;

        PetAttribute(int i) {
            this.i = i;
        }

        /**
         * 获取值
         * @return 返回值
         */
        public int getValue() {
            return i;
        }
    }

    private Pet(int id, short position, int uniqueid) {
        super(id, position, (short) 1);
        this.uniqueid = uniqueid;
        this.pos = new Point(0, 0);
    }

    /**
     * 加载从Db
     * @param itemid itemid
     * @param position 位置
     * @param petid petid
     * @return 返回值
     */
    public static Pet loadFromDb(int itemid, short position, int petid) {
        Pet ret = new Pet(itemid, position, petid);
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT name, level, closeness, fullness, summoned, flag FROM pets WHERE petid = ?")) { // Get the pet details...
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
     * 删除从Db
     * @param owner 所有者
     * @param petid petid
     */
    public static void deleteFromDb(Character owner, int petid) {
        try {
            // 宠物基础数据删除后，petignores 会通过外键级联清理，这里同步移除角色内存中的缓存。
            owner.deletePetExcludedData(petid);
            CashIdGenerator.freeCashId(petid);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 保存到Db
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
     * 创建宠物
     * @param itemid itemid
     * @return 返回值
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
     * 创建宠物
     * @param itemid itemid
     * @param level 等级
     * @param tameness tameness
     * @param fullness fullness
     * @return 返回值
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
     * 获取名称
     * @return 返回值
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取UniqueID
     * @return 返回值
     */
    public int getUniqueId() {
        return uniqueid;
    }

    /**
     * 设置UniqueID
     * @param id ID
     */
    public void setUniqueId(int id) {
        this.uniqueid = id;
    }

    /**
     * 获取Tameness
     * @return 返回值
     */
    public int getTameness() {
        return tameness;
    }

    /**
     * 设置Tameness
     * @param tameness tameness
     */
    public void setTameness(int tameness) {
        this.tameness = tameness;
    }

    /**
     * 获取等级
     * @return 返回值
     */
    public byte getLevel() {
        return level;
    }

    /**
     * 获得TamenessFullness
     * @param owner 所有者
     * @param incTameness incTameness
     * @param incFullness incFullness
     * @param type 类型
     */
    public void gainTamenessFullness(Character owner, int incTameness, int incFullness, int type) {
        gainTamenessFullness(owner, incTameness, incFullness, type, false);
    }

    /**
     * 获得TamenessFullness
     * @param owner 所有者
     * @param incTameness incTameness
     * @param incFullness incFullness
     * @param type 类型
     * @param forceEnjoy forceEnjoy
     */
    public void gainTamenessFullness(Character owner, int incTameness, int incFullness, int type, boolean forceEnjoy) {
        byte slot = owner.getPetIndex(this);
        boolean enjoyed;

        //will NOT increase pet's tameness if tried to feed pet with 100% fullness
        // unless forceEnjoy == true (cash shop)
        if (fullness < 100 || incFullness == 0 || forceEnjoy) {   //incFullness == 0: command given
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
     * 设置等级
     * @param level 等级
     */
    public void setLevel(byte level) {
        this.level = level;
    }

    /**
     * 获取Fullness
     * @return 返回值
     */
    public int getFullness() {
        return fullness;
    }

    /**
     * 设置Fullness
     * @param fullness fullness
     */
    public void setFullness(int fullness) {
        this.fullness = fullness;
    }

    /**
     * 获取Fh
     * @return 返回值
     */
    public int getFh() {
        return Fh;
    }

    /**
     * 设置Fh
     * @param Fh Fh
     */
    public void setFh(int Fh) {
        this.Fh = Fh;
    }

    /**
     * 获取Pos
     * @return 返回值
     */
    public Point getPos() {
        return pos;
    }

    /**
     * 设置Pos
     * @param pos pos
     */
    public void setPos(Point pos) {
        this.pos = pos;
    }

    /**
     * 获取Stance
     * @return 返回值
     */
    public int getStance() {
        return stance;
    }

    /**
     * 设置Stance
     * @param stance stance
     */
    public void setStance(int stance) {
        this.stance = stance;
    }

    /**
     * 判断是否为Summoned
     * @return 返回值
     */
    public boolean isSummoned() {
        return summoned;
    }

    /**
     * 设置Summoned
     * @param yes yes
     */
    public void setSummoned(boolean yes) {
        this.summoned = yes;
    }

    /**
     * 获取宠物Attribute
     * @return 返回值
     */
    public int getPetAttribute() {
        return this.petAttribute;
    }

    private void setPetAttribute(int flag) {
        this.petAttribute = flag;
    }

    /**
     * 添加宠物Attribute
     * @param owner 所有者
     * @param flag 标记
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
     * 移除宠物Attribute
     * @param owner 所有者
     * @param flag 标记
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
     * 判断是否可以Consume
     * @param itemId 物品ID
     * @return 返回值
     */
    public Pair<Integer, Boolean> canConsume(int itemId) {
        return ItemInformationProvider.getInstance().canPetConsume(this.getItemId(), itemId);
    }

    /**
     * 更新位置
     * @param movement movement
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
