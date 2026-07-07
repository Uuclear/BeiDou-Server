package org.gms.net.encryption;

/**
 * AES-OFB 加解密初始向量（IV），每个 TCP 会话在 Hello 握手时随机生成。
 * <p>
 * v83 协议规定 send/receive 方向使用不同 IV 前缀：
 * 发送 IV 前三字节为 ASCII "R0x"，接收 IV 前三字节为 "Frz"，
 * 第四字节为随机值。客户端收到 Hello 封包后据此初始化双向密钥流。
 * </p>
 */
public class InitializationVector {
    private final byte[] bytes;

    private InitializationVector(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return IV 的 4 字节副本（调用方不应修改返回数组）
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * 生成服务端发送方向的 IV：{@code [0x52, 0x30, 0x78, random]}（"R0x" + 随机字节）
     */
    public static InitializationVector generateSend() {
        byte[] ivSend = {82, 48, 120, getRandomByte()};
        return new InitializationVector(ivSend);
    }

    /**
     * 生成服务端接收方向的 IV：{@code [0x46, 0x72, 0x7A, random]}（"Frz" + 随机字节）
     */
    public static InitializationVector generateReceive() {
        byte[] ivRecv = {70, 114, 122, getRandomByte()};
        return new InitializationVector(ivRecv);
    }

    private static byte getRandomByte() {
        return (byte) (Math.random() * 255);
    }
}
