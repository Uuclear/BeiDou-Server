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
package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.processor.action.MakerProcessor;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;

/**
 * 处理客户端 MAKER_SKILL（制作器技能） 封包。
 * <p>对应操作码：{@link org.gms.net.opcodes.RecvOpcode#MAKER_SKILL}</p>
 */
public final class MakerSkillHandler extends AbstractPacketHandler {

    /** 处理 制作器技能 封包的业务逻辑。 */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        MakerProcessor.makerAction(p, c);
    }
}
