package org.gms.net.packet;

import org.gms.constants.string.CharsetConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.gms.util.ThreadLocalUtil;

import java.awt.*;

/**
 * 基于 Netty {@link ByteBuf} 的入站封包实现。
 * <p>
 * 由 {@link org.gms.net.encryption.protocol.GMSV83PacketProtocol#decode} 在 AES 与自定义解密后创建，
 * 供 {@link org.gms.net.PacketHandler} 按小端序读取业务字段。
 * </p>
 */
public class ByteBufInPacket implements InPacket {
    private final ByteBuf byteBuf;

    /**
     * @param byteBuf 已解密的封包载荷（含 RecvOpcode 前缀）
     */
    public ByteBufInPacket(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    /** {@inheritDoc} */
    @Override
    public byte[] getBytes() {
        return ByteBufUtil.getBytes(byteBuf);
    }

    /** {@inheritDoc} */
    @Override
    public byte readByte() {
        return byteBuf.readByte();
    }
    /** {@inheritDoc} */
    @Override
    public short readUnsignedByte() { return byteBuf.readUnsignedByte(); }

    /** {@inheritDoc} — 小端序 */
    @Override
    public short readShort() {
        return byteBuf.readShortLE();
    }

    /** {@inheritDoc} — 小端序 */
    @Override
    public int readInt() {
        return byteBuf.readIntLE();
    }

    /** {@inheritDoc} — 小端序 */
    @Override
    public long readLong() {
        return byteBuf.readLongLE();
    }

    /** {@inheritDoc} */
    @Override
    public Point readPos() {
        final short x = byteBuf.readShortLE();
        final short y = byteBuf.readShortLE();
        return new Point(x, y);
    }

    /**
     * {@inheritDoc}
     * <p>字符串编码由 {@link ThreadLocalUtil#getClientLang()} 决定（GBK/UTF-8 等）。</p>
     */
    @Override
    public String readString() {
        short length = readShort();
        byte[] stringBytes = new byte[length];
        byteBuf.readBytes(stringBytes);
        return new String(stringBytes, CharsetConstants.getCharset(ThreadLocalUtil.getClientLang()));
    }

    /** {@inheritDoc} */
    @Override
    public byte[] readBytes(int numberOfBytes) {
        byte[] bytes = new byte[numberOfBytes];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    /** {@inheritDoc} */
    @Override
    public void skip(int numberOfBytes) {
        byteBuf.skipBytes(numberOfBytes);
    }

    /** {@inheritDoc} */
    @Override
    public int available() {
        return byteBuf.readableBytes();
    }

    /** {@inheritDoc} */
    @Override
    public void seek(int byteOffset) {
        byteBuf.readerIndex(byteOffset);
    }

    /** {@inheritDoc} */
    @Override
    public int getPosition() {
        return byteBuf.readerIndex();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ByteBufInPacket other && byteBuf.equals(other.byteBuf);
    }

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

    private static String insertReaderPosition(String hexDump, int index) {
        StringBuilder sb = new StringBuilder(hexDump);
        sb.insert(2 * index, '_');
        return sb.toString();
    }
}
