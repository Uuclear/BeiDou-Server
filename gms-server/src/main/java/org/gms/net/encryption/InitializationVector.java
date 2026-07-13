package org.gms.net.encryption;

/**
 * 初始化向量（IV）类
 * 用于AES加密的初始化向量，在连接握手阶段生成并发送给客户端
 * 发送和接收使用不同的固定前缀字节以区分方向
 *
 * @author OdinMS开发团队
 */
public class InitializationVector {
    /**
     * 初始化向量字节数组（4字节）
     */
    private final byte[] bytes;

    /**
     * 私有构造函数
     *
     * @param bytes 4字节的初始化向量数据
     */
    private InitializationVector(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * 获取初始化向量的字节数组
     *
     * @return 初始化向量字节数组
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * 生成发送方向的初始化向量
     * 使用固定前缀 {82, 48, 120} 加上一个随机字节
     *
     * @return 发送方向的初始化向量
     */
    public static InitializationVector generateSend() {
        byte[] ivSend = {82, 48, 120, getRandomByte()};
        return new InitializationVector(ivSend);
    }

    /**
     * 生成接收方向的初始化向量
     * 使用固定前缀 {70, 114, 122} 加上一个随机字节
     *
     * @return 接收方向的初始化向量
     */
    public static InitializationVector generateReceive() {
        byte[] ivRecv = {70, 114, 122, getRandomByte()};
        return new InitializationVector(ivRecv);
    }

    /**
     * 生成随机字节（0-255）
     *
     * @return 随机字节
     */
    private static byte getRandomByte() {
        return (byte) (Math.random() * 255);
    }
}
