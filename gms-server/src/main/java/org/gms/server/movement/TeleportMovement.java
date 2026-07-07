/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
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
package org.gms.server.movement;

import org.gms.net.packet.OutPacket;

import java.awt.*;

/**
 * 传送式移动（瞬移到目标点）。
 */
public class TeleportMovement extends AbsoluteLifeMovement {

    /**
     * 构造 TeleportMovement 实例。
     * @param type 类型
     * @param position 坐标
     * @param newstate newstate
     */
    public TeleportMovement(int type, Point position, int newstate) {
        super(type, position, 0, newstate);
    }

    /**
     * 执行 serialize 操作。
     * @param p p
     */
    @Override
    public void serialize(OutPacket p) {
        p.writeByte(getType());
        p.writePos(getPosition());
        p.writePos(getPixelsPerSecond());
        p.writeByte(getNewstate());
    }
}
