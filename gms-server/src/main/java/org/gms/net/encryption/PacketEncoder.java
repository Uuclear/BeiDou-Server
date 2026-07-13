package org.gms.net.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.gms.constants.net.ServerConstants;
import org.gms.net.encryption.protocol.ProtocolFactory;
import org.gms.net.packet.Packet;

/**
 * 数据包编码器
 * Netty的MessageToByteEncoder实现，用于将游戏数据包编码为字节数据发送
 * 委托给具体版本的协议处理器执行实际编码逻辑
 *
 * @author OdinMS开发团队
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {
    /**
     * 协议工厂
     */
    private final ProtocolFactory protocolFactory;

    /**
     * 构造数据包编码器
     *
     * @param protocolFactory 协议工厂
     */
    public PacketEncoder(ProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    /**
     * 编码出站数据包
     *
     * @param ctx Netty通道上下文
     * @param in 输入数据包
     * @param out 输出字节缓冲区
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet in, ByteBuf out) {
        protocolFactory.getProtocol(ServerConstants.VERSION).encode(ctx, in, out);
    }
}
