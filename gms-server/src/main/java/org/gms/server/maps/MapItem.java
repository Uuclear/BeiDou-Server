/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

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
import org.gms.client.inventory.Item;
import org.gms.util.PacketCreator;

import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 地图掉落物实例（装备、消耗品、金币等）。
 */
public class MapItem extends AbstractMapObject {
    protected Client ownerClient;
    protected Item item;
    protected MapObject dropper;
    protected int character_ownerid, party_ownerid, meso, questid = -1;
    protected byte type;
    protected boolean pickedUp = false, playerDrop, partyDrop;
    protected long dropTime;
    private final Lock itemLock = new ReentrantLock();

    /**
     * 构造 MapItem 实例。
     * @param item item
     * @param position 坐标
     * @param dropper 掉落来源
     * @param owner 归属角色
     * @param ownerClient ownerClient
     * @param type 类型
     * @param playerDrop 是否玩家丢弃
     */
    public MapItem(Item item, Point position, MapObject dropper, Character owner, Client ownerClient, byte type, boolean playerDrop) {
        setPosition(position);
        this.item = item;
        this.dropper = dropper;
        this.character_ownerid = owner.getId();
        this.party_ownerid = owner.getPartyId();
        this.partyDrop = this.party_ownerid != -1;
        this.ownerClient = owner.getClient();
        this.meso = 0;
        this.type = type;
        this.playerDrop = playerDrop;
    }

    /**
     * 构造 MapItem 实例。
     * @param item item
     * @param position 坐标
     * @param dropper 掉落来源
     * @param owner 归属角色
     * @param ownerClient ownerClient
     * @param type 类型
     * @param playerDrop 是否玩家丢弃
     * @param questid 任务 ID
     */
    public MapItem(Item item, Point position, MapObject dropper, Character owner, Client ownerClient, byte type, boolean playerDrop, int questid) {
        setPosition(position);
        this.item = item;
        this.dropper = dropper;
        this.character_ownerid = owner.getId();
        this.party_ownerid = owner.getPartyId();
        this.partyDrop = this.party_ownerid != -1;
        this.ownerClient = owner.getClient();
        this.meso = 0;
        this.type = type;
        this.playerDrop = playerDrop;
        this.questid = questid;
    }

    /**
     * 构造 MapItem 实例。
     * @param meso 金币数量
     * @param position 坐标
     * @param dropper 掉落来源
     * @param owner 归属角色
     * @param ownerClient ownerClient
     * @param type 类型
     * @param playerDrop 是否玩家丢弃
     */
    public MapItem(int meso, Point position, MapObject dropper, Character owner, Client ownerClient, byte type, boolean playerDrop) {
        setPosition(position);
        this.item = null;
        this.dropper = dropper;
        this.character_ownerid = owner.getId();
        this.party_ownerid = owner.getPartyId();
        this.partyDrop = this.party_ownerid != -1;
        this.ownerClient = owner.getClient();
        this.meso = meso;
        this.type = type;
        this.playerDrop = playerDrop;
    }

    /**
     * 获取物品。
     * @return Item 类型结果
     */
    public final Item getItem() {
        return item;
    }

    /**
     * 获取任务。
     * @return int 类型结果
     */
    public final int getQuest() {
        return questid;
    }

    /**
     * 获取物品ID。
     * @return int 类型结果
     */
    public final int getItemId() {
        if (meso > 0) {
            return meso;
        }
        return item.getItemId();
    }

    /**
     * 获取掉落源。
     * @return MapObject 类型结果
     */
    public final MapObject getDropper() {
        return dropper;
    }

    /**
     * 获取归属者ID。
     * @return int 类型结果
     */
    public final int getOwnerId() {
        return character_ownerid;
    }

    /**
     * 获取队伍归属者ID。
     * @return int 类型结果
     */
    public final int getPartyOwnerId() {
        return party_ownerid;
    }

    /**
     * 设置队伍归属者ID。
     * @param partyid 队伍 ID
     */
    public final void setPartyOwnerId(int partyid) {
        party_ownerid = partyid;
    }

