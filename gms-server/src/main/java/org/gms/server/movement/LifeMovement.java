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
 * 生命体移动接口，描述单次移动的坐标、状态与时长。
 */
public interface LifeMovement extends LifeMovementFragment {
    /**
     * 获取移动后的目标坐标。
     * @return Point 类型结果
     */
    Point getPosition();
    /**
     * 获取移动后的姿态/状态编号。
     * @return int 类型结果
     */
    int getNewstate();
    /**
     * 获取移动动画持续时间（毫秒）。
     * @return int 类型结果
     */
    int getDuration();
    /**
     * 获取移动类型编号。
     * @return int 类型结果
     */
    int getType();
}
