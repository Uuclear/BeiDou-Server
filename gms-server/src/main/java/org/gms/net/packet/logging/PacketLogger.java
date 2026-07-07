package org.gms.net.packet.logging;

import org.gms.net.packet.Packet;

/**
 * 封包日志记录器接口，用于调试时输出封包内容。
 */
public interface PacketLogger {
    /**
     * 记录一条封包日志。
     *
     * @param packet 待记录的封包
     */
    void log(Packet packet);
}
