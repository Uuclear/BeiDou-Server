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

package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

/**
 * 处理角色更名流程中的名称可用性检查结果（频道服 CHECK_CHAR_NAME）。
 * <p>对应操作码：{@link org.gms.net.opcodes.RecvOpcode#CHECK_CHAR_NAME}</p>
 */
public final class TransferNameResultHandler extends AbstractPacketHandler {

    /** 处理 检查角色名称 封包的业务逻辑。 */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        String name = p.readString();
        c.sendPacket(PacketCreator.sendNameTransferCheck(name, Character.canCreateChar(name)));
    }
}