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

/**
 * 好友列表条目，表示一名好友的 ID、名称、分组及在线状态。
 */
public class BuddylistEntry {
    private final String name;
    private String group;
    private final int cid;
    private int channel;
    private boolean visible;

    /**
     * @param name
     * @param characterId
     * @param channel     should be -1 if the buddy is offline
     * @param visible
     */
    /**
     * Buddylist条目
     * @param name 名称
     * @param group group
     * @param characterId characterId
     * @param channel 频道
     * @param visible visible
     */
    public BuddylistEntry(String name, String group, int characterId, int channel, boolean visible) {
        this.name = name;
        this.group = group;
        this.cid = characterId;
        this.channel = channel;
        this.visible = visible;
    }

    /**
     * @return the channel the character is on. If the character is offline returns -1.
     */
    /**
     * 获取频道
     * @return 返回值
     */
    public int getChannel() {
        return channel;
    }

    /**
     * 设置频道
     * @param channel 频道
     */
    public void setChannel(int channel) {
        this.channel = channel;
    }

    /**
     * 判断是否为在线
     * @return 返回值
     */
    public boolean isOnline() {
        return channel >= 0;
    }

    /**
     * 获取名称
     * @return 返回值
     */
    public String getName() {
        return name;
    }

    /**
     * 获取Group
     * @return 返回值
     */
    public String getGroup() {
        return group;
    }

    /**
     * 获取角色ID
     * @return 返回值
     */
    public int getCharacterId() {
        return cid;
    }

    /**
     * 设置可见
     * @param visible visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * 判断是否为可见
     * @return 返回值
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * 变更Group
     * @param group group
     */
    public void changeGroup(String group) {
        this.group = group;
    }

    /**
     * 返回对象的哈希码
     * @return 返回值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cid;
        return result;
    }

    /**
     * 判断对象是否相等
     * @param obj obj
     * @return 返回值
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BuddylistEntry other = (BuddylistEntry) obj;
        return cid == other.cid;
    }
}
