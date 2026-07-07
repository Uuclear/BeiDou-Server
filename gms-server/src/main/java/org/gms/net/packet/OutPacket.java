package org.gms.net.packet;

import org.gms.net.opcodes.Opcode;
import org.gms.net.opcodes.SendOpcode;

import java.awt.*;

/**
 * 出站封包写入接口，提供 v83 协议约定的二进制字段序列化方法。
 * <p>
 * 多字节整数均为小端序；字符串为 {@code short 长度 + 字节}。
 * 通过 {@link #create(Opcode)} 创建的实例会自动在缓冲区首部写入 SendOpcode。
 * </p>
 */
public interface OutPacket extends Packet {
    void writeByte(byte value);
    void writeByte(int value);
    void writeBytes(byte[] value);
    void writeShort(int value);
    void writeInt(int value);
    void writeLong(long value);
    /** 写入布尔值：{@code 1} 或 {@code 0} */
    void writeBool(boolean value);
    /** 写入长度前缀字符串 */
    void writeString(String value);
    /** 写入固定 13 字节的定长字符串（不足补零） */
    void writeFixedString(String value);
    /** 写入指定长度的定长字符串（不足补零） */
    void writeFixedString(String value, int fixed);
    /** 写入坐标：连续两个 short（x, y） */
    void writePos(Point value);
    /** 写入指定长度的零字节填充 */
    void skip(int numberOfBytes);

    /**
     * 创建带操作码前缀的出站封包。
     *
     * @param opcode 发送操作码，作为载荷首 2 字节写入
     * @return 新的 {@link ByteBufOutPacket} 实例
     */
    static OutPacket create(Opcode opcode) {
        return new ByteBufOutPacket(opcode);
    }
}
