package org.gms.net.packet;

import org.gms.net.opcodes.Opcode;
import org.gms.net.opcodes.SendOpcode;

import java.awt.*;

/**
 * 出站数据包接口
 * 定义发送给客户端的数据包的写入方法
 * 提供各种数据类型的写入方法，按小端序（Little-Endian）写入
 *
 * @author OdinMS开发团队
 */
public interface OutPacket extends Packet {
    /**
     * 写入一个字节
     *
     * @param value 字节值
     */
    void writeByte(byte value);

    /**
     * 写入一个字节（int参数重载）
     *
     * @param value 整数值，会被转换为byte
     */
    void writeByte(int value);

    /**
     * 写入字节数组
     *
     * @param value 字节数组
     */
    void writeBytes(byte[] value);

    /**
     * 写入一个短整数（2字节，小端序）
     *
     * @param value 短整数值
     */
    void writeShort(int value);

    /**
     * 写入一个整数（4字节，小端序）
     *
     * @param value 整数值
     */
    void writeInt(int value);

    /**
     * 写入一个长整数（8字节，小端序）
     *
     * @param value 长整数值
     */
    void writeLong(long value);

    /**
     * 写入一个布尔值（1字节）
     *
     * @param value 布尔值，true写入1，false写入0
     */
    void writeBool(boolean value);

    /**
     * 写入一个字符串（先写2字节长度，再写字符串字节）
     *
     * @param value 字符串值
     */
    void writeString(String value);

    /**
     * 写入固定长度字符串（默认13字节）
     *
     * @param value 字符串值
     */
    void writeFixedString(String value);

    /**
     * 写入指定长度的固定字符串
     *
     * @param value 字符串值
     * @param fixed 固定长度
     */
    void writeFixedString(String value, int fixed);

    /**
     * 写入一个坐标点（X,Y各2字节，小端序）
     *
     * @param value 坐标点对象
     */
    void writePos(Point value);

    /**
     * 跳过指定字节数（填充0）
     *
     * @param numberOfBytes 要跳过的字节数
     */
    void skip(int numberOfBytes);

    /**
     * 工厂方法：创建一个指定操作码的出站数据包
     *
     * @param opcode 数据包操作码
     * @return 出站数据包实例
     */
    static OutPacket create(Opcode opcode) {
        return new ByteBufOutPacket(opcode);
    }
}
