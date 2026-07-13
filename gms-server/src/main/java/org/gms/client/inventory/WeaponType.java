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

/**
 * 武器类型枚举类
 * 定义了游戏中各种武器类型及其伤害系数
 * 伤害系数用于计算角色的最大基础伤害
 *
 * @author OdinMS Team
 */
public enum WeaponType {
    /** 不是武器 */
    NOT_A_WEAPON(0),
    /** 单手剑/斧/钝器 劈砍 */
    GENERAL1H_SWING(4.4),
    /** 单手剑/斧/钝器 刺击 */
    GENERAL1H_STAB(3.2),
    /** 双手剑/斧/钝器 劈砍 */
    GENERAL2H_SWING(4.8),
    /** 双手剑/斧/钝器 刺击 */
    GENERAL2H_STAB(3.4),
    /** 弓 */
    BOW(3.4),
    /** 拳套 */
    CLAW(3.6),
    /** 弩 */
    CROSSBOW(3.6),
    /** 短刀（飞侠用） */
    DAGGER_THIEVES(3.6),
    /** 短刀（其他职业用） */
    DAGGER_OTHER(4),
    /** 枪 */
    GUN(3.6),
    /** 指节 */
    KNUCKLE(4.8),
    /** 长柄武器 劈砍 */
    POLE_ARM_SWING(5.0),
    /** 长柄武器 刺击 */
    POLE_ARM_STAB(3.0),
    /** 枪 刺击 */
    SPEAR_STAB(5.0),
    /** 枪 劈砍 */
    SPEAR_SWING(3.0),
    /** 短杖 */
    STAFF(3.6),
    /** 单手剑 */
    SWORD1H(4.0),
    /** 双手剑 */
    SWORD2H(4.6),
    /** 长杖 */
    WAND(3.6);

    /** 最大伤害系数 */
    private final double damageMultiplier;

    /**
     * 构造函数
     * @param maxDamageMultiplier 最大伤害系数
     */
    WeaponType(double maxDamageMultiplier) {
        this.damageMultiplier = maxDamageMultiplier;
    }

    /**
     * 获取最大伤害系数
     * @return 伤害系数
     */
    public double getMaxDamageMultiplier() {
        return damageMultiplier;
    }
}
