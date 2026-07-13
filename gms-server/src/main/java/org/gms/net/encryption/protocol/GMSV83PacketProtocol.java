package org.gms.net.encryption.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.gms.client.Client;
import org.gms.constants.net.ServerConstants;
import org.gms.net.encryption.ClientCyphers;
import org.gms.net.encryption.InitializationVector;
import org.gms.net.encryption.MapleAESOFB;
import org.gms.net.encryption.MapleCustomEncryption;
import org.gms.net.netty.InvalidPacketHeaderException;
import org.gms.net.packet.ByteBufInPacket;
import org.gms.net.packet.Packet;
import org.gms.util.PacketCreator;

import java.util.List;

/**
 * GMS v83版本数据包协议实现
 * 处理冒险岛Global MapleStory v83版本的数据包编解码
 * 包含AES解密、自定义加密解密、数据包头部验证等逻辑
 *
 * @author OdinMS开发团队
 */
public class GMSV83PacketProtocol implements PacketProtocol {
    /**
     * 接收方向加密器
     */
    private final MapleAESOFB receiveCypher;

    /**
     * 发送方向加密器
     */
    private final MapleAESOFB sendCypher;

    /**
     * 构造GMS v83协议处理器
     *
     * @param clientCyphers 客户端加密器对
     */
    public GMSV83PacketProtocol(ClientCyphers clientCyphers) {
        this.receiveCypher = clientCyphers.getReceiveCypher();
        this.sendCypher = clientCyphers.getSendCypher();
    }

    /**
     * 解码入站数据包
     * 流程：读取4字节头部 → 验证头部 → 解析长度 → 读取数据 → AES解密 → 自定义解密
     *
     * @param context Netty通道上下文
     * @param in 输入字节缓冲区
     * @param out 输出对象列表
     * @throws InvalidPacketHeaderException 如果数据包头部无效
     */
    @Override
    public void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) {
        final int header = in.readInt();

        if (!receiveCypher.isValidHeader(header)) {
            throw new InvalidPacketHeaderException("Attempted to decode a packet with an invalid header", header);
        }

        final int packetLength = decodePacketLength(header);
        byte[] packet = new byte[packetLength];
        in.readBytes(packet);
        receiveCypher.crypt(packet);
        MapleCustomEncryption.decryptData(packet);
        out.add(new ByteBufInPacket(Unpooled.wrappedBuffer(packet)));
    }

    /**
     * 从字节数组形式的头部解析数据包长度
     *
     * @param header 4字节头部数组
     * @return 数据包长度
     */
    private static int decodePacketLength(byte[] header) {
        return (((header[1] ^ header[3]) & 0xFF) << 8) | ((header[0] ^ header[2]) & 0xFF);
    }

    /**
     * 从int形式的头部解析数据包长度
     *
     * @param header 4字节头部（int形式）
     * @return 数据包长度
     */
    private int decodePacketLength(int header) {
        int length = ((header >>> 16) ^ (header & 0xFFFF));
        length = ((length << 8) & 0xFF00) | ((length >>> 8) & 0xFF);
        return length;
    }

    /**
     * 编码出站数据包
     * 流程：写入加密头部 → 自定义加密 → AES加密 → 写入数据
     *
     * @param ctx Netty通道上下文
     * @param in 输入数据包
     * @param out 输出字节缓冲区
     */
    @Override
    public void encode(ChannelHandlerContext ctx, Packet in, ByteBuf out) {
        byte[] packet = in.getBytes();
        out.writeBytes(getEncodedHeader(packet.length));

        MapleCustomEncryption.encryptData(packet);
        sendCypher.crypt(packet);
        out.writeBytes(packet);
    }

    /**
     * 获取编码后的数据包头部
     *
     * @param length 数据包长度
     * @return 4字节加密头部
     */
    private byte[] getEncodedHeader(int length) {
        return sendCypher.getPacketHeader(length);
    }

    /**
     * 发送初始未加密的Hello握手包
     * 连接建立后第一个数据包，包含版本号、发送IV和接收IV
     *
     * @param socketChannel Socket通道
     * @param sendIv 发送方向IV
     * @param recvIv 接收方向IV
     * @param client 客户端对象
     */
    @Override
    public void writeInitialUnencryptedHelloPacket(SocketChannel socketChannel, InitializationVector sendIv, InitializationVector recvIv, Client client) {
        socketChannel.writeAndFlush(Unpooled.wrappedBuffer(PacketCreator.getHello(ServerConstants.VERSION, sendIv, recvIv).getBytes()));
    }
}
