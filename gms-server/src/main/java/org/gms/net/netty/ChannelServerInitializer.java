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
 * 频道服子 Channel 初始化器。
 * <p>
 * 按 world/channel 创建 {@link Client#createChannelClient}，校验目标频道是否在线后装配 Pipeline。
 * </p>
 */
public class ChannelServerInitializer extends ServerChannelInitializer {
    private static final Logger log = LoggerFactory.getLogger(ChannelServerInitializer.class);

    private final int world;
    private final int channel;

    /**
     * @param world   所属世界编号
     * @param channel 频道编号
     */
    public ChannelServerInitializer(int world, int channel) {
        this.world = world;
        this.channel = channel;
    }

    /**
     * 初始化频道服客户端连接：限流 → 频道有效性检查 → Hello 握手 → Pipeline 装配。
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
