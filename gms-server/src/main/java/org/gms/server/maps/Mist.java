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
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.constants.skills.BlazeWizard;
import org.gms.constants.skills.Evan;
import org.gms.constants.skills.FPMage;
import org.gms.constants.skills.NightWalker;
import org.gms.constants.skills.Shadower;
import org.gms.net.packet.Packet;
import org.gms.server.StatEffect;
import org.gms.server.life.MobSkill;
import org.gms.server.life.Monster;
import org.gms.util.PacketCreator;

import java.awt.*;

/**
 * 技能迷雾/毒雾区域对象，对范围内单位施加持续效果。
 */
public class Mist extends AbstractMapObject {
    private final Rectangle mistPosition;
    private Character owner = null;
    private Monster mob = null;
    private StatEffect source;
    private MobSkill skill;
    private final boolean isMobMist;
    private boolean isPoisonMist;
    private boolean isRecoveryMist;
    private final int skillDelay;

    /**
     * 构造 Mist 实例。
     * @param mistPosition mistPosition
     * @param mob 怪物
     * @param skill skill
     */
    public Mist(Rectangle mistPosition, Monster mob, MobSkill skill) {
        this.mistPosition = mistPosition;
        this.mob = mob;
        this.skill = skill;
        isMobMist = true;
        isPoisonMist = true;
        isRecoveryMist = false;
        skillDelay = 0;
    }

    /**
     * 构造 Mist 实例。
     * @param mistPosition mistPosition
     * @param owner 归属角色
     * @param source 来源角色
     */
    public Mist(Rectangle mistPosition, Character owner, StatEffect source) {
        this.mistPosition = mistPosition;
        this.owner = owner;
        this.source = source;
        this.skillDelay = 8;
        this.isMobMist = false;
        this.isRecoveryMist = false;
        this.isPoisonMist = false;
        switch (source.getSourceId()) {
            case Evan.RECOVERY_AURA:
                isRecoveryMist = true;
                break;

            case Shadower.SMOKE_SCREEN: // Smoke Screen
                isPoisonMist = false;
                break;

            case FPMage.POISON_MIST: // FP mist
            case BlazeWizard.FLAME_GEAR: // Flame Gear
            case NightWalker.POISON_BOMB: // Poison Bomb
                isPoisonMist = true;
                break;
        }
    }

    /**
     * 获取类型。
     * @return MapObjectType 类型结果
     */
    @Override
    public MapObjectType getType() {
        return MapObjectType.MIST;
    }

    /**
     * 获取位置。
     * @return Point 类型结果
     */
    @Override
    public Point getPosition() {
        return mistPosition.getLocation();
    }

    /**
     * 获取Source、技能。
     * @return Skill 类型结果
     */
    public Skill getSourceSkill() {
        return SkillFactory.getSkill(source.getSourceId());
    }

    /**
     * 判断是否为怪物迷雾。
     * @return boolean 类型结果
     */
    public boolean isMobMist() {
        return isMobMist;
    }

    /**
     * 判断是否为Poison、迷雾。
     * @return boolean 类型结果
     */
    public boolean isPoisonMist() {
        return isPoisonMist;
    }

    /**
     * 判断是否为Recovery、迷雾。
     * @return boolean 类型结果
     */
    public boolean isRecoveryMist() {
        return isRecoveryMist;
    }

    /**
     * 获取技能延迟。
     * @return int 类型结果
     */
    public int getSkillDelay() {
        return skillDelay;
    }

    /**
     * 获取怪物归属者。
     * @return Monster 类型结果
     */
    public Monster getMobOwner() {
        return mob;
    }

    /**
     * 获取归属者。
     * @return Character 类型结果
     */
    public Character getOwner() {
        return owner;
    }

    /**
     * 获取区域。
     * @return Rectangle 类型结果
     */
    public Rectangle getBox() {
        return mistPosition;
    }

    /**
     * 设置位置。
     * @param position 坐标
     */
    @Override
    public void setPosition(Point position) {
        throw new UnsupportedOperationException();
    }

    /**
     * 执行 make、Destroy、数据 操作。
     * @return Packet 类型结果
     */
    public final Packet makeDestroyData() {
        return PacketCreator.removeMist(getObjectId());
    }

    /**
     * 执行 make、刷新、数据 操作。
     * @return Packet 类型结果
     */
    public final Packet makeSpawnData() {
        if (owner != null) {
            return PacketCreator.spawnMist(getObjectId(), owner.getId(), getSourceSkill().getId(), owner.getSkillLevel(SkillFactory.getSkill(source.getSourceId())), this);
        }
        return PacketCreator.spawnMobMist(getObjectId(), mob.getId(), skill.getId(), this);
    }

    /**
     * 执行 make、Fake、刷新、数据 操作。
     * @param level level
     * @return Packet 类型结果
     */
    public final Packet makeFakeSpawnData(int level) {
        if (owner != null) {
            return PacketCreator.spawnMist(getObjectId(), owner.getId(), getSourceSkill().getId(), level, this);
        }
        return PacketCreator.spawnMobMist(getObjectId(), mob.getId(), skill.getId(), this);
    }

    /**
     * 执行 send、刷新、数据 操作。
     * @param client client
     */
    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(makeSpawnData());
    }

    /**
     * 执行 send、Destroy、数据 操作。
     * @param client client
     */
    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(makeDestroyData());
    }

    /**
     * 执行 make、Chance、Result 操作。
     * @return boolean 类型结果
     */
    public boolean makeChanceResult() {
        return source.makeChanceResult();
    }
}
