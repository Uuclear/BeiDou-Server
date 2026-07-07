package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.constants.id.NpcId;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.server.minigame.RockPaperScissor;
import org.gms.util.PacketCreator;

/**
 * 处理客户端 RPS_ACTION（石头剪刀布操作） 封包。
 * <p>对应操作码：{@link org.gms.net.opcodes.RecvOpcode#RPS_ACTION}</p>
 */
public final class RPSActionHandler extends AbstractPacketHandler {

    /** 处理 石头剪刀布操作 封包的业务逻辑。 */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();
        RockPaperScissor rps = chr.getRps();

        if (c.tryacquireClient()) {
            try {
                if (p.available() == 0 || !chr.getMap().containsNPC(NpcId.RPS_ADMIN)) {
                    if (rps != null) {
                        rps.dispose(c);
                    }
                    return;
                }
                final byte mode = p.readByte();
                switch (mode) {
                    case 0: // start game
                    case 5: // retry
                        if (rps != null) {
                            rps.reward(c);
                        }
                        if (chr.getMeso() >= 1000) {
                            chr.setRPS(new RockPaperScissor(c, mode));
                        } else {
                            c.sendPacket(PacketCreator.rpsMesoError(-1));
                        }
                        break;
                    case 1: // answer
                        if (rps == null || !rps.answer(c, p.readByte())) {
                            c.sendPacket(PacketCreator.rpsMode((byte) 0x0D));// 13
                        }
                        break;
                    case 2: // time over
                        if (rps == null || !rps.timeOut(c)) {
                            c.sendPacket(PacketCreator.rpsMode((byte) 0x0D));
                        }
                        break;
                    case 3: // continue
                        if (rps == null || !rps.nextRound(c)) {
                            c.sendPacket(PacketCreator.rpsMode((byte) 0x0D));
                        }
                        break;
                    case 4: // leave
                        if (rps != null) {
                            rps.dispose(c);
                        } else {
                            c.sendPacket(PacketCreator.rpsMode((byte) 0x0D));
                        }
                        break;
                }
            } finally {
                c.releaseClient();
            }
        }
    }
}
