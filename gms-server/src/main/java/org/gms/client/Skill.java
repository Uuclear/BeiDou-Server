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

import org.gms.server.StatEffect;
import org.gms.server.life.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 技能数据模型，封装技能 ID、等级、主被动类型、消耗、冷却及效果等属性。
 */
public class Skill {
    private final int id;
    private final List<StatEffect> effects = new ArrayList<>();
    private Element element;
    private int animationTime;
    private final int job;
    private boolean action;

    /**
     * 技能
     * @param id ID
     */
    public Skill(int id) {
        this.id = id;
        this.job = id / 10000;
    }

    /**
     * 获取ID
     * @return 返回值
     */
    public int getId() {
        return id;
    }

    /**
     * 获取效果
     * @param level 等级
     * @return 返回值
     */
    public StatEffect getEffect(int level) {
        return effects.get(level - 1);
    }

    /**
     * 获取最大等级
     * @return 返回值
     */
    public int getMaxLevel() {
        return effects.size();
    }

    /**
     * 判断是否为Fourth职业
     * @return 返回值
     */
    public boolean isFourthJob() {
        if (job == 2212) {
            return false;
        }
        if (id == 22170001 || id == 22171003 || id == 22171004 || id == 22181002 || id == 22181003) {
            return true;
        }
        return job % 10 == 2;
    }

    /**
     * 设置Element
     * @param elem elem
     */
    public void setElement(Element elem) {
        element = elem;
    }

    /**
     * 获取Element
     * @return 返回值
     */
    public Element getElement() {
        return element;
    }

    /**
     * 获取AnimationTime
     * @return 返回值
     */
    public int getAnimationTime() {
        return animationTime;
    }

    /**
     * 设置AnimationTime
     * @param time time
     */
    public void setAnimationTime(int time) {
        animationTime = time;
    }

    /**
     * incAnimationTime
     * @param time time
     */
    /**
     * incAnimationTime
     * @param time time
     */
    /**
     * inc动画时间
     * @param time time
     */
    public void incAnimationTime(int time) {
        animationTime += time;
    }

    /**
     * 判断是否为Beginner技能
     * @return 返回值
     */
    public boolean isBeginnerSkill() {
        return id % 10000000 < 10000;
    }

    /**
     * 设置Action
     * @param act act
     */
    public void setAction(boolean act) {
        action = act;
    }

    /**
     * 获取Action
     * @return 返回值
     */
    public boolean getAction() {
        return action;
    }

    /**
     * 添加等级效果
     * @param effect effect
     */
    public void addLevelEffect(StatEffect effect) {
        effects.add(effect);
    }
}