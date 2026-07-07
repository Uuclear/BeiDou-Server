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

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.util.PacketCreator;


/**
 * 龙神职业召唤龙地图对象。
 */
public class Dragon extends AbstractAnimatedMapObject {
    private final Character owner;

    /**
     * 构造 Dragon 实例。
     * @param chr 角色
     */
    public Dragon(Character chr) {
        super();
        this.owner = chr;
        this.setPosition(chr.getPosition());
        this.setStance(chr.getStance());
        this.sendSpawnData(chr.getClient());
    }

    /**
     * 获取类型。
     * @return MapObjectType 类型结果
     */
    @Override
    public MapObjectType getType() {
        return MapObjectType.DRAGON;
    }

    /**
     * 执行 send、刷新、数据 操作。
     * @param client client
     */
    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(PacketCreator.spawnDragon(this));
    }

    /**
     * 获取对象ID。
     * @return int 类型结果
     */
    @Override
    public int getObjectId() {
        return owner.getId();
    }

    /**
     * 执行 send、Destroy、数据 操作。
     * @param c c
     */
    @Override
    public void sendDestroyData(Client c) {
        c.sendPacket(PacketCreator.removeDragon(owner.getId()));
    }

    /**
     * 获取归属者。
     * @return Character 类型结果
     */
    public Character getOwner() {
        return owner;
    }
}