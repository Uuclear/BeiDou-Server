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
package org.gms.scripting.portal;

/**
 * 传送门脚本接口，由 GraalJS 脚本通过 {@code getInterface(PortalScript.class)} 实现。
 * <p>
 * {@link PortalScriptManager} 加载 {@code scripts/portal/*.js} 后获取本接口代理，
 * 玩家触碰传送门时调用 {@link #enter(PortalPlayerInteraction)} 决定是否允许传送。
 * </p>
 */
public interface PortalScript {
    /**
     * 传送门脚本入口。
     *
     * @param ppi 传送门玩家交互对象，可执行传送、封锁门户等操作
     * @return {@code true} 表示脚本已处理传送（阻止默认逻辑），{@code false} 使用默认传送
     */
    boolean enter(PortalPlayerInteraction ppi);
}