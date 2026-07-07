package org.gms.net.packet;

import java.awt.*;

/**
 * 入站封包读取接口，提供 v83 协议约定的二进制字段解析方法。
 * <p>
 * 所有多字节整数均为<strong>小端序（Little-Endian）</strong>；
 * 字符串格式为 {@code short 长度 + UTF-8/GBK 字节}（编码由客户端语言决定）。
 * 解密后的首个 short 为 {@link org.gms.net.opcodes.RecvOpcode}，已由 {@link Client} 读取，
 * 处理器从此接口的当前位置继续解析业务字段。
 * </p>
 */
public interface InPacket extends Packet {
    /** 读取有符号字节 */
    byte readByte();
    /** 读取无符号字节（0–255） */
    short readUnsignedByte();
    /** 读取小端序 short */
    short readShort();
    /** 读取小端序 int */
    int readInt();
    /** 读取小端序 long */
    long readLong();
    /** 读取坐标：连续两个 short（x, y） */
    Point readPos();
    /** 读取 Maple 长度前缀字符串 */
    String readString();
    /** 读取指定长度的原始字节 */
    byte[] readBytes(int numberOfBytes);
    /** 跳过指定字节数 */
    void skip(int numberOfBytes);
    /** @return 剩余可读字节数 */
    int available();
    /** 将读指针移动到绝对偏移 */
    void seek(int byteOffset);
    /** @return 当前读指针位置 */
    int getPosition();
}
