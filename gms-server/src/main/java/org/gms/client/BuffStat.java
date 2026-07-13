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
package org.gms.client;

/**
 * Buff状态枚举类
 * 定义了游戏中各种增益/减益效果(Buff/Debuff)的位掩码标识
 * 用于角色状态效果的管理、封包编码和状态计算
 *
 * @author OdinMS Team
 */
public enum BuffStat {
    //SLOW(0x1L),
    /** 变身 */
    MORPH(0x2L),
    /** 恢复 */
    RECOVERY(0x4L),
    /** 冒险岛勇士 */
    MAPLE_WARRIOR(0x8L),
    /** 稳如泰山 */
    STANCE(0x10L),
    /** 锐利之眼 */
    SHARP_EYES(0x20L),
    /** 魔法反射 */
    MANA_REFLECTION(0x40L),
    //ALWAYS_RIGHT(0X80L),
    /** 影网术 */
    SHADOW_CLAW(0x100L),
    /** 无限魔力 */
    INFINITY(0x200L),
    /** 圣灵之盾 */
    HOLY_SHIELD(0x400L),
    /** 击腿箭 */
    HAMSTRING(0x800L),
    /** 致盲 */
    BLIND(0x1000L),
    /** 精力集中 */
    CONCENTRATE(0x2000L),
    /** 傀儡 */
    PUPPET(0x4000L),
    /** 英雄回声 */
    ECHO_OF_HERO(0x8000L),
    /** 道具金币加成 */
    MESO_UP_BY_ITEM(0x10000L),
    /** 幽灵变身 */
    GHOST_MORPH(0x20000L),
    /** 光环 */
    AURA(0x40000L),
    /** 混乱 */
    CONFUSE(0x80000L),

    // ------ 双倍经验卡效果 ------
    /** 经验卡1 */
    COUPON_EXP1(0x100000L),
    /** 经验Buff */
    EXP_BUFF(0x40000000L),
    /** 经验卡2 */
    COUPON_EXP2(0x200000L),
    /** 经验卡3/4 */
    COUPON_EXP3(0x400000L), COUPON_EXP4(0x400000L),
    /** 掉宝卡1 */
    COUPON_DRP1(0x800000L),
    /** 掉宝卡2/3 */
    COUPON_DRP2(0x1000000L), COUPON_DRP3(0x1000000L),

    // ------ 怪物卡Buff效果，感谢Arnah (Vertisy) ------
    /** 道具掉率提升 */
    ITEM_UP_BY_ITEM(0x100000L),
    /** 物理攻击免疫 */
    RESPECT_PIMMUNE(0x200000L),
    /** 魔法攻击免疫 */
    RESPECT_MIMMUNE(0x400000L),
    /** 攻击防御 */
    DEFENSE_ATT(0x800000L),
    /** 状态防御 */
    DEFENSE_STATE(0x1000000L),

