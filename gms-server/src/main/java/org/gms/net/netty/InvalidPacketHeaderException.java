package org.gms.net.netty;

/**
 * 无效数据包头部异常
 * 当接收到的数据包头部验证失败时抛出此异常
 * 通常表示数据包损坏、加密密钥不匹配或非法连接
 *
 * @author OdinMS开发团队
 */
public class InvalidPacketHeaderException extends RuntimeException {
    /**
     * 导致异常的数据包头部值
     */
    private final int header;

    /**
     * 构造无效头部异常
     *
     * @param message 异常消息
     * @param header 无效的头部值
     */
    public InvalidPacketHeaderException(String message, int header) {
        super(message);
        this.header = header;
    }

    /**
     * 获取无效的头部值
     *
     * @return 头部整数值
     */
    public int getHeader() {
        return header;
    }
}
