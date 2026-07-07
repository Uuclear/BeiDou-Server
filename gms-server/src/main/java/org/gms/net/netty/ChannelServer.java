package org.gms.net.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 频道服 Netty TCP 监听器，处理已进入游戏的客户端连接。
 * <p>
 * 每个世界下的每个频道对应一个 {@code ChannelServer} 实例，监听独立端口。
 * 客户端从登录服获得频道 IP/端口后连接此处，经 {@link ChannelServerInitializer} 建立
 * 加密 Pipeline，并使用 {@link org.gms.net.PacketProcessor#getChannelServerProcessor(int, int)}
 * 分发游戏内 RecvOpcode（移动、攻击、聊天等）。
 * </p>
 */
public class ChannelServer extends AbstractServer {
    private final int world;
    private final int channel;
    private Channel nettyChannel;

    /**
     * @param port    频道监听端口
     * @param world   所属世界编号
     * @param channel 频道编号（从 1 起）
     */
    public ChannelServer(int port, int world, int channel) {
        super(port);
        this.world = world;
        this.channel = channel;
    }

    /**
     * 绑定端口并启动 NIO 服务端，子 Channel 由 {@link ChannelServerInitializer} 按 world/channel 初始化。
     */
    @Override
    public void start() {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelServerInitializer(world, channel));

        this.nettyChannel = bootstrap.bind(port).syncUninterruptibly().channel();
    }

    /**
     * 关闭监听 Channel。
     *
     * @throws IllegalStateException 若尚未调用 {@link #start()}
     */
    @Override
    public void stop() {
        if (nettyChannel == null) {
            throw new IllegalStateException("Must start ChannelServer before stopping it");
        }

        nettyChannel.close().syncUninterruptibly();
    }
}
