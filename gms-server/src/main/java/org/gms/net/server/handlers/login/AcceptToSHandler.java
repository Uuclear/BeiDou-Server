package org.gms.net.server.handlers.login;

import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

/**
 * 处理客户端 ACCEPT_TOS（接受服务条款） 封包。
 * <p>对应操作码：{@link org.gms.net.opcodes.RecvOpcode#ACCEPT_TOS}</p>
 */
public final class AcceptToSHandler extends AbstractPacketHandler {

    /** 仅在客户端尚未完成登录时处理该封包。 */
    @Override
    public boolean validateState(Client c) {
        return !c.isLoggedIn();
    }

    /** 处理 接受服务条款 封包的业务逻辑。 */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        if (p.available() == 0 || p.readByte() != 1 || c.acceptToS()) {
            c.disconnect(false, false);//Client dc's but just because I am cool I do this (:
            return;
        }
        if (c.finishLogin() == 0) {
            c.sendPacket(PacketCreator.getAuthSuccess(c));
        } else {
            c.sendPacket(PacketCreator.getLoginFailed(9));//shouldn't happen XD
        }
    }
}
