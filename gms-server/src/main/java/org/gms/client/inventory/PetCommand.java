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

/*
 * @author Leifde
 */
/**
 * 宠物指令枚举，定义玩家可对宠物发出的各类指令。
 */
public class PetCommand {
    private final int petId;
    private final int skillId;
    private final int prob;
    private final int inc;

    /**
     * 宠物Command
     * @param petId petId
     * @param skillId 技能ID
     * @param prob prob
     * @param inc inc
     */
    public PetCommand(int petId, int skillId, int prob, int inc) {
        this.petId = petId;
        this.skillId = skillId;
        this.prob = prob;
        this.inc = inc;
    }

    /**
     * 获取宠物ID
     * @return 返回值
     */
    public int getPetId() {
        return petId;
    }

    /**
     * 获取技能ID
     * @return 返回值
     */
    public int getSkillId() {
        return skillId;
    }

    /**
     * 获取Probability
     * @return 返回值
     */
    public int getProbability() {
        return prob;
    }

    /**
     * 获取Increase
     * @return 返回值
     */
    public int getIncrease() {
        return inc;
    }
}
