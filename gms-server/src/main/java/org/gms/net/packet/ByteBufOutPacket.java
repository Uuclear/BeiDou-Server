package org.gms.net.packet;

import org.gms.client.Client;
import org.gms.constants.string.CharsetConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.jcip.annotations.NotThreadSafe;
import org.gms.net.opcodes.Opcode;
import org.gms.net.opcodes.SendOpcode;
import org.gms.util.ThreadLocalUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

/**
 * 基于Netty ByteBuf的出站数据包实现
 * 使用Netty的ByteBuf作为底层数据存储，提供小端序的数据写入能力
 * 非线程安全，每个线程应使用独立实例
 *
 * @author OdinMS开发团队
 */
@NotThreadSafe
public class ByteBufOutPacket implements OutPacket {
    /**
     * Netty字节缓冲区
     */
    private final ByteBuf byteBuf;

    /**
     * 构造空的出站数据包（不写入操作码）
     */
    public ByteBufOutPacket() {
        this.byteBuf = Unpooled.buffer();
    }

    /**
     * 构造指定操作码的出站数据包
     *
     * @param op 数据包操作码
     */
    public ByteBufOutPacket(Opcode op) {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeShortLE((short) op.getValue());
        this.byteBuf = byteBuf;
    }

    /**
     * 构造指定操作码和初始容量的出站数据包
     *
     * @param op 发送操作码
     * @param initialCapacity 初始缓冲区容量
     */
    public ByteBufOutPacket(SendOpcode op, int initialCapacity) {
        ByteBuf byteBuf = Unpooled.buffer(initialCapacity);
        byteBuf.writeShortLE((short) op.getValue());
        this.byteBuf = byteBuf;
    }

    /**
     * 获取整个数据包的字节数组
     *
     * @return 数据包字节数组
     */
    @Override
    public byte[] getBytes() {
        return ByteBufUtil.getBytes(byteBuf);
    }

    /**
     * 写入一个字节
     *
     * @param value 字节值
     */
    @Override
    public void writeByte(byte value) {
        byteBuf.writeByte(value);
    }

    /**
     * 写入一个字节（int参数重载）
     *
     * @param value 整数值，自动转换为byte
     */
    @Override
    public void writeByte(int value) {
        writeByte((byte) value);
    }

    /**
     * 写入字节数组
     *
     * @param value 字节数组
     */
    @Override
    public void writeBytes(byte[] value) {
        byteBuf.writeBytes(value);
    }

    /**
     * 写入一个短整数（2字节，小端序）
     *
     * @param value 短整数值
     */
    @Override
    public void writeShort(int value) {
        byteBuf.writeShortLE(value);
    }

    /**
     * 写入一个整数（4字节，小端序）
     *
     * @param value 整数值
     */
    @Override
    public void writeInt(int value) {
        byteBuf.writeIntLE(value);
    }

    /**
     * 写入一个长整数（8字节，小端序）
     *
     * @param value 长整数值
     */
    @Override
    public void writeLong(long value) {
        byteBuf.writeLongLE(value);
    }

    /**
     * 写入布尔值（1字节：true=1, false=0）
     *
     * @param value 布尔值
     */
    @Override
    public void writeBool(boolean value) {
        byteBuf.writeByte(value ? 1 : 0);
    }

    /**
     * 写入字符串（先写length:short，再写字符串字节）
     * 使用客户端当前语言的字符集编码
     *
     * @param value 字符串值
     */
    @Override
    public void writeString(String value) {
        byte[] bytes = value.getBytes(CharsetConstants.getCharset(ThreadLocalUtil.getClientLang()));
        writeShort(bytes.length);
        writeBytes(bytes);
    }

    /**
     * 写入固定长度字符串（默认13字节）
     *
     * @param value 字符串值
     */
    @Override
    public void writeFixedString(String value) {
        writeFixedString(value, 13);
    }

    /**
     * 写入指定长度的固定字符串，不足补0
     *
     * @param value 字符串值
     * @param fixed 固定长度
     */
    @Override
    public void writeFixedString(String value, int fixed) {
        writeBytes(Arrays.copyOf(value.getBytes(CharsetConstants.getCharset(ThreadLocalUtil.getClientLang())), fixed));
    }

    /**
     * 写入坐标点（X:short, Y:short，小端序）
     *
     * @param value 坐标点对象
     */
    @Override
    public void writePos(Point value) {
        writeShort((short) value.getX());
        writeShort((short) value.getY());
    }

    /**
     * 跳过指定字节数（填充0）
     *
     * @param numberOfBytes 要跳过的字节数
     */
    @Override
    public void skip(int numberOfBytes) {
        writeBytes(new byte[numberOfBytes]);
    }

    /**
     * 判断两个数据包是否相等
     *
     * @param o 比较对象
     * @return 如果底层ByteBuf相等返回true
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof ByteBufOutPacket other && byteBuf.equals(other.byteBuf);
    }
}
