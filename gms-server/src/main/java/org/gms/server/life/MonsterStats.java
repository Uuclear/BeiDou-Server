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
package org.gms.server.life;

import org.gms.server.life.LifeFactory.BanishInfo;
import org.gms.server.life.LifeFactory.loseItem;
import org.gms.server.life.LifeFactory.selfDestruction;
import org.gms.util.Pair;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 怪物静态属性（HP、经验、防御、元素抗性、Boss 标志等）。
 */
public class MonsterStats {
    public boolean changeable;
    public int exp, hp, mp, level, PADamage, PDDamage, MADamage, MDDamage, dropPeriod, cp, buffToGive = -1, removeAfter, acc, eva;
    public boolean boss, undead, ffaLoot, isExplosiveReward, firstAttack, removeOnMiss;
    public String name;
    public Map<String, Integer> animationTimes = new HashMap<>();
    public Map<Element, ElementalEffectiveness> resistance = new HashMap<>();
    public List<Integer> revives = Collections.emptyList();
    public byte tagColor, tagBgColor;
    public Set<MobSkillId> skills = new HashSet<>();
    public Pair<Integer, Integer> cool = null;
    public BanishInfo banish = null;
    public List<loseItem> loseItem = null;
    public selfDestruction selfDestruction = null;
    public int fixedStance = 0;
    public boolean friendly;
    public int movetype = -1;    //怪物类型，-1=未知，0=stand（陆地），1=fly（飞天）
    public int imgwidth = 0;     //第一帧图片宽度
    public int imgheight = 0;    //第一帧图片高度
    public int bboxMinX = 0;
    public int bboxMinY = 0;
    public int bboxMaxX = 0;
    public int bboxMaxY = 0;
    public boolean bboxValid = false;

    /**
     * 设置Change。
     * @param change change
     */
    public void setChange(boolean change) {
        this.changeable = change;
    }

    /**
     * 判断是否为可变更。
     * @return boolean 类型结果
     */
    public boolean isChangeable() {
        return changeable;
    }

    /**
     * 获取经验。
     * @return int 类型结果
     */
    public int getExp() {
        return exp;
    }

    /**
     * 设置经验。
     * @param exp exp
     */
    public void setExp(int exp) {
        this.exp = exp;
    }

    /**
     * 获取HP。
     * @return int 类型结果
     */
    public int getHp() {
        return hp;
    }

    /**
     * 设置HP。
     * @param hp hp
     */
    public void setHp(int hp) {
        this.hp = hp;
    }

    /**
     * 获取MP。
     * @return int 类型结果
     */
    public int getMp() {
        return mp;
    }

    /**
     * 设置MP。
     * @param mp mp
     */
    public void setMp(int mp) {
        this.mp = mp;
    }

    /**
     * 获取等级。
     * @return int 类型结果
     */
    public int getLevel() {
        return level;
    }

    /**
     * 设置等级。
     * @param level level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 移除After。
     * @return int 类型结果
     */
    public int removeAfter() {
        return removeAfter;
    }

    /**
     * 设置移除、After。
     * @param removeAfter removeAfter
     */
    public void setRemoveAfter(int removeAfter) {
        this.removeAfter = removeAfter;
    }

    /**
     * 获取掉落、Period。
     * @return int 类型结果
     */
    public int getDropPeriod() {
        return dropPeriod;
    }

    /**
     * 设置掉落、Period。
     * @param dropPeriod dropPeriod
     */
    public void setDropPeriod(int dropPeriod) {
        this.dropPeriod = dropPeriod;
    }

    /**
     * 设置Boss。
     * @param boss boss
     */
    public void setBoss(boolean boss) {
        this.boss = boss;
    }

    /**
     * 判断是否为Boss。
     * @return boolean 类型结果
     */
    public boolean isBoss() {
        return boss;
    }

    /**
     * 设置Ffa战利品。
     * @param ffaLoot ffaLoot
     */
    public void setFfaLoot(boolean ffaLoot) {
        this.ffaLoot = ffaLoot;
    }

