package org.gms.net.packet.logging;

import org.gms.net.packet.Packet;

/**
 * 数据包日志记录器接口
 * 定义记录数据包的通用方法
 *
 * @author OdinMS开发团队
 */
public interface PacketLogger {
    /**
     * 记录数据包
     *
     * @param packet 要记录的数据包
     */
    void log(Packet packet);
}
