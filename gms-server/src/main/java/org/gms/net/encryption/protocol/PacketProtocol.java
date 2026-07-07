package org.gms.net.encryption.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.gms.client.Client;
import org.gms.net.encryption.InitializationVector;
import org.gms.net.packet.Packet;

import java.util.List;

/**
 * 封包编解码协议接口，抽象不同 MapleStory 版本的加解密与握手行为。
 * <p>
 * v83 实现为 {@link GMSV83PacketProtocol}，由 {@link ProtocolFactory} 按版本号选择。
 * </p>
 */
public interface PacketProtocol {
    /**
     * 将入站 ByteBuf 解码为一个或多个 {@link org.gms.net.packet.InPacket}。
     */
    void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out);

    /**
     * 将出站 {@link Packet} 编码写入 ByteBuf（含包头与加密）。
     */
    void encode(ChannelHandlerContext ctx, Packet in, ByteBuf out);

    /**
     * 向新连接发送未加密的 Hello 封包（版本号 + IV），完成握手第一步。
     */
    void writeInitialUnencryptedHelloPacket(SocketChannel socketChannel, InitializationVector sendIv, InitializationVector recvIv, Client client);
}
