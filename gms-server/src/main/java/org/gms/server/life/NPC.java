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
package org.gms.server.life;

import org.gms.client.Client;
import org.gms.server.ShopFactory;
import org.gms.server.maps.MapObjectType;
import org.gms.util.PacketCreator;

/**
 * NPC 地图对象，提供对话、商店、任务等服务。
 */
public class NPC extends AbstractLoadedLife {
    private final NPCStats stats;

    /**
     * 构造 NPC 实例。
     * @param id ID
     * @param stats stats
     */
    public NPC(int id, NPCStats stats) {
        super(id);
        this.stats = stats;
    }

    /**
     * 判断是否拥有商店。
     * @return boolean 类型结果
     */
    public boolean hasShop() {
        return ShopFactory.getInstance().getShopForNPC(getId()) != null;
    }

    /**
     * 执行 send、商店 操作。
     * @param c c
     */
    public void sendShop(Client c) {
        ShopFactory.getInstance().getShopForNPC(getId()).sendShop(c);
    }

    /**
     * 执行 send、刷新、数据 操作。
     * @param client client
     */
    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(PacketCreator.spawnNPC(this));
        client.sendPacket(PacketCreator.spawnNPCRequestController(this, true));
    }

    /**
     * 执行 send、Destroy、数据 操作。
     * @param client client
     */
    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(PacketCreator.removeNPCController(getObjectId()));
        client.sendPacket(PacketCreator.removeNPC(getObjectId()));
    }

    /**
     * 获取类型。
     * @return MapObjectType 类型结果
     */
    @Override
    public MapObjectType getType() {
        return MapObjectType.NPC;
    }

    /**
     * 获取名称。
     * @return String 类型结果
     */
    public String getName() {
        return stats.getName();
    }
}
