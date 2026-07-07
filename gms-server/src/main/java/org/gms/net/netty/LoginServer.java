package org.gms.net.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 登录服 Netty TCP 监听器，处理客户端首次连接与账号/角色选择流程。
 * <p>
 * 在 v83 协议栈中，登录服是客户端连接的第一跳：完成 Hello 握手、AES 密钥交换后，
 * 使用 {@link org.gms.net.PacketProcessor#getLoginServerProcessor()} 分发登录相关 RecvOpcode。
 * 角色选定后，客户端会断开并连接对应世界的频道服（{@link ChannelServer}）。
 * </p>
 */
public class LoginServer extends AbstractServer {
    /** 登录服在 PacketProcessor 中使用的虚拟世界编号 */
    public static final int WORLD_ID = -1;
    /** 登录服在 PacketProcessor 中使用的虚拟频道编号 */
    public static final int CHANNEL_ID = -1;
    private Channel channel;

    /**
     * @param port 登录服监听端口（通常来自配置 login.port）
     */
    public LoginServer(int port) {
        super(port);
    }

    /**
     * 绑定端口并启动 NIO 服务端，每个新连接由 {@link LoginServerInitializer} 初始化 Pipeline。
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
     * 关闭监听 Channel，停止接受新连接。
     *
     * @throws IllegalStateException 若尚未调用 {@link #start()}
     */
    @Override
    public void stop() {
        if (channel == null) {
            throw new IllegalStateException("Must start LoginServer before stopping it");
        }

        channel.close().syncUninterruptibly();
    }
}
