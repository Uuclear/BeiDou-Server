package org.gms.net.packet.out;

import org.gms.dao.entity.NotesDO;
import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.ByteBufOutPacket;
import org.gms.util.PacketCreator;

import java.util.List;
import java.util.Objects;

/**
 * 显示纸条列表数据包
 * 服务器发送给客户端，显示玩家收到的纸条列表
 *
 * @author OdinMS开发团队
 */
public final class ShowNotesPacket extends ByteBufOutPacket {

    /**
     * 构造显示纸条列表数据包
     * 操作码：MEMO_RESULT，值为3表示显示纸条列表
     *
     * @param notes 纸条列表
     */
    public ShowNotesPacket(List<NotesDO> notes) {
        super(SendOpcode.MEMO_RESULT);
        Objects.requireNonNull(notes);

        writeByte(3);
        writeByte(notes.size());
        notes.forEach(this::writeNote);
    }

    /**
     * 写入单个纸条数据
     *
     * @param note 纸条数据对象
     */
    private void writeNote(NotesDO note) {
        writeInt(note.getId());
        writeString(note.getFrom() + " ");
        writeString(note.getMessage());
        writeLong(PacketCreator.getTime(note.getTimestamp()));
        writeByte(note.getFame());
    }
}
