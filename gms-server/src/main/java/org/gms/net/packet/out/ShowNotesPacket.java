package org.gms.net.packet.out;

import org.gms.dao.entity.NotesDO;
import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.ByteBufOutPacket;
import org.gms.util.PacketCreator;

import java.util.List;
import java.util.Objects;

/**
 * 展示玩家便笺列表的出站封包（MEMO_RESULT，结果码 3）。
 */
public final class ShowNotesPacket extends ByteBufOutPacket {

    /**
     * 构造便笺列表封包。
     *
     * @param notes 便笺数据列表，不可为 null
     */
    public ShowNotesPacket(List<NotesDO> notes) {
        super(SendOpcode.MEMO_RESULT);
        Objects.requireNonNull(notes);

        writeByte(3);
        writeByte(notes.size());
        notes.forEach(this::writeNote);
    }

    private void writeNote(NotesDO note) {
        writeInt(note.getId());
        writeString(note.getFrom() + " "); //Stupid nexon forgot space lol
        writeString(note.getMessage());
        writeLong(PacketCreator.getTime(note.getTimestamp()));
        writeByte(note.getFame());
    }
}