    /** HP恢复 */
    HPREC(0x2000000L),
    /** MP恢复 */
    MPREC(0x4000000L),
    /** 狂乱之怒 */
    BERSERK_FURY(0x8000000L),
    /** 神圣之体 */
    DIVINE_BODY(0x10000000L),
    /** 闪电 */
    SPARK(0x20000000L),
    /** 地图椅子 */
    MAP_CHAIR(0x40000000L),
    /** 终极攻击 */
    FINALATTACK(0x80000000L),
    /** 武器攻击力 */
    WATK(0x100000000L),
    /** 武器防御力 */
    WDEF(0x200000000L),
    /** 魔法攻击力 */
    MATK(0x400000000L),
    /** 魔法防御力 */
    MDEF(0x800000000L),
    /** 命中率 */
    ACC(0x1000000000L),
    /** 回避率 */
    AVOID(0x2000000000L),
    /** 手技 */
    HANDS(0x4000000000L),
    /** 移动速度 */
    SPEED(0x8000000000L),
    /** 跳跃力 */
    JUMP(0x10000000000L),
    /** 魔法盾 */
    MAGIC_GUARD(0x20000000000L),
    /** 隐身术 */
    DARKSIGHT(0x40000000000L),
    /** 快速武器 */
    BOOSTER(0x80000000000L),
    /** 伤害反击 */
    POWERGUARD(0x100000000000L),
    /** 神圣之火(HP) */
    HYPERBODYHP(0x200000000000L),
    /** 神圣之火(MP) */
    HYPERBODYMP(0x400000000000L),
    /** 无敌 */
    INVINCIBLE(0x800000000000L),
    /** 无形箭 */
    SOULARROW(0x1000000000000L),
    /** 眩晕 */
    STUN(0x2000000000000L),
    /** 中毒 */
    POISON(0x4000000000000L),
    /** 封印 */
    SEAL(0x8000000000000L),
    /** 黑暗 */
    DARKNESS(0x10000000000000L),
    /** 连击 */
    COMBO(0x20000000000000L),
    /** 召唤兽 */
    SUMMON(0x20000000000000L),
    /** 属性 Charges */
    WK_CHARGE(0x40000000000000L),
    /** 龙之魂 */
    DRAGONBLOOD(0x80000000000000L),
    /** 神圣祈祷 */
    HOLY_SYMBOL(0x100000000000000L),
    /** 金币获取 */
    MESOUP(0x200000000000000L),
    /** 影子伙伴 */
    SHADOWPARTNER(0x400000000000000L),
    /** 偷窃 */
    PICKPOCKET(0x800000000000000L),
    /** 金币护盾 */
    MESOGUARD(0x1000000000000000L),
    /** 经验值增加 */
    EXP_INCREASE(0x2000000000000000L),
    /** 虚弱 */
    WEAKEN(0x4000000000000000L),
    /** 地图保护 */
    MAP_PROTECTION(0x8000000000000000L),

    // 以下为不正确的BuffStat（位掩码冲突）
    /** 减速 */
    SLOW(0x200000000L, true),
    /** 元素重置 */
    ELEMENTAL_RESET(0x200000000L, true),
    /** 魔法护盾 */
    MAGIC_SHIELD(0x400000000L, true),
    /** 魔法抗性 */
    MAGIC_RESISTANCE(0x800000000L, true),
    // 需要灵魂石
    // 不正确的BuffStat结束

    /** 风之漫步 */
    WIND_WALK(0x400000000L, true),
    /** 战神连击 */
    ARAN_COMBO(0x1000000000L, true),
    /** 连击吸血 */
    COMBO_DRAIN(0x2000000000L, true),
    /** 连击屏障 */
    COMBO_BARRIER(0x4000000000L, true),
    /** 身体压力 */
    BODY_PRESSURE(0x8000000000L, true),
    /** 智能击退 */
    SMART_KNOCKBACK(0x10000000000L, true),
    /** 狂战士 */
    BERSERK(0x20000000000L, true),
    /** 能量充能 */
    ENERGY_CHARGE(0x4000000000000L, true),
    /** 冲刺2(速度) */
    DASH2(0x8000000000000L, true),
    /** 冲刺(跳跃) */
    DASH(0x10000000000000L, true),
    /** 怪物骑乘 */
    MONSTER_RIDING(0x20000000000000L, true),
    /** 极速领域 */
    SPEED_INFUSION(0x40000000000000L, true),
    /**  homing beacon */
    HOMING_BEACON(0x80000000000000L, true);

    /** Buff状态位掩码值 */
    private final long i;
    /** 是否为第一组位掩码（用于解决位掩码冲突） */
    private final boolean isFirst;

    /**
     * 构造函数（带isFirst参数）
     * @param i 位掩码值
     * @param isFirst 是否为第一组
     */
    BuffStat(long i, boolean isFirst) {
        this.i = i;
        this.isFirst = isFirst;
    }

    /**
     * 构造函数（默认isFirst为false）
     * @param i 位掩码值
     */
    BuffStat(long i) {
        this.i = i;
        this.isFirst = false;
    }

    /**
     * 获取位掩码值
     * @return 位掩码值
     */
    public long getValue() {
        return i;
    }

    /**
     * 判断是否为第一组位掩码
     * @return 是否为第一组
     */
    public boolean isFirst() {
        return isFirst;
    }

    /**
     * 返回Buff名称字符串
     * @return Buff名称
     */
    @Override
    public String toString() {
        return name();
    }
}
