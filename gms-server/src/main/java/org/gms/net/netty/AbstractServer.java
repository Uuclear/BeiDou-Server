package org.gms.net.netty;

/**
 * Netty 服务端抽象基类，定义监听端口与生命周期方法。
 */
public abstract class AbstractServer {
    final int port;

    AbstractServer(int port) {
        this.port = port;
    }

    /** 启动 TCP 监听 */
    public abstract void start();
    /** 停止 TCP 监听 */
    public abstract void stop();
}
