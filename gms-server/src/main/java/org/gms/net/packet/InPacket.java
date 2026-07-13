package org.gms.net.packet;

import java.awt.*;

/**
 * 入站数据包接口
 * 定义从客户端接收到的数据包的读取方法
 * 提供各种数据类型的读取方法，按小端序（Little-Endian）读取
 *
 * @author OdinMS开发团队
 */
public interface InPacket extends Packet {
    /**
     * 读取一个字节
     *
     * @return 字节值（-128~127）
     */
    byte readByte();

    /**
     * 读取一个无符号字节
     *
     * @return 无符号字节值（0~255）
     */
    short readUnsignedByte();

    /**
     * 读取一个短整数（2字节，小端序）
     *
     * @return 短整数值
     */
    short readShort();

    /**
     * 读取一个整数（4字节，小端序）
     *
     * @return 整数值
     */
    int readInt();

    /**
     * 读取一个长整数（8字节，小端序）
     *
     * @return 长整数值
     */
    long readLong();

    /**
     * 读取一个坐标点（X,Y各2字节，小端序）
     *
     * @return 坐标点对象
     */
    Point readPos();

    /**
     * 读取一个字符串（先读2字节长度，再读对应字节数）
     *
     * @return 解码后的字符串
     */
    String readString();

    /**
     * 读取指定字节数的数据
     *
     * @param numberOfBytes 要读取的字节数
     * @return 字节数组
     */
    byte[] readBytes(int numberOfBytes);

    /**
     * 跳过指定字节数
     *
     * @param numberOfBytes 要跳过的字节数
     */
    void skip(int numberOfBytes);

    /**
     * 获取剩余可读字节数
     *
     * @return 可读字节数
     */
    int available();

    /**
     * 设置读取位置
     *
     * @param byteOffset 字节偏移位置
     */
    void seek(int byteOffset);

    /**
     * 获取当前读取位置
     *
     * @return 当前读取位置（字节偏移）
     */
    int getPosition();
}
