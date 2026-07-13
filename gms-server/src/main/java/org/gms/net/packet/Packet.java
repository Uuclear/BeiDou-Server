package org.gms.net.packet;

/**
 * 数据包接口
 * 定义所有数据包的通用方法，是InPacket（入站数据包）和OutPacket（出站数据包）的基础接口
 *
 * @author OdinMS开发团队
 */
public interface Packet {
    /**
     * 获取数据包的字节数组内容
     *
     * @return 数据包的字节数组
     */
    byte[] getBytes();
}
