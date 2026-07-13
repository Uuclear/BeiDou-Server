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
package org.gms.client;

/**
 * 疾病状态值持有者类
 * 用于存储疾病/异常状态的开始时间和持续时间
 *
 * @author OdinMS Team
 */
public class DiseaseValueHolder {
    /** 状态开始时间（毫秒时间戳） */
    public long startTime;
    /** 状态持续时长（毫秒） */
    public long length;

    /**
     * 构造函数
     * @param start 状态开始时间
     * @param length 状态持续时长
     */
    public DiseaseValueHolder(long start, long length) {
        this.startTime = start;
        this.length = length;
    }
}
