package org.gms.net.packet.out;

import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.ByteBufOutPacket;

/**
 * 发送便笺成功结果的出站封包（MEMO_RESULT，结果码 4）。
 */
public final class SendNoteSuccessPacket extends ByteBufOutPacket {

    /** 构造发送便笺成功封包。 */
    public SendNoteSuccessPacket() {
        super(SendOpcode.MEMO_RESULT);

        writeByte(4);
    }
}
