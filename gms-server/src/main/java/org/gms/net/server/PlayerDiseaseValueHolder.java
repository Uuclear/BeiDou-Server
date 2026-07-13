/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.net.server;

import org.gms.client.Disease;

/**
 * 玩家疾病（减益效果）值持有者类
 * 用于存储玩家的减益效果（疾病/Debuff）信息，包括疾病类型、开始时间和持续时长
 * 主要用于角色切换频道或重新登录时恢复减益状态
 *
 * @author Celino
 */
public class PlayerDiseaseValueHolder {
    /**
     * 减益效果开始时间（时间戳，毫秒）
     */
    public long startTime;
    
    /**
     * 减益效果持续时长（毫秒）
     */
    public long length;
    
    /**
     * 疾病（减益效果）枚举，标识具体的减益类型
     */
    public Disease disease;

    /**
     * 构造函数：创建玩家疾病（减益效果）值持有者
     *
     * @param disease 疾病（减益效果）类型
     * @param startTime 减益效果开始时间（毫秒时间戳）
     * @param length 减益效果持续时长（毫秒）
     */
    public PlayerDiseaseValueHolder(final Disease disease, final long startTime, final long length) {
        this.disease = disease;
        this.startTime = startTime;
        this.length = length;
    }
}