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
 * 基于 Netty {@link ByteBuf} 的出站封包实现。
 * <p>
 * 写入的数据经 {@link org.gms.net.encryption.protocol.GMSV83PacketProtocol#encode}
 * 进行自定义加密与 AES-OFB 后发送到客户端。
 * 此类非线程安全（{@link NotThreadSafe}），每个发送操作应在同一线程完成。
 * </p>
 */
@NotThreadSafe
public class ByteBufOutPacket implements OutPacket {
    private final ByteBuf byteBuf;

    /** 创建空载荷封包（需手动写入 opcode） */
    public ByteBufOutPacket() {
        this.byteBuf = Unpooled.buffer();
    }

    /**
     * 创建封包并写入操作码作为首 2 字节（小端序）。
     *
     * @param op 发送操作码
     */
    public ByteBufOutPacket(Opcode op) {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeShortLE((short) op.getValue());
        this.byteBuf = byteBuf;
    }

    /**
     * 创建带预分配容量的封包（减少扩容）。
     *
     * @param op              发送操作码
     * @param initialCapacity 初始缓冲区容量（不含 opcode 的 2 字节）
     */
    public ByteBufOutPacket(SendOpcode op, int initialCapacity) {
        ByteBuf byteBuf = Unpooled.buffer(initialCapacity);
        byteBuf.writeShortLE((short) op.getValue());
        this.byteBuf = byteBuf;
    }

    /** {@inheritDoc} */
    @Override
    public byte[] getBytes() {
        return ByteBufUtil.getBytes(byteBuf);
    }

    /** {@inheritDoc} */
    @Override
    public void writeByte(byte value) {
        byteBuf.writeByte(value);
    }

    /** {@inheritDoc} */
    @Override
    public void writeByte(int value) {
        writeByte((byte) value);
    }

    /** {@inheritDoc} */
    @Override
    public void writeBytes(byte[] value) {
        byteBuf.writeBytes(value);
    }

    /** {@inheritDoc} — 小端序 */
    @Override
    public void writeShort(int value) {
        byteBuf.writeShortLE(value);
    }

    /** {@inheritDoc} — 小端序 */
    @Override
    public void writeInt(int value) {
        byteBuf.writeIntLE(value);
    }

    /** {@inheritDoc} — 小端序 */
    @Override
    public void writeLong(long value) {
        byteBuf.writeLongLE(value);
    }

    /** {@inheritDoc} */
    @Override
    public void writeBool(boolean value) {
        byteBuf.writeByte(value ? 1 : 0);
    }

    /** {@inheritDoc} */
    @Override
    public void writeString(String value) {
        byte[] bytes = value.getBytes(CharsetConstants.getCharset(ThreadLocalUtil.getClientLang()));
        writeShort(bytes.length);
        writeBytes(bytes);
    }

    /** {@inheritDoc} — 默认 13 字节定长 */
    @Override
    public void writeFixedString(String value) {
        writeFixedString(value, 13);
    }

    /** {@inheritDoc} — 不足部分以零字节填充 */
    @Override
    public void writeFixedString(String value, int fixed) {
        writeBytes(Arrays.copyOf(value.getBytes(CharsetConstants.getCharset(ThreadLocalUtil.getClientLang())), fixed));
    }

    /** {@inheritDoc} */
    @Override
    public void writePos(Point value) {
        writeShort((short) value.getX());
        writeShort((short) value.getY());
    }

    /** {@inheritDoc} — 写入零字节而非跳过已有数据 */
    @Override
    public void skip(int numberOfBytes) {
        writeBytes(new byte[numberOfBytes]);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ByteBufOutPacket other && byteBuf.equals(other.byteBuf);
    }
}
