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
package org.gms.server.maps;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.SkillFactory;
import org.gms.util.PacketCreator;

import java.awt.*;

/**
 * 召唤兽地图对象，跟随主人并参与战斗。
 */
public class Summon extends AbstractAnimatedMapObject {
    private final Character owner;
    private final byte skillLevel;
    private final int skill;
    private int hp;
    private final SummonMovementType movementType;

    /**
     * 构造 Summon 实例。
     * @param owner 归属角色
     * @param skill skill
     * @param pos 坐标
     * @param movementType movementType
     */
    public Summon(Character owner, int skill, Point pos, SummonMovementType movementType) {
        this.owner = owner;
        this.skill = skill;
        this.skillLevel = owner.getSkillLevel(SkillFactory.getSkill(skill));
        if (skillLevel == 0) {
            throw new RuntimeException();
        }

        this.movementType = movementType;
        setPosition(pos);
    }

    /**
     * 执行 send、刷新、数据 操作。
     * @param client client
     */
    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(PacketCreator.spawnSummon(this, false));
    }

    /**
     * 执行 send、Destroy、数据 操作。
     * @param client client
     */
    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(PacketCreator.removeSummon(this, true));
    }

    /**
     * 获取归属者。
     * @return Character 类型结果
     */
    public Character getOwner() {
        return owner;
    }

    /**
     * 获取技能。
     * @return int 类型结果
     */
    public int getSkill() {
        return skill;
    }

    /**
     * 获取HP。
     * @return int 类型结果
     */
    public int getHP() {
        return hp;
    }

    /**
     * 添加HP。
     * @param delta delta
     */
    public void addHP(int delta) {
        this.hp += delta;
    }

    /**
     * 获取移动类型。
     * @return SummonMovementType 类型结果
     */
    public SummonMovementType getMovementType() {
        return movementType;
    }

    /**
     * 判断是否为Stationary。
     * @return boolean 类型结果
     */
    public boolean isStationary() {
        return (skill == 3111002 || skill == 3211002 || skill == 5211001 || skill == 13111004);
    }

    /**
     * 获取技能等级。
     * @return byte 类型结果
     */
    public byte getSkillLevel() {
        return skillLevel;
    }

    /**
     * 获取类型。
     * @return MapObjectType 类型结果
     */
    @Override
    public MapObjectType getType() {
        return MapObjectType.SUMMON;
    }

    /**
     * 判断是否为Puppet。
     * @return boolean 类型结果
     */
    public final boolean isPuppet() {
        switch (skill) {
            case 3111002:
            case 3211002:
            case 13111004:
                return true;
        }
        return false;
    }
}
