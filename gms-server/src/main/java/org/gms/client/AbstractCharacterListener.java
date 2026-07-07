/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
 * 角色属性变更监听器接口，在 HP、属性池等数据变化时接收回调通知。
 */
public interface AbstractCharacterListener {
    /**
     * HP 数值发生变化时回调。
     *
     * @param oldHp 变更前的 HP
     */
    void onHpChanged(int oldHp);

    /** HP/MP 池上限或当前值更新时回调。 */
    void onHpMpPoolUpdate();

    /** 角色基础属性更新时回调。 */
    void onStatUpdate();

    /** 需要向客户端广播属性池更新时回调。 */
    void onAnnounceStatPoolUpdate();
}
