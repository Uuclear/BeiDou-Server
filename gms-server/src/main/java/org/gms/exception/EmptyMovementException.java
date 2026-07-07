/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
package org.gms.exception;

import org.gms.net.packet.InPacket;


/**
 * 空移动包异常，当解析玩家移动数据包内容为空时抛出。
 * 用于网络层移动校验，携带原始 InPacket 便于排查。
 */
public class EmptyMovementException extends Exception {

    /**
     * 构造 EmptyMovementException。
     *
     * @param inPacket 原始网络数据包
     */
    public EmptyMovementException(InPacket inPacket) {
        super("Empty movement: " + inPacket);
    }

}
