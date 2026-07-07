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
package org.gms.server.maps;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.constants.id.MapId;
import org.gms.net.server.world.Party;
import org.gms.util.PacketCreator;

import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 传送门对象的数据封装。
 */
public class DoorObject extends AbstractMapObject {
    private final int ownerId;
    private int pairOid;

    private final MapleMap from;
    private final MapleMap to;
    private int linkedPortalId;
    private Point linkedPos;

    private final Lock rlock;
    private final Lock wlock;

    /**
     * 构造 DoorObject 实例。
     * @param owner 归属角色
     * @param destination destination
     * @param origin origin
     * @param townPortalId townPortalId
     * @param targetPosition targetPosition
     * @param toPosition toPosition
     */
    public DoorObject(int owner, MapleMap destination, MapleMap origin, int townPortalId, Point targetPosition, Point toPosition) {
        super();
        setPosition(targetPosition);

        ownerId = owner;
        linkedPortalId = townPortalId;
        from = origin;
        to = destination;
        linkedPos = toPosition;

        ReadWriteLock lock = new ReentrantReadWriteLock(true);
        this.rlock = lock.readLock();
        this.wlock = lock.writeLock();
    }

    /**
     * 执行 update 操作。
     * @param townPortalId townPortalId
     * @param toPosition toPosition
     */
    public void update(int townPortalId, Point toPosition) {
        wlock.lock();
        try {
            linkedPortalId = townPortalId;
            linkedPos = toPosition;
        } finally {
            wlock.unlock();
        }
    }

    private int getLinkedPortalId() {
        rlock.lock();
        try {
            return linkedPortalId;
        } finally {
            rlock.unlock();
        }
    }

    private Point getLinkedPortalPosition() {
        rlock.lock();
        try {
            return linkedPos;
        } finally {
            rlock.unlock();
        }
    }

    /**
     * 执行 warp 操作。
     * @param chr 角色
     */
    public void warp(final Character chr) {
        Party party = chr.getParty();
        if (chr.getId() == ownerId || (party != null && party.getMemberById(ownerId) != null)) {
            chr.sendPacket(PacketCreator.playPortalSound());

            if (!inTown() && party == null) {
                chr.changeMap(to, getLinkedPortalId());
            } else {
                chr.changeMap(to, getLinkedPortalPosition());
            }
        } else {
            chr.sendPacket(PacketCreator.blockedMessage(6));
            chr.sendPacket(PacketCreator.enableActions());
        }
    }

    /**
     * 执行 send、刷新、数据 操作。
     * @param client client
     */
    @Override
    public void sendSpawnData(Client client) {
        sendSpawnData(client, true);
    }

    /**
     * 执行 send、刷新、数据 操作。
     * @param client client
     * @param launched launched
     */
    public void sendSpawnData(Client client, boolean launched) {
        Character chr = client.getPlayer();
        if (this.getFrom().getId() == chr.getMapId()) {
            if (chr.getParty() != null && (this.getOwnerId() == chr.getId() || chr.getParty().getMemberById(this.getOwnerId()) != null)) {
                chr.sendPacket(PacketCreator.partyPortal(this.getFrom().getId(), this.getTo().getId(), this.toPosition()));
            }

            chr.sendPacket(PacketCreator.spawnPortal(this.getFrom().getId(), this.getTo().getId(), this.toPosition()));
            if (!this.inTown()) {
                chr.sendPacket(PacketCreator.spawnDoor(this.getOwnerId(), this.getPosition(), launched));
            }
        }
    }

    /**
     * 执行 send、Destroy、数据 操作。
     * @param client client
     */
    @Override
    public void sendDestroyData(Client client) {
        Character chr = client.getPlayer();
        if (from.getId() == chr.getMapId()) {
            Party party = chr.getParty();
            if (party != null && (ownerId == chr.getId() || party.getMemberById(ownerId) != null)) {
                client.sendPacket(PacketCreator.partyPortal(MapId.NONE, MapId.NONE, new Point(-1, -1)));
            }
            client.sendPacket(PacketCreator.removeDoor(ownerId, inTown()));
        }
    }

    /**
     * 执行 send、Destroy、数据 操作。
     * @param client client
     * @param partyUpdate partyUpdate
     */
    public void sendDestroyData(Client client, boolean partyUpdate) {
        if (client != null && from.getId() == client.getPlayer().getMapId()) {
            client.sendPacket(PacketCreator.partyPortal(MapId.NONE, MapId.NONE, new Point(-1, -1)));
            client.sendPacket(PacketCreator.removeDoor(ownerId, inTown()));
        }
    }

    /**
     * 获取归属者ID。
     * @return int 类型结果
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * 设置Pair、对象 ID。
     * @param oid 对象 ID
     */
    public void setPairOid(int oid) {
        this.pairOid = oid;
    }

    /**
     * 获取Pair、对象 ID。
     * @return int 类型结果
     */
    public int getPairOid() {
        return pairOid;
    }

    /**
     * 执行 in、Town 操作。
     * @return boolean 类型结果
     */
    public boolean inTown() {
        return getLinkedPortalId() == -1;
    }

    /**
     * 获取来自。
     * @return MapleMap 类型结果
     */
    public MapleMap getFrom() {
        return from;
    }

    /**
     * 获取到。
     * @return MapleMap 类型结果
     */
    public MapleMap getTo() {
        return to;
    }

    /**
     * 获取Town。
     * @return MapleMap 类型结果
     */
    public MapleMap getTown() {
        return inTown() ? from : to;
    }

    /**
     * 获取区域。
     * @return MapleMap 类型结果
     */
    public MapleMap getArea() {
        return !inTown() ? from : to;
    }

    /**
     * 获取区域位置。
     * @return Point 类型结果
     */
    public Point getAreaPosition() {
        return !inTown() ? getPosition() : getLinkedPortalPosition();
    }

    /**
     * 执行 to位置 操作。
     * @return Point 类型结果
     */
    public Point toPosition() {
        return getLinkedPortalPosition();
    }

    /**
     * 获取类型。
     * @return MapObjectType 类型结果
     */
    @Override
    public MapObjectType getType() {
        return MapObjectType.DOOR;
    }
}
