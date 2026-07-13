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
import org.gms.constants.id.NpcId;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;

/**
 * 使用生命之水Handler - 处理使用生命之水复活宠物
 * 处理客户端发送的相关数据包
 *
 * @author OdinMS开发团队
 */
public final class UseWaterOfLifeHandler extends AbstractPacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {
        c.getAbstractPlayerInteraction().openNpc(NpcId.MAR_THE_FAIRY, "waterOfLife");
    }
}