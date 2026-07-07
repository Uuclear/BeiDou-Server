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

import java.awt.*;

/**
 * 生命体移动抽象基类。
 */
public abstract class AbstractLifeMovement implements LifeMovement {
    private final Point position;
    private final int duration;
    private final int newstate;
    private final int type;

    /**
     * 构造 AbstractLifeMovement 实例。
     * @param type 类型
     * @param position 坐标
     * @param duration duration
     * @param newstate newstate
     */
    public AbstractLifeMovement(int type, Point position, int duration, int newstate) {
        super();
        this.type = type;
        this.position = position;
        this.duration = duration;
        this.newstate = newstate;
    }

    /**
     * 获取类型。
     * @return int 类型结果
     */
    @Override
    public int getType() {
        return this.type;
    }

    /**
     * 获取持续时间。
     * @return int 类型结果
     */
    @Override
    public int getDuration() {
        return duration;
    }

    /**
     * 获取新状态。
     * @return int 类型结果
     */
    @Override
    public int getNewstate() {
        return newstate;
    }

    /**
     * 获取位置。
     * @return Point 类型结果
     */
    @Override
    public Point getPosition() {
        return position;
    }
}
