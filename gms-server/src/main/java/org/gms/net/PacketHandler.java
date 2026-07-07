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
package org.gms.net;

import org.gms.client.Client;
import org.gms.net.packet.InPacket;

/**
 * 入站封包处理器接口，定义封包处理逻辑与会话状态校验。
 */
public interface PacketHandler {
    /**
     * 处理客户端发来的入站封包。
     *
     * @param p 入站封包
     * @param c 客户端会话
     */
    void handlePacket(InPacket p, Client c);

    /**
     * 校验当前客户端状态是否允许处理该封包。
     *
     * @param c 客户端会话
     * @return 状态合法返回 true
     */
    boolean validateState(Client c);
}
