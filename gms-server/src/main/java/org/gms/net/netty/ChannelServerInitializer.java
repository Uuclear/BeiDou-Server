package org.gms.net.netty;

import org.gms.client.Client;
import io.netty.channel.socket.SocketChannel;
import org.gms.net.PacketProcessor;
import org.gms.net.server.Server;
import org.gms.net.server.coordinator.session.SessionCoordinator;
import org.gms.util.I18nUtil;
import org.gms.util.RateLimitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 频道服务器通道初始化器
 * 当新的客户端连接到频道服务器时，负责初始化通道和客户端对象
 * 执行速率限制检查、频道可用性检查等
 *
 * @author OdinMS开发团队
 */
public class ChannelServerInitializer extends ServerChannelInitializer {
    private static final Logger log = LoggerFactory.getLogger(ChannelServerInitializer.class);

    /**
     * 世界ID
     */
    private final int world;

    /**
     * 频道ID
     */
    private final int channel;

    /**
     * 构造频道服务器初始化器
     *
     * @param world 世界ID
     * @param channel 频道ID
     */
    public ChannelServerInitializer(int world, int channel) {
        this.world = world;
        this.channel = channel;
    }

    /**
     * 初始化新连接的Socket通道
     * 流程：记录IP → 获取数据包处理器 → 速率限制检查 → 创建客户端 → 检查频道是否存在 → 初始化Pipeline
     *
     * @param socketChannel 客户端Socket通道
     */
    @Override
    public void initChannel(SocketChannel socketChannel) {
        final String clientIp = socketChannel.remoteAddress().getHostString();
        log.info(I18nUtil.getLogMessage("ChannelServerInitializer.initChannel.info1"), clientIp,world,channel);

        PacketProcessor packetProcessor = PacketProcessor.getChannelServerProcessor(world, channel);
        final long clientSessionId = sessionId.getAndIncrement();
        final String remoteAddress = getRemoteAddress(socketChannel);
        if (!RateLimitUtil.getInstance().check(remoteAddress)) {
            log.warn(I18nUtil.getLogMessage("LoginServerInitializer.initChannel.warn1"), remoteAddress);
            socketChannel.close();
        }
        final Client client = Client.createChannelClient(clientSessionId, remoteAddress, packetProcessor, world, channel);

        if (Server.getInstance().getChannel(world, channel) == null) {
            SessionCoordinator.getInstance().closeSession(client, true);
            socketChannel.close();
            return;
        }

        initPipeline(socketChannel, client);
    }
}
