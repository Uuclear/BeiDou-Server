package org.gms.net.netty;

import org.gms.client.Client;
import org.gms.constants.net.ServerConstants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.gms.net.encryption.ClientCyphers;
import org.gms.net.encryption.InitializationVector;
import org.gms.net.encryption.PacketCodec;
import org.gms.net.encryption.protocol.ProtocolFactory;
import org.gms.net.packet.logging.InPacketLogger;
import org.gms.net.packet.logging.OutPacketLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Netty 子 Channel 初始化器基类，负责建立 v83 协议栈的标准 Pipeline。
 * <p>
 * 每个新 TCP 连接按以下顺序装配处理器：
 * <ol>
 *   <li>发送未加密的 Hello 封包（版本号 + sendIv + recvIv）</li>
 *   <li>{@link IdleStateHandler} — 空闲超时检测（30 秒）</li>
 *   <li>{@link PacketCodec} — AES-OFB + 自定义加解密编解码</li>
 *   <li>收/发封包日志 Handler</li>
 *   <li>{@link Client} — 业务层，根据 RecvOpcode 分发到 {@link org.gms.net.PacketProcessor}</li>
 * </ol>
 * </p>
 */
public abstract class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger log = LoggerFactory.getLogger(ServerChannelInitializer.class);
    /** 读/写空闲超时（秒），超时后触发断线逻辑 */
    private static final int IDLE_TIME_SECONDS = 30;
    private static final ChannelHandler sendPacketLogger = new OutPacketLogger();
    private static final ChannelHandler receivePacketLogger = new InPacketLogger();

    /** 全局递增的会话 ID 种子，用于标识每个客户端连接 */
    static final AtomicLong sessionId = new AtomicLong(7777);

    /**
     * 安全获取客户端远程 IP 地址。
     *
     * @param channel Netty Channel
     * @return 远程 IP 字符串，获取失败时返回 {@code "null"}
     */
    String getRemoteAddress(Channel channel) {
        String remoteAddress = "null";
        try {
            remoteAddress = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        } catch (NullPointerException npe) {
            log.warn("Unable to get remote address from netty Channel: {}", channel, npe);
        }

        return remoteAddress;
    }

    /**
     * 完成 v83 握手并装配加密 Pipeline。
     * <p>
     * 生成随机 sendIv/recvIv，通过 Hello 封包告知客户端，随后所有业务封包均经 AES-OFB 加密。
     * </p>
     *
     * @param socketChannel 新建立的 SocketChannel
     * @param client        已创建的 {@link Client} 实例
     */
    void initPipeline(SocketChannel socketChannel, Client client) {
        // sendIv 用于服务端发送方向的 AES；recvIv 用于接收客户端封包
        final InitializationVector sendIv = InitializationVector.generateSend();
        final InitializationVector recvIv = InitializationVector.generateReceive();
        final ProtocolFactory protocolFactory = new ProtocolFactory(ClientCyphers.of(sendIv, recvIv));
        // Hello 封包明文发送，内含版本号与 IV，客户端据此初始化对称密钥流
        protocolFactory.getProtocol(ServerConstants.VERSION).writeInitialUnencryptedHelloPacket(socketChannel, sendIv, recvIv, client);
        setUpHandlers(socketChannel.pipeline(), protocolFactory, client);
    }

    private void setUpHandlers(ChannelPipeline pipeline, ProtocolFactory protocolFactory, Client client) {
        pipeline.addLast("IdleStateHandler", new IdleStateHandler(0, 0, IDLE_TIME_SECONDS));
        pipeline.addLast("PacketCodec", new PacketCodec(protocolFactory));
        pipeline.addLast("Client", client);

        // 日志 Handler 插在 Client 之前，可记录加解密后的明文封包
        pipeline.addBefore("Client", "SendPacketLogger", sendPacketLogger);
        pipeline.addBefore("Client", "ReceivePacketLogger", receivePacketLogger);
    }
}
