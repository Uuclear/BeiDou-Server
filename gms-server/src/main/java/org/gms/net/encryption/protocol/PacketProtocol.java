package org.gms.net.encryption.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.gms.client.Client;
import org.gms.net.encryption.InitializationVector;
import org.gms.net.packet.Packet;

import java.util.List;

/**
 * 数据包协议接口
 * 定义不同版本冒险岛协议的编解码标准
 * 支持多版本协议扩展
 *
 * @author OdinMS开发团队
 */
public interface PacketProtocol {
    /**
     * 解码入站数据
     *
     * @param context Netty通道上下文
     * @param in 输入字节缓冲区
     * @param out 输出对象列表
     */
    void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out);

    /**
     * 编码出站数据包
     *
     * @param ctx Netty通道上下文
     * @param in 输入数据包
     * @param out 输出字节缓冲区
     */
    void encode(ChannelHandlerContext ctx, Packet in, ByteBuf out);

    /**
     * 发送初始未加密的握手包（Hello包）
     * 连接建立后首先发送的数据包，包含版本信息和IV
     *
     * @param socketChannel Socket通道
     * @param sendIv 发送方向初始化向量
     * @param recvIv 接收方向初始化向量
     * @param client 客户端对象
     */
    void writeInitialUnencryptedHelloPacket(SocketChannel socketChannel, InitializationVector sendIv, InitializationVector recvIv, Client client);
}
