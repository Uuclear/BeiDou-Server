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
/**
 * 角色创建配方类，封装创建角色所需的初始属性、装备和技能配置。
 */
public class CharacterFactoryRecipe {
    private final Job job;
    private final int level;
    private final int map;
    private final int top;
    private final int bottom;
    private final int shoes;
    private final int weapon;
    private int str = 4, dex = 4, int_ = 4, luk = 4;
    private int maxHp = 50, maxMp = 5;
    private int ap = 0, sp = 0;
    private int meso = 0;
    private final List<Pair<Skill, Integer>> skills = new LinkedList<>();

    private final List<Pair<Item, InventoryType>> itemsWithType = new LinkedList<>();
    private final Map<InventoryType, AtomicInteger> runningTypePosition = new LinkedHashMap<>();

    /**
     * 角色工厂Recipe
     * @param job job
     * @param level 等级
     * @param map map
     * @param top top
     * @param bottom bottom
     * @param shoes shoes
     * @param weapon weapon
     */
    public CharacterFactoryRecipe(Job job, int level, int map, int top, int bottom, int shoes, int weapon) {
        this.job = job;
        this.level = level;
        this.map = map;
        this.top = top;
        this.bottom = bottom;
        this.shoes = shoes;
        this.weapon = weapon;

        if (!GameConfig.getServerBoolean("use_starting_ap_4")) {
            if (GameConfig.getServerBoolean("use_auto_assign_starters_ap")) {
                str = 12;
                dex = 5;
            } else {
                ap = 9;
            }
        }
    }

    /**
     * 设置力量
     * @param v v
     */
    public void setStr(int v) {
        str = v;
    }

    /**
     * 设置敏捷
     * @param v v
     */
    public void setDex(int v) {
        dex = v;
    }

    /**
     * 设置智力
     * @param v v
     */
    public void setInt(int v) {
        int_ = v;
    }

    /**
     * 设置运气
     * @param v v
     */
    public void setLuk(int v) {
        luk = v;
    }

    /**
     * 设置最大HP
     * @param v v
     */
    public void setMaxHp(int v) {
        maxHp = v;
    }

    /**
     * 设置最大MP
     * @param v v
     */
    public void setMaxMp(int v) {
        maxMp = v;
    }

    /**
     * 设置RemainingAp
     * @param v v
     */
    public void setRemainingAp(int v) {
        ap = v;
    }

    /**
     * 设置RemainingSp
     * @param v v
     */
    public void setRemainingSp(int v) {
        sp = v;
    }

    /**
     * 设置金币
     * @param v v
     */
    public void setMeso(int v) {
        meso = v;
    }

    /**
     * 添加Starting技能等级
     * @param skill 技能
     * @param level 等级
     */
    public void addStartingSkillLevel(Skill skill, int level) {
        skills.add(new Pair<>(skill, level));
    }

    /**
     * 添加StartingEquipment
     * @param eqpItem eqpItem
     */
    public void addStartingEquipment(Item eqpItem) {
        itemsWithType.add(new Pair<>(eqpItem, InventoryType.EQUIP));
    }

    /**
     * 添加Starting物品
     * @param itemid itemid
     * @param quantity 数量
     * @param itemType itemType
     */
    public void addStartingItem(int itemid, int quantity, InventoryType itemType) {
        AtomicInteger p = runningTypePosition.get(itemType);
        if (p == null) {
            p = new AtomicInteger(0);
            runningTypePosition.put(itemType, p);
        }

        itemsWithType.add(new Pair<>(new Item(itemid, (short) p.getAndIncrement(), (short) quantity), itemType));
    }

    /**
     * 获取职业
     * @return 返回值
     */
    public Job getJob() {
        return job;
    }

    /**
     * 获取等级
     * @return 返回值
     */
    public int getLevel() {
        return level;
    }

    /**
     * 获取地图
     * @return 返回值
     */
    public int getMap() {
        return map;
    }

    /**
     * 获取Top
     * @return 返回值
     */
    public int getTop() {
        return top;
    }

    /**
     * 获取Bottom
     * @return 返回值
     */
    public int getBottom() {
        return bottom;
    }

    /**
     * 获取Shoes
     * @return 返回值
     */
    public int getShoes() {
        return shoes;
    }

    /**
     * 获取Weapon
     * @return 返回值
     */
    public int getWeapon() {
        return weapon;
    }

    /**
     * 获取力量
     * @return 返回值
     */
    public int getStr() {
        return str;
    }

    /**
     * 获取敏捷
     * @return 返回值
     */
    public int getDex() {
        return dex;
    }

    /**
     * 获取智力
     * @return 返回值
     */
    public int getInt() {
        return int_;
    }

    /**
     * 获取运气
     * @return 返回值
     */
    public int getLuk() {
        return luk;
    }

    /**
     * 获取最大HP
     * @return 返回值
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * 获取最大MP
     * @return 返回值
     */
    public int getMaxMp() {
        return maxMp;
    }

    /**
     * 获取RemainingAp
     * @return 返回值
     */
    public int getRemainingAp() {
        return ap;
    }

    /**
     * 获取RemainingSp
     * @return 返回值
     */
    public int getRemainingSp() {
        return sp;
    }

    /**
     * 获取金币
     * @return 返回值
     */
    public int getMeso() {
        return meso;
    }

    /**
     * 获取初始技能等级
     * @return 返回值
     */
    public List<Pair<Skill, Integer>> getStartingSkillLevel() {
        return skills;
    }

    /**
     * 获取初始物品
     * @return 返回值
     */
    public List<Pair<Item, InventoryType>> getStartingItems() {
        return itemsWithType;
    }
}
