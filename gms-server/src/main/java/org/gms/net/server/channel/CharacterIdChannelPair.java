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
package org.gms.net.server.channel;

/**
 * 角色ID与频道ID配对类
 * 用于存储角色ID与其所在频道ID的映射关系
 * 主要用于跨频道通信时定位角色所在的频道
 *
 * @author Frz
 */
public class CharacterIdChannelPair {
    /**
     * 角色ID
     */
    private int charid;
    
    /**
     * 频道ID
     */
    private int channel;

    /**
     * 默认构造函数
     */
    public CharacterIdChannelPair() {
    }

    /**
     * 构造函数：创建角色ID与频道ID配对
     *
     * @param charid 角色ID
     * @param channel 频道ID
     */
    public CharacterIdChannelPair(int charid, int channel) {
        this.charid = charid;
        this.channel = channel;
    }

    /**
     * 获取角色ID
     *
     * @return 角色ID
     */
    public int getCharacterId() {
        return charid;
    }

    /**
     * 获取频道ID
     *
     * @return 频道ID
     */
    public int getChannel() {
        return channel;
    }
}
