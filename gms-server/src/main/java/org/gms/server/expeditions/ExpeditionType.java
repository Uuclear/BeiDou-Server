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

package org.gms.server.expeditions;

import org.gms.config.GameConfig;

/**
 * 远征队类型枚举（扎昆、黑龙、品克缤等）。
 */
public enum ExpeditionType {
    BALROG_EASY(3, 30, 50, 255, 5),
    BALROG_NORMAL(6, 30, 50, 255, 5),
    SCARGA(6, 30, 100, 255, 5),
    SHOWA(3, 30, 100, 255, 5),
    ZAKUM(6, 30, 50, 255, 5),
    HORNTAIL(6, 30, 100, 255, 5),
    CHAOS_ZAKUM(6, 30, 120, 255, 5),
    CHAOS_HORNTAIL(6, 30, 120, 255, 5),
    ARIANT(2, 7, 20, 30, 5),
    ARIANT1(2, 7, 20, 30, 5),
    ARIANT2(2, 7, 20, 30, 5),
    PINKBEAN(6, 30, 120, 255, 5),
    CWKPQ(6, 30, 90, 255, 5);   // CWKPQ min-level 90, found thanks to Cato

    private final int minSize;
    private final int maxSize;
    private final int minLevel;
    private final int maxLevel;
    private final int registrationMinutes;

    ExpeditionType(int minSize, int maxSize, int minLevel, int maxLevel, int minutes) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.registrationMinutes = minutes;
    }

    /**
     * 获取Min、Size。
     * @return int 类型结果
     */
    public int getMinSize() {
        return !GameConfig.getServerBoolean("use_enable_solo_expeditions") ? minSize : 1;
    }

    /**
     * 获取Max、Size。
     * @return int 类型结果
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * 获取Min等级。
     * @return int 类型结果
     */
    public int getMinLevel() {
        return minLevel;
    }

    /**
     * 获取Max等级。
     * @return int 类型结果
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * 获取Registration、Minutes。
     * @return int 类型结果
     */
    public int getRegistrationMinutes() {
        return registrationMinutes;
    }
}
