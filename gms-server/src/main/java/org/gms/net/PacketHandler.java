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
import org.gms.net.packet.InPacket;

/**
 * 数据包处理器接口
 * 定义了处理客户端发送的数据包的基本方法
 * 所有具体的数据包处理器都需要实现此接口
 *
 * @author OdinMS开发团队
 */
public interface PacketHandler {
    /**
     * 处理接收到的数据包
     *
     * @param p 输入数据包对象，包含数据包的具体数据内容
     * @param c 客户端连接对象，代表与客户端的连接会话
     */
    void handlePacket(InPacket p, Client c);

    /**
     * 验证客户端连接状态是否有效
     * 用于在处理数据包前检查客户端是否处于合适的状态（如已登录等）
     *
     * @param c 客户端连接对象
     * @return 如果客户端状态有效返回true，否则返回false
     */
    boolean validateState(Client c);
}
