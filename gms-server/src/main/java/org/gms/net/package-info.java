/**
 * 网络协议层，负责 TCP 连接、加解密、包编解码与 Handler 分发。
 * <p>
 * 数据流：Netty Channel → AES 编解码 → {@link org.gms.client.Client} →
 * {@link org.gms.net.PacketProcessor} → 具体 {@code *Handler}。
 */
package org.gms.net;
