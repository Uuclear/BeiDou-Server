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
 * 可覆盖的怪物属性（用于事件副本动态调参）。
 */
public class OverrideMonsterStats {

    public int hp;
    public int exp, mp;

    /**
     * 构造 OverrideMonsterStats 实例。
     */
    public OverrideMonsterStats() {
        hp = 1;
        exp = 0;
        mp = 0;
    }

    /**
     * 构造 OverrideMonsterStats 实例。
     * @param hp hp
     * @param mp mp
     * @param exp exp
     * @param change change
     */
    public OverrideMonsterStats(int hp, int mp, int exp, boolean change) {
        this.hp = /*change ? (hp * 3L / 2L) : */ hp;
        this.mp = mp;
        this.exp = exp;
    }

    /**
     * 构造 OverrideMonsterStats 实例。
     * @param hp hp
     * @param mp mp
     * @param exp exp
     */
    public OverrideMonsterStats(int hp, int mp, int exp) {
        this(hp, mp, exp, true);
    }

    /**
     * 获取经验。
     * @return int 类型结果
     */
    public int getExp() {
        return exp;
    }

    /**
     * 设置O经验。
     * @param exp exp
     */
    public void setOExp(int exp) {
        this.exp = exp;
    }

    /**
     * 获取HP。
     * @return int 类型结果
     */
    public int getHp() {
        return hp;
    }

    /**
     * 设置OHP。
     * @param hp hp
     */
    public void setOHp(int hp) {
        this.hp = hp;
    }

    /**
     * 获取MP。
     * @return int 类型结果
     */
    public int getMp() {
        return mp;
    }

    /**
     * 设置OMP。
     * @param mp mp
     */
    public void setOMp(int mp) {
        this.mp = mp;
    }
}