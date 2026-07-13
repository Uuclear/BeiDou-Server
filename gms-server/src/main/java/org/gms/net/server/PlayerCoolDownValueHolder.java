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
package org.gms.net.server;

/**
 * 玩家技能冷却值持有者类
 * 用于存储玩家技能的冷却信息，包括技能ID、开始时间和冷却时长
 * 主要用于角色切换频道或重新登录时恢复技能冷却状态
 *
 * @author Danny
 */
public class PlayerCoolDownValueHolder {
    /**
     * 技能ID，标识具体的技能
     */
    public int skillId;
    
    /**
     * 冷却开始时间（时间戳，毫秒）
     */
    public long startTime;
    
    /**
     * 冷却持续时长（毫秒）
     */
    public long length;

    /**
     * 构造函数：创建玩家技能冷却值持有者
     *
     * @param skillId 技能ID
     * @param startTime 冷却开始时间（毫秒时间戳）
     * @param length 冷却持续时长（毫秒）
     */
    public PlayerCoolDownValueHolder(int skillId, long startTime, long length) {
        this.skillId = skillId;
        this.startTime = startTime;
        this.length = length;
    }
}
