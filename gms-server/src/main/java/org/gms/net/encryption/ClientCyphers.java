package org.gms.net.encryption;

import org.gms.constants.net.ServerConstants;

/**
 * 单个客户端会话的双向 AES-OFB 密码器容器。
 * <p>
 * 发送与接收方向使用不同的 IV 和版本常量：
 * 发送 cipher 的版本为 {@code 0xFFFF - VERSION}，接收 cipher 的版本为 {@code VERSION}，
 * 与 MapleStory 客户端约定一致。
 * </p>
 */
public class ClientCyphers {
    private final MapleAESOFB send;
    private final MapleAESOFB receive;

    private ClientCyphers(MapleAESOFB send, MapleAESOFB receive) {
        this.send = send;
        this.receive = receive;
    }

    /**
     * 根据 Hello 握手生成的 IV 创建双向密码器。
     *
     * @param sendIv   服务端发送方向 IV
     * @param receiveIv 服务端接收方向 IV
     * @return 封装好的双向密码器
     */
    public static ClientCyphers of(InitializationVector sendIv, InitializationVector receiveIv) {
        // 发送方向版本取反，与客户端 recv 方向对应
        MapleAESOFB send = new MapleAESOFB(sendIv, (short) (0xFFFF - ServerConstants.VERSION));
        MapleAESOFB receive = new MapleAESOFB(receiveIv, ServerConstants.VERSION);
        return new ClientCyphers(send, receive);
    }

    /** @return 服务端发送（客户端接收）方向的 AES 密码器 */
    public MapleAESOFB getSendCypher() {
        return send;
    }

    /** @return 服务端接收（客户端发送）方向的 AES 密码器 */
    public MapleAESOFB getReceiveCypher() {
        return receive;
    }
}
