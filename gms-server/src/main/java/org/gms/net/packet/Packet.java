package org.gms.net.packet;

/**
 * 封包基础接口，表示一段可序列化的字节数据。
 * <p>
 * 加解密 Pipeline 的出站方向以 {@link Packet#getBytes()} 获取完整载荷后编码；
 * 入站方向解码后由 {@link InPacket} 提供结构化读取。
 * </p>
 */
public interface Packet {
    /** @return 封包完整字节内容（出站时含 opcode 前缀） */
    byte[] getBytes();
}
