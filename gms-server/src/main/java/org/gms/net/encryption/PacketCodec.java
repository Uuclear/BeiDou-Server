package org.gms.net.encryption;

import io.netty.channel.CombinedChannelDuplexHandler;
import org.gms.net.encryption.protocol.ProtocolFactory;

/**
 * 数据包编解码器
 * Netty的组合双通道处理器，将解码器和编码器组合在一起
 * 入站数据解码，出站数据编码
 *
 * @author OdinMS开发团队
 */
public class PacketCodec extends CombinedChannelDuplexHandler<PacketDecoder, PacketEncoder> {
    /**
     * 构造数据包编解码器
     *
     * @param protocolFactory 协议工厂，用于创建对应版本的协议处理器
     */
    public PacketCodec(ProtocolFactory protocolFactory) {
        super(new PacketDecoder(protocolFactory), new PacketEncoder(protocolFactory));
    }
}
