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
 * 服务器通道初始化器抽象基类
 * 提供登录服务器和频道服务器共用的通道初始化逻辑
 * 包括：生成IV、发送Hello包、配置Pipeline处理器链等
 *
 * @author OdinMS开发团队
 */
public abstract class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger log = LoggerFactory.getLogger(ServerChannelInitializer.class);

    /**
     * 空闲超时时间（秒），30秒无数据传输则断开连接
     */
    private static final int IDLE_TIME_SECONDS = 30;

    /**
     * 发送数据包日志记录器（共享单例）
     */
    private static final ChannelHandler sendPacketLogger = new OutPacketLogger();

    /**
     * 接收数据包日志记录器（共享单例）
     */
    private static final ChannelHandler receivePacketLogger = new InPacketLogger();

    /**
     * 会话ID生成器，从7777开始自增
     */
    static final AtomicLong sessionId = new AtomicLong(7777);

    /**
     * 获取远程客户端IP地址
     *
     * @param channel Netty通道
     * @return 客户端IP地址字符串，获取失败返回"null"
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
     * 初始化通道Pipeline
     * 生成发送和接收IV，创建协议工厂，发送Hello握手包，然后设置处理器链
     *
     * @param socketChannel 客户端Socket通道
     * @param client 客户端对象
     */
    void initPipeline(SocketChannel socketChannel, Client client) {
        final InitializationVector sendIv = InitializationVector.generateSend();
        final InitializationVector recvIv = InitializationVector.generateReceive();
        final ProtocolFactory protocolFactory = new ProtocolFactory(ClientCyphers.of(sendIv, recvIv));
        protocolFactory.getProtocol(ServerConstants.VERSION).writeInitialUnencryptedHelloPacket(socketChannel, sendIv, recvIv, client);
        setUpHandlers(socketChannel.pipeline(), protocolFactory, client);
    }

    /**
     * 配置ChannelPipeline处理器链
     * 添加顺序：空闲状态检测 → 数据包编解码器 → 数据包日志记录器 → 客户端处理器
     *
     * @param pipeline Netty管道
     * @param protocolFactory 协议工厂
     * @param client 客户端对象
     */
    private void setUpHandlers(ChannelPipeline pipeline, ProtocolFactory protocolFactory, Client client) {
        pipeline.addLast("IdleStateHandler", new IdleStateHandler(0, 0, IDLE_TIME_SECONDS));
        pipeline.addLast("PacketCodec", new PacketCodec(protocolFactory));
        pipeline.addLast("Client", client);

        pipeline.addBefore("Client", "SendPacketLogger", sendPacketLogger);
        pipeline.addBefore("Client", "ReceivePacketLogger", receivePacketLogger);
    }
}
