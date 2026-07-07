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
package org.gms.net.server.handlers;

import org.gms.client.Client;
import org.gms.net.PacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

/**
 * 处理自定义扩展封包（CUSTOM_PACKET），向 GM 回显原始载荷。
 * <p>对应操作码：{@link org.gms.net.opcodes.RecvOpcode#CUSTOM_PACKET}</p>
 */
public class CustomPacketHandler implements PacketHandler {
    /** 读取自定义载荷并向高权限 GM 回显。 */
    @Override
    public void handlePacket(InPacket p, Client c) {
        if (p.available() > 0 && c.getGMLevel() >= 4) {//w/e
            c.sendPacket(PacketCreator.customPacket(p.readBytes(p.available())));
        }
    }

    /** 任意连接状态下均可处理该封包。 */
    @Override
    public boolean validateState(Client c) {
        return true;
    }
}
