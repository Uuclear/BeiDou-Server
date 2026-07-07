package org.gms.net.encryption;

import io.netty.channel.CombinedChannelDuplexHandler;
import org.gms.net.encryption.protocol.ProtocolFactory;

/**
 * Netty 组合编解码器，将 {@link PacketDecoder} 与 {@link PacketEncoder} 合并为单个 Pipeline Handler。
 * <p>
 * 在 v83 协议栈中位于 {@link org.gms.net.netty.ServerChannelInitializer} 装配的 Pipeline 内，
 * 负责 TCP 字节流 ↔ 明文 {@link org.gms.net.packet.Packet} 的双向转换。
 * </p>
 */
public class PacketCodec extends CombinedChannelDuplexHandler<PacketDecoder, PacketEncoder> {
    /**
     * @param protocolFactory 按服务端版本选择具体协议实现（如 {@link org.gms.net.encryption.protocol.GMSV83PacketProtocol}）
     */
    public PacketCodec(ProtocolFactory protocolFactory) {
        super(new PacketDecoder(protocolFactory), new PacketEncoder(protocolFactory));
    }
}
