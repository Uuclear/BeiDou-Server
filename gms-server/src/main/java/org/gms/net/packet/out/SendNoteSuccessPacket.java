package org.gms.net.packet.out;

import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.ByteBufOutPacket;

/**
 * 发送纸条成功数据包
 * 服务器发送给客户端，表示纸条发送成功
 *
 * @author OdinMS开发团队
 */
public final class SendNoteSuccessPacket extends ByteBufOutPacket {

    /**
     * 构造发送纸条成功数据包
     * 操作码：MEMO_RESULT，值为4表示发送成功
     */
    public SendNoteSuccessPacket() {
        super(SendOpcode.MEMO_RESULT);

        writeByte(4);
    }
}
