package org.gms.net.encryption;

import org.gms.constants.net.ServerConstants;

/**
 * 客户端加密器对
 * 封装发送和接收两个方向的AES加密器，用于管理客户端连接的加解密状态
 *
 * @author OdinMS开发团队
 */
public class ClientCyphers {
    /**
     * 发送方向加密器（服务器→客户端）
     */
    private final MapleAESOFB send;

    /**
     * 接收方向加密器（客户端→服务器）
     */
    private final MapleAESOFB receive;

    /**
     * 私有构造函数
     *
     * @param send 发送加密器
     * @param receive 接收加密器
     */
    private ClientCyphers(MapleAESOFB send, MapleAESOFB receive) {
        this.send = send;
        this.receive = receive;
    }

    /**
     * 根据初始化向量创建客户端加密器对
     *
     * @param sendIv 发送方向初始化向量
     * @param receiveIv 接收方向初始化向量
     * @return 客户端加密器对实例
     */
    public static ClientCyphers of(InitializationVector sendIv, InitializationVector receiveIv) {
        MapleAESOFB send = new MapleAESOFB(sendIv, (short) (0xFFFF - ServerConstants.VERSION));
        MapleAESOFB receive = new MapleAESOFB(receiveIv, ServerConstants.VERSION);
        return new ClientCyphers(send, receive);
    }

    /**
     * 获取发送方向加密器
     *
     * @return 发送加密器
     */
    public MapleAESOFB getSendCypher() {
        return send;
    }

    /**
     * 获取接收方向加密器
     *
     * @return 接收加密器
     */
    public MapleAESOFB getReceiveCypher() {
        return receive;
    }
}
