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
package org.gms.client;

import org.gms.net.packet.Packet;
import org.gms.net.server.PlayerStorage;
import org.gms.constants.game.GameConstants;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 好友列表管理类，维护角色的好友关系及分组。
 */
public class BuddyList {
    /**
     * BuddyOperation枚举，定义相关常量值
     */
    public enum BuddyOperation {
        ADDED, DELETED
    }

    /**
     * BuddyAddResult枚举，定义相关常量值
     */
    public enum BuddyAddResult {
        BUDDYLIST_FULL, ALREADY_ON_LIST, OK
    }

    private final Map<Integer, BuddylistEntry> buddies = new LinkedHashMap<>();
    private int capacity;
    private final Deque<CharacterNameAndId> pendingRequests = new LinkedList<>();

    /**
     * 好友列表
     * @param capacity capacity
     */
    public BuddyList(int capacity) {
        this.capacity = capacity;
    }

    /**
     * contains
     * @param characterId characterId
     * @return 返回值
     */
    public boolean contains(int characterId) {
        synchronized (buddies) {
            return buddies.containsKey(characterId);
        }
    }

    /**
     * contains可见
     * @param characterId characterId
     * @return 返回值
     */
    public boolean containsVisible(int characterId) {
        BuddylistEntry ble;
        synchronized (buddies) {
            ble = buddies.get(characterId);
        }

        if (ble == null) {
            return false;
        }
        return ble.isVisible();

    }

    /**
     * 获取Capacity
     * @return 返回值
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * 设置Capacity
     * @param capacity capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 获取
     * @param characterId characterId
     * @return 返回值
     */
    public BuddylistEntry get(int characterId) {
        synchronized (buddies) {
            return buddies.get(characterId);
        }
    }

    /**
     * 获取
     * @param characterName characterName
     * @return 返回值
     */
    public BuddylistEntry get(String characterName) {
        String lowerCaseName = characterName.toLowerCase();
        for (BuddylistEntry ble : getBuddies()) {
            if (ble.getName().toLowerCase().equals(lowerCaseName)) {
                return ble;
            }
        }

        return null;
    }

    /**
     * put
     * @param entry entry
     */
    /**
     * put
     * @param entry entry
     */
    /**
     * 添加好友条目
     * @param entry 好友条目
     */
    public void put(BuddylistEntry entry) {
        synchronized (buddies) {
            buddies.put(entry.getCharacterId(), entry);
        }
    }

    /**
     * 移除
     * @param characterId characterId
     */
    public void remove(int characterId) {
        synchronized (buddies) {
            buddies.remove(characterId);
        }
    }

    /**
     * 获取Buddies
     * @return 返回值
     */
    public Collection<BuddylistEntry> getBuddies() {
        synchronized (buddies) {
            return Collections.unmodifiableCollection(buddies.values());
        }
    }

    /**
     * 判断是否为Full
     * @return 返回值
     */
    public boolean isFull() {
        synchronized (buddies) {
            return buddies.size() >= capacity;
        }
    }

    /**
     * 获取好友Ids
     * @return 返回值
     */
    public int[] getBuddyIds() {
        synchronized (buddies) {
            int[] buddyIds = new int[buddies.size()];
            int i = 0;
            for (BuddylistEntry ble : buddies.values()) {
                buddyIds[i++] = ble.getCharacterId();
            }
            return buddyIds;
        }
    }

    /**
     * 广播
     * @param packet 封包
     * @param pstorage pstorage
     */
    public void broadcast(Packet packet, PlayerStorage pstorage) {
        for (int bid : getBuddyIds()) {
            Character chr = pstorage.getCharacterById(bid);

            if (chr != null && chr.isLoggedInWorld()) {
                chr.sendPacket(packet);
            }
        }
    }

    /**
     * 加载从Db
     * @param characterId characterId
     */
    public void loadFromDb(int characterId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT b.buddyid, b.pending, b.group, c.name as buddyname FROM buddies as b, characters as c WHERE c.id = b.buddyid AND b.characterid = ?")) {
                ps.setInt(1, characterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (rs.getInt("pending") == 1) {
                            pendingRequests.push(new CharacterNameAndId(rs.getInt("buddyid"), rs.getString("buddyname")));
                        } else {
                            put(new BuddylistEntry(rs.getString("buddyname"), rs.getString("group"), rs.getInt("buddyid"), (byte) -1, true));
                        }
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM buddies WHERE pending = 1 AND characterid = ?")) {
                ps.setInt(1, characterId);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * pollPending请求
     * @return 返回值
     */
    public CharacterNameAndId pollPendingRequest() {
        return pendingRequests.pollLast();
    }

    /**
     * 添加好友请求
     * @param c 客户端会话
     * @param cidFrom cidFrom
     * @param nameFrom nameFrom
     * @param channelFrom channelFrom
     */
    public void addBuddyRequest(Client c, int cidFrom, String nameFrom, int channelFrom) {
        // 落库：给被加方写一条 pending=1，使请求不因下线/重启丢失（与离线加好友分支对齐）。
        // buddies 表无 (characterid,buddyid) 唯一约束，用 DELETE+INSERT 保证幂等。
        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM buddies WHERE characterid = ? AND buddyid = ?")) {
                ps.setInt(1, c.getPlayer().getId()); // 被加方
                ps.setInt(2, cidFrom);                // 发起方
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO buddies (characterid, `buddyid`, `pending`, `group`) VALUES (?, ?, 1, ?)")) {
                ps.setInt(1, c.getPlayer().getId());
                ps.setInt(2, cidFrom);
                ps.setString(3, GameConstants.DEFAULT_BUDDY_GROUP);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        put(new BuddylistEntry(nameFrom, GameConstants.DEFAULT_BUDDY_GROUP, cidFrom, channelFrom, false));
        if (pendingRequests.isEmpty()) {
            c.sendPacket(PacketCreator.requestBuddylistAdd(cidFrom, c.getPlayer().getId(), nameFrom));
        } else {
            pendingRequests.push(new CharacterNameAndId(cidFrom, nameFrom));
        }
    }
}
