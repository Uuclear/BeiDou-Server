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
 * 技能宏数据模型，存储玩家配置的技能快捷键组合。
 */
public class SkillMacro {
    private int skill1;
    private int skill2;
    private int skill3;
    private final String name;
    private final int shout;
    private final int position;

    /**
     * 技能宏
     * @param skill1 skill1
     * @param skill2 skill2
     * @param skill3 skill3
     * @param name 名称
     * @param shout shout
     * @param position 位置
     */
    public SkillMacro(int skill1, int skill2, int skill3, String name, int shout, int position) {
        this.skill1 = skill1;
        this.skill2 = skill2;
        this.skill3 = skill3;
        this.name = name;
        this.shout = shout;
        this.position = position;
    }

    /**
     * 获取Skill1
     * @return 返回值
     */
    public int getSkill1() {
        return skill1;
    }

    /**
     * 获取Skill2
     * @return 返回值
     */
    public int getSkill2() {
        return skill2;
    }

    /**
     * 获取Skill3
     * @return 返回值
     */
    public int getSkill3() {
        return skill3;
    }

    /**
     * 设置Skill1
     * @param skill 技能
     */
    public void setSkill1(int skill) {
        skill1 = skill;
    }

    /**
     * 设置Skill2
     * @param skill 技能
     */
    public void setSkill2(int skill) {
        skill2 = skill;
    }

    /**
     * 设置Skill3
     * @param skill 技能
     */
    public void setSkill3(int skill) {
        skill3 = skill;
    }

    /**
     * 获取名称
     * @return 返回值
     */
    public String getName() {
        return name;
    }

    /**
     * 获取Shout
     * @return 返回值
     */
    public int getShout() {
        return shout;
    }

    /**
     * 获取位置
     * @return 返回值
     */
    public int getPosition() {
        return position;
    }
}