    /**
     * 判断是否为Ffa战利品。
     * @return boolean 类型结果
     */
    public boolean isFfaLoot() {
        return ffaLoot;
    }

    /**
     * 设置动画时间。
     * @param name name
     * @param delay 延迟（毫秒）
     */
    public void setAnimationTime(String name, int delay) {
        animationTimes.put(name, delay);
    }

    /**
     * 获取动画时间。
     * @param name name
     * @return int 类型结果
     */
    public int getAnimationTime(String name) {
        Integer ret = animationTimes.get(name);
        if (ret == null) {
            return 500;
        }
        return ret;
    }

    /**
     * 判断是否为Mobile。
     * @return boolean 类型结果
     */
    public boolean isMobile() {
        return animationTimes.containsKey("move") || animationTimes.containsKey("fly");
    }

    /**
     * 获取Revives。
     * @return List<Integer> 类型结果
     */
    public List<Integer> getRevives() {
        return revives;
    }

    /**
     * 设置Revives。
     * @param revives revives（Integer 列表/集合）
     */
    public void setRevives(List<Integer> revives) {
        this.revives = revives;
    }

    /**
     * 设置Undead。
     * @param undead undead
     */
    public void setUndead(boolean undead) {
        this.undead = undead;
    }

    /**
     * 判断是否为Undead。
     * @return boolean 类型结果
     */
    public boolean isUndead() {
        return undead;
    }

    /**
     * 设置克制。
     * @param e e
     * @param ee ee
     */
    public void setEffectiveness(Element e, ElementalEffectiveness ee) {
        resistance.put(e, ee);
    }

    /**
     * 获取克制。
     * @param e e
     * @return ElementalEffectiveness 类型结果
     */
    public ElementalEffectiveness getEffectiveness(Element e) {
        ElementalEffectiveness elementalEffectiveness = resistance.get(e);
        if (elementalEffectiveness == null) {
            return ElementalEffectiveness.NORMAL;
        } else {
            return elementalEffectiveness;
        }
    }

    /**
     * 获取名称。
     * @return String 类型结果
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称。
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取Tag、Color。
     * @return byte 类型结果
     */
    public byte getTagColor() {
        return tagColor;
    }

    /**
     * 设置Tag、Color。
     * @param tagColor tagColor
     */
    public void setTagColor(int tagColor) {
        this.tagColor = (byte) tagColor;
    }

    /**
     * 获取Tag、Bg、Color。
     * @return byte 类型结果
     */
    public byte getTagBgColor() {
        return tagBgColor;
    }

    /**
     * 设置Tag、Bg、Color。
     * @param tagBgColor tagBgColor
     */
    public void setTagBgColor(int tagBgColor) {
        this.tagBgColor = (byte) tagBgColor;
    }

    /**
     * 设置Skills。
     * @param skills skills（MobSkillId 列表/集合）
     */
    public void setSkills(Set<MobSkillId> skills) {
        this.skills = skills;
    }

    /**
     * 获取Skills。
     * @return Set<MobSkillId> 类型结果
     */
    public Set<MobSkillId> getSkills() {
        return Collections.unmodifiableSet(this.skills);
    }

    /**
     * 获取No、Skills。
     * @return int 类型结果
     */
    public int getNoSkills() {
        return this.skills.size();
    }

