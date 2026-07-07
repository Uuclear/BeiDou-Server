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
package org.gms.server.maps;

import org.gms.client.Client;
import org.gms.net.packet.Packet;
import org.gms.util.PacketCreator;

/**
 * 地图视觉效果（如屏幕特效、BGM 变化）。
 */
public class MapEffect {
    private final String msg;
    private final int itemId;
    private final boolean active = true;

    /**
     * 构造 MapEffect 实例。
     * @param msg msg
     * @param itemId 物品 ID
     */
    public MapEffect(String msg, int itemId) {
        this.msg = msg;
        this.itemId = itemId;
    }

    /**
     * 执行 make、Destroy、数据 操作。
     * @return Packet 类型结果
     */
    public final Packet makeDestroyData() {
        return PacketCreator.removeMapEffect();
    }

    /**
     * 执行 make、Start、数据 操作。
     * @return Packet 类型结果
     */
    public final Packet makeStartData() {
        return PacketCreator.startMapEffect(msg, itemId, active);
    }

    /**
     * 执行 send、Start、数据 操作。
     * @param client client
     */
    public void sendStartData(Client client) {
        client.sendPacket(makeStartData());
    }
}
