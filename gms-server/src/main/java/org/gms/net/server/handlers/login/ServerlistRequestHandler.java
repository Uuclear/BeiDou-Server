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
package org.gms.net.server.handlers.login;

import org.gms.client.Client;
import org.gms.constants.game.GameConstants;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.util.PacketCreator;

import java.util.List;

/**
 * 处理请求或重新请求服务器列表（SERVERLIST_REQUEST / SERVERLIST_REREQUEST）。
 * <p>对应操作码：{@link org.gms.net.opcodes.RecvOpcode#SERVERLIST_REREQUEST}, {@link org.gms.net.opcodes.RecvOpcode#SERVERLIST_REQUEST}</p>
 */
public final class ServerlistRequestHandler extends AbstractPacketHandler {

    /** 处理 重新请求服务器列表 封包的业务逻辑。 */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        Server server = Server.getInstance();
        List<World> worlds = server.getWorlds();
        c.requestedServerlist(worlds.size());

        for (World world : worlds) {
            c.sendPacket(PacketCreator.getServerList(world.getId(), GameConstants.WORLD_NAMES[world.getId()], world.getFlag(), world.getEventMessage(), world.getChannels()));
        }
        c.sendPacket(PacketCreator.getEndOfServerList());
        c.sendPacket(PacketCreator.selectWorld(0));//too lazy to make a check lol
        c.sendPacket(PacketCreator.sendRecommended(server.worldRecommendedList()));
    }
}