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

import org.gms.constants.game.GameConstants;
import org.gms.server.life.MobSkillType;

import java.util.Arrays;

/**
 * 疾病/异常状态枚举类
 * 定义了游戏中角色和怪物可能受到的各种负面状态效果
 * 每个状态对应一个位掩码值和关联的怪物技能类型
 *
 * @author OdinMS Team
 */
public enum Disease {
    /** 无状态 */
    NULL(0x0),
    /** 减速 */
    SLOW(0x1, MobSkillType.SLOW),
    /** 诱惑 */
    SEDUCE(0x80, MobSkillType.SEDUCE),
    /** 可钓鱼状态 */
    FISHABLE(0x100),
    /** 僵尸化 */
    ZOMBIFY(0x4000),
    /** 方向混乱 */
    CONFUSE(0x80000, MobSkillType.REVERSE_INPUT),
    /** 眩晕 */
    STUN(0x2000000000000L, MobSkillType.STUN),
    /** 中毒 */
    POISON(0x4000000000000L, MobSkillType.POISON),
    /** 技能封印 */
    SEAL(0x8000000000000L, MobSkillType.SEAL),
    /** 黑暗(命中率降低) */
    DARKNESS(0x10000000000000L, MobSkillType.DARKNESS),
    /** 虚弱(无法跳跃) */
    WEAKEN(0x4000000000000000L, MobSkillType.WEAKNESS),
    /** 诅咒 */
    CURSE(0x8000000000000000L, MobSkillType.CURSE);

    /** 疾病状态位掩码值 */
    private final long i;
    /** 关联的怪物技能类型 */
    private final MobSkillType mobSkillType;

    /**
     * 构造函数（无关联怪物技能）
     * @param i 位掩码值
     */
    Disease(long i) {
        this(i, null);
    }

    /**
     * 构造函数（有关联怪物技能）
     * @param i 位掩码值
     * @param skill 关联的怪物技能类型
     */
    Disease(long i, MobSkillType skill) {
        this.i = i;
        this.mobSkillType = skill;
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
     * @return 始终返回false（Disease不分组）
     */
    public boolean isFirst() {
        return false;
    }

    /**
     * 获取关联的怪物技能类型
     * @return 怪物技能类型
     */
    public MobSkillType getMobSkillType() {
        return mobSkillType;
    }

    /**
     * 根据序号获取疾病枚举
     * @param ord 序号
     * @return 对应的Disease枚举，越界则返回NULL
     */
    public static Disease ordinal(int ord) {
        try {
            return Disease.values()[ord];
        } catch (IndexOutOfBoundsException io) {
            return NULL;
        }
    }

    /**
     * 获取随机疾病（用于怪物嘉年华等）
     * @return 随机选择的疾病
     */
    public static final Disease getRandom() {
        Disease[] diseases = GameConstants.CPQ_DISEASES;
        return diseases[(int) (Math.random() * diseases.length)];
    }

    /**
     * 根据怪物技能类型获取对应的疾病
     * @param skill 怪物技能类型
     * @return 对应的Disease枚举，如果未找到则返回null
     */
    public static final Disease getBySkill(MobSkillType skill) {
        if (skill == null) {
            return null;
        }
        return Arrays.stream(Disease.values())
                .filter(d -> d.mobSkillType == skill)
                .findAny()
                .orElse(null);
    }

}
