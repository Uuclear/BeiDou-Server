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
package org.gms.net.packet.logging;

import org.gms.client.Character;
import org.gms.client.Client;
import net.jcip.annotations.NotThreadSafe;
import org.gms.net.opcodes.RecvOpcode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.HexTool;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 监控角色数据包日志记录器
 * 用于记录特定被监控角色的数据包到日志文件，方便调试和分析
 *
 * @author Alan (SharpAceX)
 */
@NotThreadSafe
public class MonitoredChrLogger {
    private static final Logger log = LoggerFactory.getLogger(MonitoredChrLogger.class);

    /**
     * 被监控的角色ID集合
     */
    private static final Set<Integer> monitoredChrIds = new HashSet<>();

    /**
     * 切换角色的监控状态
     *
     * @param chrId 角色ID
     * @return 新的监控状态：true表示现在被监控，false表示不再监控
     */
    public static boolean toggleMonitored(int chrId) {
        if (monitoredChrIds.contains(chrId)) {
            monitoredChrIds.remove(chrId);
            return false;
        } else {
            monitoredChrIds.add(chrId);
            return true;
        }
    }

    /**
     * 获取所有被监控的角色ID集合
     *
     * @return 被监控角色ID集合
     */
    public static Collection<Integer> getMonitoredChrIds() {
        return monitoredChrIds;
    }

    /**
     * 如果角色被监控，则记录数据包
     * 会忽略高频操作码（移动、聊天、受伤等）
     *
     * @param c 客户端连接
     * @param packetId 数据包ID（操作码）
     * @param packetContent 数据包内容
     */
    public static void logPacketIfMonitored(Client c, short packetId, byte[] packetContent) {
        Character chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        if (!monitoredChrIds.contains(chr.getId())) {
            return;
        }
        RecvOpcode op = getOpcodeFromValue(packetId);
        if (isRecvBlocked(op)) {
            return;
        }

        String packet = packetContent.length > 0 ? HexTool.toHexString(packetContent) : "<empty>";
        log.info("{}-{} {}-{}", c.getAccountName(), chr.getName(), packetId, packet);
    }

    /**
     * 检查接收操作码是否被阻止记录
     * 阻止记录高频操作码
     *
     * @param op 接收操作码
     * @return 如果被阻止返回true
     */
    private static boolean isRecvBlocked(RecvOpcode op) {
        return switch (op) {
            case MOVE_PLAYER, GENERAL_CHAT, TAKE_DAMAGE, MOVE_PET, MOVE_LIFE, NPC_ACTION, FACE_EXPRESSION -> true;
            default -> false;
        };
    }

    /**
     * 根据操作码值查找对应的RecvOpcode枚举
     *
     * @param value 操作码值
     * @return RecvOpcode枚举，未找到返回null
     */
    private static RecvOpcode getOpcodeFromValue(int value) {
        return Arrays.stream(RecvOpcode.values())
                .filter(opcode -> value == opcode.getValue())
                .findAny()
                .orElse(null);
    }
}
