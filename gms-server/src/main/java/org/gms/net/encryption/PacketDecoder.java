package org.gms.net.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.gms.constants.net.ServerConstants;
import org.gms.net.encryption.protocol.ProtocolFactory;

import java.util.List;

/**
 * Netty 入站解码器：将 TCP 字节流解析为 {@link org.gms.net.packet.InPacket}。
 * <p>
 * 使用 {@link ReplayingDecoder} 自动处理半包：数据不足时暂停，待更多字节到达后继续。
 * 实际加解密逻辑委托给 {@link org.gms.net.encryption.protocol.PacketProtocol}。
 * </p>
 */
public class PacketDecoder extends ReplayingDecoder<Void> {
    private final ProtocolFactory protocolFactory;

    /**
     * @param protocolFactory 协议工厂，按 {@link org.gms.constants.net.ServerConstants#VERSION} 选择解码实现
     */
    public PacketDecoder(ProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) {
        protocolFactory.getProtocol(ServerConstants.VERSION).decode(context, in, out);
    }
}
