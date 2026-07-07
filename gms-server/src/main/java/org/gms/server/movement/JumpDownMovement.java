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
 * 跳下平台移动。
 */
public class JumpDownMovement extends AbstractLifeMovement {
    private Point pixelsPerSecond;
    private int fh;
    private int originFh;

    /**
     * 构造 JumpDownMovement 实例。
     * @param type 类型
     * @param position 坐标
     * @param duration duration
     * @param newstate newstate
     */
    public JumpDownMovement(int type, Point position, int duration, int newstate) {
        super(type, position, duration, newstate);
    }

    /**
     * 获取Pixels、Per、Second。
     * @return Point 类型结果
     */
    public Point getPixelsPerSecond() {
        return pixelsPerSecond;
    }

    /**
     * 设置Pixels、Per、Second。
     * @param wobble wobble
     */
    public void setPixelsPerSecond(Point wobble) {
        this.pixelsPerSecond = wobble;
    }

    /**
     * 获取Fh。
     * @return int 类型结果
     */
    public int getFh() {
        return fh;
    }

    /**
     * 设置Fh。
     * @param fh fh
     */
    public void setFh(int fh) {
        this.fh = fh;
    }

    /**
     * 获取Origin、Fh。
     * @return int 类型结果
     */
    public int getOriginFh() {
        return originFh;
    }

    /**
     * 设置Origin、Fh。
     * @param fh fh
     */
    public void setOriginFh(int fh) {    // fh actually originFh, thanks Spoon for pointing this out
        this.originFh = fh;
    }

    /**
     * 执行 serialize 操作。
     * @param p p
     */
    @Override
    public void serialize(OutPacket p) {
        p.writeByte(getType());
        p.writePos(getPosition());
        p.writePos(pixelsPerSecond);
        p.writeShort(fh);
        p.writeShort(originFh);
        p.writeByte(getNewstate());
        p.writeShort(getDuration());
    }
}
