package org.gms.net.packet;

import org.gms.constants.string.CharsetConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.gms.util.ThreadLocalUtil;

import java.awt.*;

/**
 * 基于Netty ByteBuf的入站数据包实现
 * 使用Netty的ByteBuf作为底层数据存储，提供小端序的数据读取能力
 *
 * @author OdinMS开发团队
 */
public class ByteBufInPacket implements InPacket {
    /**
     * Netty字节缓冲区
     */
    private final ByteBuf byteBuf;

    /**
     * 构造入站数据包
     *
     * @param byteBuf Netty字节缓冲区
     */
    public ByteBufInPacket(ByteBuf byteBuf) {
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
     * 读取一个字节
     *
     * @return 字节值
     */
    @Override
    public byte readByte() {
        return byteBuf.readByte();
    }

    /**
     * 读取一个无符号字节
     *
     * @return 无符号字节值（0~255）
     */
    @Override
    public short readUnsignedByte() { return byteBuf.readUnsignedByte(); }

    /**
     * 读取一个短整数（2字节，小端序）
     *
     * @return 短整数值
     */
    @Override
    public short readShort() {
        return byteBuf.readShortLE();
    }

    /**
     * 读取一个整数（4字节，小端序）
     *
     * @return 整数值
     */
    @Override
    public int readInt() {
        return byteBuf.readIntLE();
    }

    /**
     * 读取一个长整数（8字节，小端序）
     *
     * @return 长整数值
     */
    @Override
    public long readLong() {
        return byteBuf.readLongLE();
    }

    /**
     * 读取坐标点（X:short, Y:short，小端序）
     *
     * @return 坐标点对象
     */
    @Override
    public Point readPos() {
        final short x = byteBuf.readShortLE();
        final short y = byteBuf.readShortLE();
        return new Point(x, y);
    }

    /**
     * 读取字符串（先读length:short，再读length字节）
     * 使用客户端当前语言的字符集解码
     *
     * @return 解码后的字符串
     */
    @Override
    public String readString() {
        short length = readShort();
        byte[] stringBytes = new byte[length];
        byteBuf.readBytes(stringBytes);
        return new String(stringBytes, CharsetConstants.getCharset(ThreadLocalUtil.getClientLang()));
    }

    /**
     * 读取指定字节数
     *
     * @param numberOfBytes 字节数
     * @return 字节数组
     */
    @Override
    public byte[] readBytes(int numberOfBytes) {
        byte[] bytes = new byte[numberOfBytes];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    /**
     * 跳过指定字节数
     *
     * @param numberOfBytes 要跳过的字节数
     */
    @Override
    public void skip(int numberOfBytes) {
        byteBuf.skipBytes(numberOfBytes);
    }

    /**
     * 获取剩余可读字节数
     *
     * @return 可读字节数
     */
    @Override
    public int available() {
        return byteBuf.readableBytes();
    }

    /**
     * 设置读取位置
     *
     * @param byteOffset 字节偏移量
     */
    @Override
    public void seek(int byteOffset) {
        byteBuf.readerIndex(byteOffset);
    }

    /**
     * 获取当前读取位置
     *
     * @return 当前读取索引
     */
    @Override
    public int getPosition() {
        return byteBuf.readerIndex();
    }

    /**
     * 判断两个数据包是否相等
     *
     * @param o 比较对象
     * @return 如果底层ByteBuf相等返回true
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof ByteBufInPacket other && byteBuf.equals(other.byteBuf);
    }

    /**
     * 将数据包转换为十六进制字符串表示，在当前读取位置插入下划线标记
     *
     * @return 十六进制转储字符串
     */
    @Override
    public String toString() {
        final int readerIndex = byteBuf.readerIndex();
        byteBuf.markReaderIndex();
        byteBuf.readerIndex(0);

        String hexDumpWithPosition = insertReaderPosition(ByteBufUtil.hexDump(byteBuf).toUpperCase(), readerIndex);
        String toString = String.format("ByteBufInPacket[%s]", hexDumpWithPosition);

        byteBuf.resetReaderIndex();
        return toString;
    }

    /**
     * 在十六进制转储字符串中插入读取位置标记
     *
     * @param hexDump 十六进制转储字符串
     * @param index 读取位置（字节偏移）
     * @return 插入标记后的字符串
     */
    private static String insertReaderPosition(String hexDump, int index) {
        StringBuilder sb = new StringBuilder(hexDump);
        sb.insert(2 * index, '_');
        return sb.toString();
    }
}
