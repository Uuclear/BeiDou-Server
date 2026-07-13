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

import org.gms.server.StatEffect;

/**
 * 玩家增益效果值持有者类
 * 用于存储玩家的增益效果（Buff）信息，包括使用时间和效果详情
 * 主要用于角色切换频道或重新登录时恢复增益状态
 *
 * @author Danny
 */
public class PlayerBuffValueHolder {
    /**
     * 增益效果使用时间（时间戳）
     */
    public int usedTime;
    
    /**
     * 增益效果对象，包含效果的具体属性（如属性加成、持续时间等）
     */
    public StatEffect effect;

    /**
     * 构造函数：创建玩家增益效果值持有者
     *
     * @param usedTime 增益效果使用的时间戳
     * @param effect 增益效果对象
     */
    public PlayerBuffValueHolder(int usedTime, StatEffect effect) {
        this.usedTime = usedTime;
        this.effect = effect;
    }
}
