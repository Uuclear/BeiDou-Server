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
 * 好友列表类
 * 管理角色的好友列表，包括添加、删除、好友上下线通知等功能
 *
 * @author OdinMS Team
 */
public class BuddyList {
    /**
     * 好友操作枚举
     */
    public enum BuddyOperation {
        /** 添加好友 */
        ADDED,
        /** 删除好友 */
        DELETED
    }

    /**
     * 添加好友结果枚举
     */
    public enum BuddyAddResult {
        /** 好友列表已满 */
        BUDDYLIST_FULL,
        /** 已在好友列表中 */
        ALREADY_ON_LIST,
        /** 添加成功 */
        OK
    }

    /** 好友列表，key为角色ID，value为好友条目 */
    private final Map<Integer, BuddylistEntry> buddies = new LinkedHashMap<>();
    /** 好友列表容量 */
    private int capacity;
    /** 待处理的好友请求队列 */
    private final Deque<CharacterNameAndId> pendingRequests = new LinkedList<>();

    /**
     * 构造函数
     * @param capacity 好友列表初始容量
     */
    public BuddyList(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 检查好友列表中是否包含指定角色
     * @param characterId 角色ID
     * @return 是否包含
     */
    public boolean contains(int characterId) {
        synchronized (buddies) {
            return buddies.containsKey(characterId);
        }
    }

    /**
     * 检查好友列表中是否包含指定角色且该好友可见
     * @param characterId 角色ID
     * @return 是否可见
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
     * 获取好友列表容量
     * @return 容量
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * 设置好友列表容量
     * @param capacity 新容量
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 根据角色ID获取好友条目
     * @param characterId 角色ID
     * @return 好友条目
     */
    public BuddylistEntry get(int characterId) {
        synchronized (buddies) {
            return buddies.get(characterId);
        }
    }

    /**
     * 根据角色名称获取好友条目
     * @param characterName 角色名称
     * @return 好友条目
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
     * 添加好友到列表
     * @param entry 好友条目
     */
    public void put(BuddylistEntry entry) {
        synchronized (buddies) {
            buddies.put(entry.getCharacterId(), entry);
        }
    }

    /**
     * 从列表中移除好友
     * @param characterId 角色ID
     */
    public void remove(int characterId) {
        synchronized (buddies) {
            buddies.remove(characterId);
        }
    }

    /**
     * 获取所有好友（不可修改视图）
     * @return 好友条目集合
     */
    public Collection<BuddylistEntry> getBuddies() {
        synchronized (buddies) {
            return Collections.unmodifiableCollection(buddies.values());
        }
    }

    /**
     * 检查好友列表是否已满
     * @return 是否已满
     */
    public boolean isFull() {
        synchronized (buddies) {
            return buddies.size() >= capacity;
        }
    }

    /**
     * 获取所有好友ID数组
     * @return 好友ID数组
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
     * 向所有在线好友广播数据包
     * @param packet 要广播的数据包
     * @param pstorage 玩家存储
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
     * 从数据库加载好友列表
     * @param characterId 角色ID
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
     * 取出待处理的好友请求
     * @return 待处理请求
     */
    public CharacterNameAndId pollPendingRequest() {
        return pendingRequests.pollLast();
    }

    /**
     * 添加好友请求
     * @param c 客户端
     * @param cidFrom 请求发起方角色ID
     * @param nameFrom 请求发起方名称
     * @param channelFrom 请求发起方频道
     */
    public void addBuddyRequest(Client c, int cidFrom, String nameFrom, int channelFrom) {
        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM buddies WHERE characterid = ? AND buddyid = ?")) {
                ps.setInt(1, c.getPlayer().getId());
                ps.setInt(2, cidFrom);
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
