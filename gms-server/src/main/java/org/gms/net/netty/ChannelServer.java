package org.gms.net.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 频道服务器
 * 基于Netty实现的游戏频道服务器，负责处理游戏内的所有网络通信
 * 每个世界可以有多个频道，每个频道运行在独立的端口上
 *
 * @author OdinMS开发团队
 */
public class ChannelServer extends AbstractServer {
    /**
     * 世界ID
     */
    private final int world;

    /**
     * 频道ID
     */
    private final int channel;

    /**
     * Netty通道实例
     */
    private Channel nettyChannel;

    /**
     * 构造频道服务器
     *
     * @param port 监听端口
     * @param world 世界ID
     * @param channel 频道ID
     */
    public ChannelServer(int port, int world, int channel) {
        super(port);
        this.world = world;
        this.channel = channel;
    }

    /**
     * 启动频道服务器
     * 创建NIO事件循环组，配置ServerBootstrap并绑定端口
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
     * 停止频道服务器
     * 关闭Netty通道
     *
     * @throws IllegalStateException 如果服务器未启动则抛出异常
     */
    @Override
    public void stop() {
        if (nettyChannel == null) {
            throw new IllegalStateException("Must start ChannelServer before stopping it");
        }

        nettyChannel.close().syncUninterruptibly();
    }
}
