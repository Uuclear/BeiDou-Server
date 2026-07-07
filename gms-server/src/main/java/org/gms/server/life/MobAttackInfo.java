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

/**
 * 怪物攻击信息（攻击位置、MP 消耗、冷却、动画时长）。
 */
public class MobAttackInfo {
    private boolean isDeadlyAttack;
    private int mpBurn;
    private int diseaseSkill;
    private int diseaseLevel;
    private int mpCon;

    /**
     * 构造 MobAttackInfo 实例。
     * @param mobId mobId
     * @param attackId attackId
     */
    public MobAttackInfo(int mobId, int attackId) {
    }

    /**
     * 设置Deadly、攻击。
     * @param isDeadlyAttack isDeadlyAttack
     */
    public void setDeadlyAttack(boolean isDeadlyAttack) {
        this.isDeadlyAttack = isDeadlyAttack;
    }

    /**
     * 判断是否为Deadly、攻击。
     * @return boolean 类型结果
     */
    public boolean isDeadlyAttack() {
        return isDeadlyAttack;
    }

    /**
     * 设置MP、Burn。
     * @param mpBurn mpBurn
     */
    public void setMpBurn(int mpBurn) {
        this.mpBurn = mpBurn;
    }

    /**
     * 获取MP、Burn。
     * @return int 类型结果
     */
    public int getMpBurn() {
        return mpBurn;
    }

    /**
     * 设置Disease、技能。
     * @param diseaseSkill diseaseSkill
     */
    public void setDiseaseSkill(int diseaseSkill) {
        this.diseaseSkill = diseaseSkill;
    }

    /**
     * 获取Disease、技能。
     * @return int 类型结果
     */
    public int getDiseaseSkill() {
        return diseaseSkill;
    }

    /**
     * 设置Disease、等级。
     * @param diseaseLevel diseaseLevel
     */
    public void setDiseaseLevel(int diseaseLevel) {
        this.diseaseLevel = diseaseLevel;
    }

    /**
     * 获取Disease、等级。
     * @return int 类型结果
     */
    public int getDiseaseLevel() {
        return diseaseLevel;
    }

    /**
     * 设置MPCon。
     * @param mpCon mpCon
     */
    public void setMpCon(int mpCon) {
        this.mpCon = mpCon;
    }

    /**
     * 获取MPCon。
     * @return int 类型结果
     */
    public int getMpCon() {
        return mpCon;
    }
}
