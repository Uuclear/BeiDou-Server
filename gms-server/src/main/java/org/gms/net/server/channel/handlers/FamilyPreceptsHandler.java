package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.Family;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

/**
 * 处理客户端 CHANGE_FAMILY_MESSAGE（更改家族消息） 封包。
 * <p>对应操作码：{@link org.gms.net.opcodes.RecvOpcode#CHANGE_FAMILY_MESSAGE}</p>
 */
public class FamilyPreceptsHandler extends AbstractPacketHandler {

    /** 处理 更改家族消息 封包的业务逻辑。 */
    @Override
    public void handlePacket(InPacket p, Client c) {
        Family family = c.getPlayer().getFamily();
        if (family == null) {
            return;
        }
        if (family.getLeader().getChr() != c.getPlayer()) {
            return; //only the leader can set the precepts
        }
        String newPrecepts = p.readString();
        if (newPrecepts.length() > 200) {
            return;
        }
        family.setMessage(newPrecepts, true);
        //family.broadcastFamilyInfoUpdate(); //probably don't need to broadcast for this?
        c.sendPacket(PacketCreator.getFamilyInfo(c.getPlayer().getFamilyEntry()));
    }

}
