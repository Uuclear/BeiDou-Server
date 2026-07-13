package org.gms.net.encryption.protocol;

import org.gms.net.encryption.ClientCyphers;

import java.util.HashMap;
import java.util.Map;

/**
 * 协议工厂类
 * 根据客户端版本号创建对应的协议处理器实例
 * 使用简单工厂模式管理协议版本
 *
 * @author OdinMS开发团队
 */
public class ProtocolFactory {
    /**
     * 协议映射表，键为版本号，值为对应协议处理器
     */
    private final Map<Short, PacketProtocol> PROTOCOLS = new HashMap<>();

    /**
     * 构造协议工厂
     * 注册所有支持的版本协议处理器
     *
     * @param clientCyphers 客户端加密器对
     */
    public ProtocolFactory(ClientCyphers clientCyphers){
        PROTOCOLS.put(ProtocolConstants.GMS_V83, new GMSV83PacketProtocol(clientCyphers));
    }

    /**
     * 根据版本号获取协议处理器
     *
     * @param version 客户端版本号
     * @return 对应版本的协议处理器
     * @throws UnsupportedOperationException 如果版本不支持则抛出异常
     */
    public PacketProtocol getProtocol(short version) {
        PacketProtocol protocol = PROTOCOLS.get(version);

        if (protocol == null) {
            throw new UnsupportedOperationException("PacketProtocol is a unsupported version: " + version);
        }

        return protocol;
    }
}