    /**
     * 获取Clientside、归属者、ID。
     * @return int 类型结果
     */
    public final int getClientsideOwnerId() {   // thanks nozphex (RedHat) for noting an issue with collecting party items
        if (this.party_ownerid == -1) {
            return this.character_ownerid;
        } else {
            return this.party_ownerid;
        }
    }

    /**
     * 判断是否拥有Clientside、Ownership。
     * @param player 玩家
     * @return boolean 类型结果
     */
    public final boolean hasClientsideOwnership(Character player) {
        return this.character_ownerid == player.getId() || this.party_ownerid == player.getPartyId() || hasExpiredOwnershipTime();
    }

    /**
     * 判断是否为FFA掉落。
     * @return boolean 类型结果
     */
    public final boolean isFFADrop() {
        return type == 2 || type == 3 || hasExpiredOwnershipTime();
    }

    /**
     * 判断是否拥有Expired、Ownership、时间。
     * @return boolean 类型结果
     */
    public final boolean hasExpiredOwnershipTime() {
        return System.currentTimeMillis() - dropTime >= SECONDS.toMillis(15);
    }

    /**
     * 判断是否可以Be、Picked、按。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public final boolean canBePickedBy(Character chr) {
        if (character_ownerid <= 0 || isFFADrop()) {
            return true;
        }

        if (party_ownerid == -1) {
            if (chr.getId() == character_ownerid) {
                return true;
            } else if (chr.isPartyMember(character_ownerid)) {
                party_ownerid = chr.getPartyId();
                return true;
            }
        } else {
            if (chr.getPartyId() == party_ownerid) {
                return true;
            } else if (chr.getId() == character_ownerid) {
                party_ownerid = chr.getPartyId();
                return true;
            }
        }

        return hasExpiredOwnershipTime();
    }

    /**
     * 获取归属者、Client。
     * @return Client 类型结果
     */
    public final Client getOwnerClient() {
        return (ownerClient.isLoggedIn() && !ownerClient.getPlayer().isAwayFromWorld()) ? ownerClient : null;
    }

    /**
     * 获取金币。
     * @return int 类型结果
     */
    public final int getMeso() {
        return meso;
    }

    /**
     * 判断是否为玩家掉落。
     * @return boolean 类型结果
     */
    public final boolean isPlayerDrop() {
        return playerDrop;
    }

    /**
     * 判断是否为Picked、Up。
     * @return boolean 类型结果
     */
    public final boolean isPickedUp() {
        return pickedUp;
    }

    /**
     * 设置Picked、Up。
     * @param pickedUp pickedUp
     */
    public void setPickedUp(final boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    /**
     * 获取掉落时间。
     * @return long 类型结果
     */
    public long getDropTime() {
        return dropTime;
    }

    /**
     * 设置掉落时间。
     * @param time time
     */
    public void setDropTime(long time) {
        this.dropTime = time;
    }

    /**
     * 获取掉落类型。
     * @return byte 类型结果
     */
    public byte getDropType() {
        return type;
    }

    /**
     * 执行 lock、物品 操作。
     */
    public void lockItem() {
        itemLock.lock();
    }

    /**
     * 执行 unlock、物品 操作。
     */
    public void unlockItem() {
        itemLock.unlock();
    }

    /**
     * 获取类型。
     * @return MapObjectType 类型结果
     */
    @Override
    public final MapObjectType getType() {
        return MapObjectType.ITEM;
    }

    /**
     * 执行 send、刷新、数据 操作。
     * @param client client
     */
    @Override
    public void sendSpawnData(final Client client) {
        Character chr = client.getPlayer();

        if (chr.needQuestItem(questid, getItemId())) {
            this.lockItem();
            try {
                client.sendPacket(PacketCreator.dropItemFromMapObject(chr, this, null, getPosition(), (byte) 2));
            } finally {
                this.unlockItem();
            }
        }
    }

    /**
     * 执行 send、Destroy、数据 操作。
     * @param client client
     */
    @Override
    public void sendDestroyData(final Client client) {
        client.sendPacket(PacketCreator.removeItemFromMap(getObjectId(), 1, 0));
    }
}