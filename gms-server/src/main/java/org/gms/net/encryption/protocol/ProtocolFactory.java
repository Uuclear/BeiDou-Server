package org.gms.net.encryption.protocol;

import org.gms.net.encryption.ClientCyphers;

import java.util.HashMap;
import java.util.Map;

/**
 * 封包协议工厂，按 MapleStory 客户端版本号选择对应的 {@link PacketProtocol} 实现。
 * <p>
 * 每个 TCP 会话在握手时创建一个工厂实例，绑定该会话的 {@link ClientCyphers}。
 * </p>
 */
public class ProtocolFactory {
    private final Map<Short, PacketProtocol> PROTOCOLS = new HashMap<>();

    /**
     * @param clientCyphers 本会话双向 AES 密码器，用于构造版本对应的协议实现
     */
    public ProtocolFactory(ClientCyphers clientCyphers){
        // 在这里注册版本与对应的处理器
        PROTOCOLS.put(ProtocolConstants.GMS_V83, new GMSV83PacketProtocol(clientCyphers));
    }

    /**
     * @param version 客户端版本号（如 83）
     * @return 对应版本的协议实现
     * @throws UnsupportedOperationException 未注册的版本
     */
    public PacketProtocol getProtocol(short version) {
        PacketProtocol protocol = PROTOCOLS.get(version);

        if (protocol == null) {
            throw new UnsupportedOperationException("PacketProtocol is a unsupported version: " + version);
        }

        return protocol;
    }
}
