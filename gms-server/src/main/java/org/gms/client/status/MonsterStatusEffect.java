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
package org.gms.client.status;

import org.gms.client.Skill;
import org.gms.server.life.MobSkill;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 怪物状态效果实例，记录状态类型、技能来源及持续时间。
 */
public class MonsterStatusEffect {

    private final Map<MonsterStatus, Integer> stati;
    private final Skill skill;
    private final MobSkill mobskill;
    private final boolean monsterSkill;

    /**
     * 怪物状态效果
     * @param stati stati
     * @param skillId 技能ID
     * @param mobskill mobskill
     * @param monsterSkill monsterSkill
     */
    public MonsterStatusEffect(Map<MonsterStatus, Integer> stati, Skill skillId, MobSkill mobskill, boolean monsterSkill) {
        this.stati = new ConcurrentHashMap<>(stati);
        this.skill = skillId;
        this.monsterSkill = monsterSkill;
        this.mobskill = mobskill;
    }

    /**
     * 获取Stati
     * @return 返回值
     */
    public Map<MonsterStatus, Integer> getStati() {
        return stati;
    }

    /**
     * 设置值
     * @param status status
     * @param newVal newVal
     * @return 返回值
     */
    public Integer setValue(MonsterStatus status, Integer newVal) {
        return stati.put(status, newVal);
    }

    /**
     * 获取技能
     * @return 返回值
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * 判断是否为怪物技能
     * @return 返回值
     */
    public boolean isMonsterSkill() {
        return monsterSkill;
    }

    /**
     * 移除活跃状态
     * @param stat 属性
     */
    public void removeActiveStatus(MonsterStatus stat) {
        stati.remove(stat);
    }

    /**
     * 获取Mob技能
     * @return 返回值
     */
    public MobSkill getMobSkill() {
        return mobskill;
    }
}
