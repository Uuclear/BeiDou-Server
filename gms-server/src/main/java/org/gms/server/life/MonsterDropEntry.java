/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
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
package org.gms.server.life;

/**
 * 怪物掉落条目（物品、概率、任务关联、数量范围）。
 */
public class MonsterDropEntry {
    /**
     * 构造 MonsterDropEntry 实例。
     * @param itemId 物品 ID
     * @param chance chance
     * @param Minimum Minimum
     * @param Maximum Maximum
     * @param questid 任务 ID
     */
    public MonsterDropEntry(int itemId, int chance, int Minimum, int Maximum, short questid) {
        this.itemId = itemId;
        this.chance = chance;
        this.questid = questid;
        this.Minimum = Minimum;
        this.Maximum = Maximum;
    }

    public short questid;
    public int itemId, chance, Minimum, Maximum;
}