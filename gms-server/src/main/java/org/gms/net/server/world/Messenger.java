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
package org.gms.net.server.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 聊天室（Messenger）实体，最多容纳 3 名成员并管理座位占用状态。
 */
public final class Messenger {

    private final int id;
    private final List<MessengerCharacter> members = new ArrayList<>(3);
    private final boolean[] pos = new boolean[3];

    /**
     * 构造聊天室并指定创建者与初始座位。
     *
     * @param id     聊天室 ID
     * @param chrfor 创建者成员快照
     */
    public Messenger(int id, MessengerCharacter chrfor) {
        this.id = id;
        for (int i = 0; i < 3; i++) {
            pos[i] = false;
        }
        addMember(chrfor, chrfor.getPosition());
    }

    /** 返回聊天室 ID。 */
    public int getId() {
        return id;
    }

    /** 返回不可变的成员列表副本。 */
    public Collection<MessengerCharacter> getMembers() {
        return Collections.unmodifiableList(members);
    }

    /**
     * 添加成员到指定座位。
     *
     * @param member   成员快照
     * @param position 座位索引（0-2）
     */
    public void addMember(MessengerCharacter member, int position) {
        members.add(member);
        member.setPosition(position);
        pos[position] = true;
    }

    /**
     * 移除成员并释放其座位。
     *
     * @param member 待移除成员
     */
    public void removeMember(MessengerCharacter member) {
        int position = member.getPosition();
        pos[position] = false;
        members.remove(member);
    }

    /** 返回当前最低可用座位索引，满员时返回 -1。 */
    public int getLowestPosition() {
        for (byte i = 0; i < 3; i++) {
            if (!pos[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 按角色名查询座位索引。
     *
     * @param name 角色名
     * @return 座位索引，未找到返回 -1
     */
    public int getPositionByName(String name) {
        for (MessengerCharacter messengerchar : members) {
            if (messengerchar.getName().equals(name)) {
                return messengerchar.getPosition();
            }
        }
        return -1;
    }
}

