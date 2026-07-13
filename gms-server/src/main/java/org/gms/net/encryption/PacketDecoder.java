package org.gms.net.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.gms.constants.net.ServerConstants;
import org.gms.net.encryption.protocol.ProtocolFactory;

import java.util.List;

/**
 * 数据包解码器
 * Netty的ReplayingDecoder实现，用于将接收到的字节数据解码为游戏数据包
 * 委托给具体版本的协议处理器执行实际解码逻辑
 *
 * @author OdinMS开发团队
 */
public class PacketDecoder extends ReplayingDecoder<Void> {
    /**
     * 协议工厂
     */
    private final ProtocolFactory protocolFactory;

    /**
     * 构造数据包解码器
     *
     * @param protocolFactory 协议工厂
     */
    public PacketDecoder(ProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    /**
     * 解码入站数据
     *
     * @param context Netty通道上下文
     * @param in 输入字节缓冲区
     * @param out 输出对象列表，解码后的数据包添加到此
     */
    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) {
        protocolFactory.getProtocol(ServerConstants.VERSION).decode(context, in, out);
    }
}
