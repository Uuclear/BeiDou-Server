package org.gms.net.netty;

/**
 * 服务器抽象基类
 * 定义Netty服务器的基本属性和启动/停止接口
 * 登录服务器和频道服务器都继承此类
 *
 * @author OdinMS开发团队
 */
public abstract class AbstractServer {
    /**
     * 服务器监听端口
     */
    final int port;

    /**
     * 构造服务器
     *
     * @param port 监听端口号
     */
    AbstractServer(int port) {
        this.port = port;
    }

    /**
     * 启动服务器
     */
    public abstract void start();

    /**
     * 停止服务器
     */
    public abstract void stop();
}
