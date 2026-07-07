package org.gms.net.netty;

/**
 * 封包头无效时抛出的运行时异常，携带解析到的异常头值。
 */
public class InvalidPacketHeaderException extends RuntimeException {
    private final int header;

    /**
     * 构造异常并记录无效封包头。
     *
     * @param message 错误描述
     * @param header  解析到的封包头值
     */
    public InvalidPacketHeaderException(String message, int header) {
        super(message);
        this.header = header;
    }

    /**
     * 返回无效封包头值。
     *
     * @return 封包头整型值
     */
    public int getHeader() {
        return header;
    }
}
