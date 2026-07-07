package org.gms.net.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.gms.constants.net.ServerConstants;
import org.gms.net.encryption.protocol.ProtocolFactory;
import org.gms.net.packet.Packet;

/**
 * Netty 出站编码器：将 {@link Packet} 编码为加密的 TCP 字节流。
 * <p>
 * 编码顺序（v83）：写入 4 字节包头 → Maple 自定义加密 → AES-OFB 加密。
 * </p>
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {
    private final ProtocolFactory protocolFactory;

    /**
     * @param protocolFactory 协议工厂，按服务端版本选择编码实现
     */
    public PacketEncoder(ProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet in, ByteBuf out) {
        protocolFactory.getProtocol(ServerConstants.VERSION).encode(ctx, in, out);
    }
}
