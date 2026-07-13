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
package org.gms.net;

import org.gms.client.Client;
import org.gms.net.server.Server;

/**
 * 数据包处理器抽象基类
 * 提供了PacketHandler接口的默认实现，作为所有具体数据包处理器的父类
 * 默认验证客户端是否已登录状态，并提供获取服务器当前时间的工具方法
 *
 * @author OdinMS开发团队
 */
public abstract class AbstractPacketHandler implements PacketHandler {
    /**
     * 验证客户端状态的默认实现
     * 检查客户端是否已经登录到游戏服务器
     *
     * @param c 客户端连接对象
     * @return 如果客户端已登录返回true，否则返回false
     */
    @Override
    public boolean validateState(Client c) {
        return c.isLoggedIn();
    }

    /**
     * 获取服务器当前时间的工具方法
     * 供子类处理器使用，避免直接依赖Server单例
     *
     * @return 服务器当前时间的毫秒数
     */
    protected static long currentServerTime() {
        return Server.getInstance().getCurrentTime();
    }
}
