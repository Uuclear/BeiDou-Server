package org.gms.net.netty;

import org.gms.client.Client;
import io.netty.channel.socket.SocketChannel;
import org.gms.net.PacketProcessor;
import org.gms.net.server.coordinator.session.SessionCoordinator;
import org.gms.util.I18nUtil;
import org.gms.util.RateLimitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登录服务器通道初始化器
 * 当新的客户端连接到登录服务器时，负责初始化通道和客户端对象
 * 执行速率限制检查、登录会话检查等
 *
 * @author OdinMS开发团队
 */
public class LoginServerInitializer extends ServerChannelInitializer {
    private static final Logger log = LoggerFactory.getLogger(LoginServerInitializer.class);

    /**
     * 初始化新连接的Socket通道
     * 流程：记录IP → 获取数据包处理器 → 速率限制检查 → 创建登录客户端 → 会话检查 → 初始化Pipeline
     *
     * @param socketChannel 客户端Socket通道
     */
    @Override
    public void initChannel(SocketChannel socketChannel) {
        final String clientIp = socketChannel.remoteAddress().getHostString();
        log.info(I18nUtil.getLogMessage("LoginServerInitializer.initChannel.info1"), clientIp);

        PacketProcessor packetProcessor = PacketProcessor.getLoginServerProcessor();
        final long clientSessionId = sessionId.getAndIncrement();
        final String remoteAddress = getRemoteAddress(socketChannel);
        if (!RateLimitUtil.getInstance().check(remoteAddress)) {
            log.warn(I18nUtil.getLogMessage("LoginServerInitializer.initChannel.warn1"), remoteAddress);
            socketChannel.close();
        }
        final Client client = Client.createLoginClient(clientSessionId, remoteAddress, packetProcessor, LoginServer.WORLD_ID, LoginServer.CHANNEL_ID);

        if (!SessionCoordinator.getInstance().canStartLoginSession(client)) {
            socketChannel.close();
            return;
        }

        initPipeline(socketChannel, client);
    }
}
