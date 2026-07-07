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
package org.gms.server.movement;

import org.gms.net.packet.OutPacket;

import java.awt.*;

/**
 * 移动过程中切换装备外观片段。
 */
public class ChangeEquip implements LifeMovementFragment {
    private final int wui;

    /**
     * 构造 ChangeEquip 实例。
     * @param wui wui
     */
    public ChangeEquip(int wui) {
        this.wui = wui;
    }

    /**
     * 执行 serialize 操作。
     * @param p p
     */
    @Override
    public void serialize(OutPacket p) {
        p.writeByte(10);
        p.writeByte(wui);
    }

    /**
     * 获取位置。
     * @return Point 类型结果
     */
    @Override
    public Point getPosition() {
        return new Point(0, 0);
    }
}