    /**
     * 判断是否拥有技能。
     * @param skillId skillId
     * @param level level
     * @return boolean 类型结果
     */
    public boolean hasSkill(int skillId, int level) {
        for (MobSkillId skill : skills) {
            if (skill.type().getId() == skillId && skill.level() == level) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置First、攻击。
     * @param firstAttack firstAttack
     */
    public void setFirstAttack(boolean firstAttack) {
        this.firstAttack = firstAttack;
    }

    /**
     * 判断是否为First、攻击。
     * @return boolean 类型结果
     */
    public boolean isFirstAttack() {
        return firstAttack;
    }

    /**
     * 设置Buff、到、Give。
     * @param buff buff
     */
    public void setBuffToGive(int buff) {
        this.buffToGive = buff;
    }

    /**
     * 获取Buff、到、Give。
     * @return int 类型结果
     */
    public int getBuffToGive() {
        return buffToGive;
    }

    void removeEffectiveness(Element e) {
        resistance.remove(e);
    }

    /**
     * 获取Banish、信息。
     * @return BanishInfo 类型结果
     */
    public BanishInfo getBanishInfo() {
        return banish;
    }

    /**
     * 设置Banish、信息。
     * @param banish banish
     */
    public void setBanishInfo(BanishInfo banish) {
        this.banish = banish;
    }

    /**
     * 获取PA伤害。
     * @return int 类型结果
     */
    public int getPADamage() {
        return PADamage;
    }

    /**
     * 设置PA伤害。
     * @param PADamage PADamage
     */
    public void setPADamage(int PADamage) {
        this.PADamage = PADamage;
    }

    /**
     * 获取CP。
     * @return int 类型结果
     */
    public int getCP() {
        return cp;
    }

    /**
     * 设置CP。
     * @param cp cp
     */
    public void setCP(int cp) {
        this.cp = cp;
    }

    /**
     * 执行 lose、物品 操作。
     * @return List<loseItem> 类型结果
     */
    public List<loseItem> loseItem() {
        return loseItem;
    }

    /**
     * 添加Lose、物品。
     * @param li li
     */
    public void addLoseItem(loseItem li) {
        if (loseItem == null) {
            loseItem = new LinkedList<>();
        }
        loseItem.add(li);
    }

    /**
     * 执行 self、Destruction 操作。
     * @return selfDestruction 类型结果
     */
    public selfDestruction selfDestruction() {
        return selfDestruction;
    }

    /**
     * 设置自身、Destruction。
     * @param sd sd
     */
    public void setSelfDestruction(selfDestruction sd) {
        this.selfDestruction = sd;
    }

    /**
     * 设置Explosive、Reward。
     * @param isExplosiveReward isExplosiveReward
     */
    public void setExplosiveReward(boolean isExplosiveReward) {
        this.isExplosiveReward = isExplosiveReward;
    }

    /**
     * 判断是否为Explosive、Reward。
     * @return boolean 类型结果
     */
    public boolean isExplosiveReward() {
        return isExplosiveReward;
    }

    /**
     * 设置移除、在、Miss。
     * @param removeOnMiss removeOnMiss
     */
    public void setRemoveOnMiss(boolean removeOnMiss) {
        this.removeOnMiss = removeOnMiss;
    }

    /**
     * 移除在、Miss。
     * @return boolean 类型结果
     */
    public boolean removeOnMiss() {
        return removeOnMiss;
    }

    /**
     * 设置Cool。
     * @param cool cool
     */
    public void setCool(Pair<Integer, Integer> cool) {
        this.cool = cool;
    }

    /**
     * 获取Cool。
     * @return Pair<Integer, Integer> 类型结果
     */
    public Pair<Integer, Integer> getCool() {
        return cool;
    }

    /**
     * 获取PD伤害。
     * @return int 类型结果
     */
    public int getPDDamage() {
        return PDDamage;
    }

    /**
     * 获取MA伤害。
     * @return int 类型结果
     */
    public int getMADamage() {
        return MADamage;
    }

    /**
     * 获取MD伤害。
     * @return int 类型结果
     */
    public int getMDDamage() {
        return MDDamage;
    }

    /**
     * 判断是否为友好。
     * @return boolean 类型结果
     */
    public boolean isFriendly() {
        return friendly;
    }

    /**
     * 设置友好。
     * @param value value
     */
    public void setFriendly(boolean value) {
        this.friendly = value;
    }

    /**
     * 设置PD伤害。
     * @param PDDamage PDDamage
     */
    public void setPDDamage(int PDDamage) {
        this.PDDamage = PDDamage;
    }

    /**
     * 设置MA伤害。
     * @param MADamage MADamage
     */
    public void setMADamage(int MADamage) {
        this.MADamage = MADamage;
    }

    /**
     * 设置MD伤害。
     * @param MDDamage MDDamage
     */
    public void setMDDamage(int MDDamage) {
        this.MDDamage = MDDamage;
    }

    /**
     * 获取Fixed、Stance。
     * @return int 类型结果
     */
    public int getFixedStance() {
        return this.fixedStance;
    }

    /**
     * 设置Fixed、Stance。
     * @param stance stance
     */
    public void setFixedStance(int stance) {
        this.fixedStance = stance;
    }

    /**
     * 怪物类型，-1=未知，0=stand（陆地怪物），1=fly（飞天怪物）
     * @return
     */
    public int getMovetype() {
        return movetype;
    }
    /**
     * 怪物类型，-1=未知，0=stand（陆地怪物），1=fly（飞天怪物）
     * @return
     */
    public void setMovetype(int movetype) {
        this.movetype = movetype;
    }

    /**
     * 设置第一帧图片的宽度
     * @param imgwidth
     */
    public void setImgwidth(int imgwidth) {
        this.imgwidth = imgwidth;
    }

    /**
     * 设置第一帧图片的高度
     * @param imgheight
     */
    public void setImgheight(int imgheight) {
        this.imgheight = imgheight;
    }

    /**
     * 取第一帧图片的宽度
     * @return
     */
    public int getImgwidth() {
        return this.imgwidth;
    }

    /**
     * 取第一帧图片的高度
     * @return
     */
    public int getImgheight() {
        return this.imgheight;
    }

    /**
     * 设置怪物碰撞框（相对 origin 的 lt/rb）
     */
    public void setBbox(int minX, int minY, int maxX, int maxY) {
        this.bboxMinX = minX;
        this.bboxMinY = minY;
        this.bboxMaxX = maxX;
        this.bboxMaxY = maxY;
        this.bboxValid = true;
    }

    /**
     * 是否已计算碰撞框
     */
    public boolean hasBbox() {
        return bboxValid;
    }

    /**
     * 碰撞框相对 lt.x
     */
    public int getBboxMinX() {
        return bboxMinX;
    }

    /**
     * 碰撞框相对 lt.y
     */
    public int getBboxMinY() {
        return bboxMinY;
    }

    /**
     * 碰撞框相对 rb.x
     */
    public int getBboxMaxX() {
        return bboxMaxX;
    }

    /**
     * 碰撞框相对 rb.y
     */
    public int getBboxMaxY() {
        return bboxMaxY;
    }

    /**
     * 碰撞框宽度（相对值）
     */
    public int getBboxWidth() {
        if (bboxValid) {
            return Math.max(0, bboxMaxX - bboxMinX);
        }
        return imgwidth;
    }

    /**
     * 碰撞框高度（相对值）
     */
    public int getBboxHeight() {
        if (bboxValid) {
            return Math.max(0, bboxMaxY - bboxMinY);
        }
        return imgheight;
    }

    /**
     * 大体型判定：用于决定是否启用碰撞框距离检测
     */
    public boolean isLargeSize() {
        int width = getBboxWidth();
        int height = getBboxHeight();
        if (width <= 0 || height <= 0) {
            return false;
        }
        // 宽高或面积满足阈值即可视为大体型
        return width >= 160 || height >= 160 || width * height >= 25000;
    }

    /**
     * 执行 copy 操作。
     * @return MonsterStats 类型结果
     */
    public MonsterStats copy() {
        MonsterStats copy = new MonsterStats();
        try {
            FieldCopyUtil.setFields(this, copy);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(10000);
            } catch (Exception ex) {

            }

        }

        return copy;
    }

    // FieldCopyUtil src: http://www.codesenior.com/en/tutorial/Java-Copy-Fields-From-One-Object-to-Another-Object-with-Reflection
    private static class FieldCopyUtil { // thanks to Codesenior dev team
        private static void setFields(Object from, Object to) {
            Field[] fields = from.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    Field fieldFrom = from.getClass().getDeclaredField(field.getName());
                    Object value = fieldFrom.get(from);
                    to.getClass().getDeclaredField(field.getName()).set(to, value);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
