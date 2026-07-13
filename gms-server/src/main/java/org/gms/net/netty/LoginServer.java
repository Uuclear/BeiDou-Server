package org.gms.net.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 登录服务器
 * 基于Netty实现的登录服务器，负责处理用户登录、角色选择等登录阶段的网络通信
 * 登录服务器使用固定的世界ID(-1)和频道ID(-1)
 *
 * @author OdinMS开发团队
 */
public class LoginServer extends AbstractServer {
    /**
     * 登录服务器的世界ID标识（-1表示登录服务器）
     */
    public static final int WORLD_ID = -1;

    /**
     * 登录服务器的频道ID标识（-1表示登录服务器）
     */
    public static final int CHANNEL_ID = -1;

    /**
     * Netty通道实例
     */
    private Channel channel;

    /**
     * 构造登录服务器
     *
     * @param port 监听端口
     */
    public LoginServer(int port) {
        super(port);
    }

    /**
     * 启动登录服务器
     * 创建NIO事件循环组，配置ServerBootstrap并绑定端口
     */
    @Override
    public void start() {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new LoginServerInitializer());

        this.channel = bootstrap.bind(port).syncUninterruptibly().channel();
    }

    /**
     * 停止登录服务器
     * 关闭Netty通道
     *
     * @throws IllegalStateException 如果服务器未启动则抛出异常
     */
    @Override
    public void stop() {
        if (channel == null) {
            throw new IllegalStateException("Must start LoginServer before stopping it");
        }

        channel.close().syncUninterruptibly();
    }
}
