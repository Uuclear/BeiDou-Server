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
 * GMS v83 封包编解码协议实现。
 * <p>
 * 单个 TCP 封包 on-wire 格式：
 * <pre>
 * [4 字节包头（明文）][载荷（MapleCustomEncryption + AES-OFB）]
 * </pre>
 * 载荷解密后首 2 字节为小端序 RecvOpcode/SendOpcode，后续为业务数据。
 * 包头校验失败时抛出 {@link org.gms.net.netty.InvalidPacketHeaderException}。
 * </p>
 */
public class GMSV83PacketProtocol implements PacketProtocol {
    private final MapleAESOFB receiveCypher;
    private final MapleAESOFB sendCypher;

    /**
     * @param clientCyphers 本会话的双向 AES 密码器
     */
    public GMSV83PacketProtocol(ClientCyphers clientCyphers) {
        this.receiveCypher = clientCyphers.getReceiveCypher();
        this.sendCypher = clientCyphers.getSendCypher();
    }

    /**
     * 解码单个入站封包：读包头 → 校验 → 读载荷 → AES 解密 → 自定义解密 → 包装为 InPacket。
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
        receiveCypher.crypt(packet);          // AES-OFB 解密（XOR 对称）
        MapleCustomEncryption.decryptData(packet); // 自定义混淆层解密
        out.add(new ByteBufInPacket(Unpooled.wrappedBuffer(packet)));
    }

    /**
     * 从 4 字节包头解析载荷长度（字节数组形式）。
     *
     * @param header 包头 4 字节
     * @return 载荷字节数
     */
    private static int decodePacketLength(byte[] header) {
        // 后两字节 XOR 得长度的高低位
        return (((header[1] ^ header[3]) & 0xFF) << 8) | ((header[0] ^ header[2]) & 0xFF);
    }

    /** 从整型包头解析载荷长度 */
    private int decodePacketLength(int header) {
        int length = ((header >>> 16) ^ (header & 0xFFFF));
        length = ((length << 8) & 0xFF00) | ((length >>> 8) & 0xFF); // 字节交换
        return length;
    }

    /**
     * 编码出站封包：自定义加密 → AES 加密 → 写包头 → 写载荷。
     */
    @Override
    public void encode(ChannelHandlerContext ctx, Packet in, ByteBuf out) {
        byte[] packet = in.getBytes();
        out.writeBytes(getEncodedHeader(packet.length));

        MapleCustomEncryption.encryptData(packet); // 先自定义混淆
        sendCypher.crypt(packet);                  // 再 AES-OFB
        out.writeBytes(packet);
    }

    /** 生成 4 字节明文包头 */
    private byte[] getEncodedHeader(int length) {
        return sendCypher.getPacketHeader(length);
    }

    /**
     * 发送 Hello 封包：明文，包含服务端版本号与双向 IV，客户端据此初始化加密。
     */
    @Override
    public void writeInitialUnencryptedHelloPacket(SocketChannel socketChannel, InitializationVector sendIv, InitializationVector recvIv, Client client) {
        socketChannel.writeAndFlush(Unpooled.wrappedBuffer(PacketCreator.getHello(ServerConstants.VERSION, sendIv, recvIv).getBytes()));
    }
}
