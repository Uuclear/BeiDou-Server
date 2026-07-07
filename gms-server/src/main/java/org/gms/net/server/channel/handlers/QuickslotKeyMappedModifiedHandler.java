package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.keybind.QuickslotBinding;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;

/**
 * 处理客户端 CHANGE_QUICKSLOT（CP_QuickslotKeyMappedModified） 封包。
 * <p>对应操作码：{@link org.gms.net.opcodes.RecvOpcode#CHANGE_QUICKSLOT}</p>
 */
public class QuickslotKeyMappedModifiedHandler extends AbstractPacketHandler {
    /** 处理 CP_QuickslotKeyMappedModified 封包的业务逻辑。 */
    @Override
    public void handlePacket(InPacket p, Client c) {
        // Invalid size for the packet.
        if (p.available() != QuickslotBinding.QUICKSLOT_SIZE * Integer.BYTES ||
                // not logged in-game
                c.getPlayer() == null) {
            return;
        }

        byte[] aQuickslotKeyMapped = new byte[QuickslotBinding.QUICKSLOT_SIZE];

        for (int i = 0; i < QuickslotBinding.QUICKSLOT_SIZE; i++) {
            aQuickslotKeyMapped[i] = (byte) p.readInt();
        }

        c.getPlayer().changeQuickslotKeybinding(aQuickslotKeyMapped);
    }
}
