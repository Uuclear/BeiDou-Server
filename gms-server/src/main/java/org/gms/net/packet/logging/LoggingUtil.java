package org.gms.net.packet.logging;

import io.netty.buffer.Unpooled;
import org.gms.net.opcodes.RecvOpcode;

import java.util.Set;

/**
 * 封包日志工具类，提供操作码读取与高频封包过滤判断。
 */
public class LoggingUtil {
    private static final Set<Short> ignoredDebugRecvPackets = Set.of(
            (short) RecvOpcode.MOVE_PLAYER.getValue(), // 41
            (short) RecvOpcode.HEAL_OVER_TIME.getValue(), // 89
            (short) RecvOpcode.SPECIAL_MOVE.getValue(), // 91
            (short) RecvOpcode.QUEST_ACTION.getValue(), // 107
            (short) RecvOpcode.MOVE_PET.getValue(), // 167
            (short) RecvOpcode.MOVE_LIFE.getValue(), // 188
            (short) RecvOpcode.NPC_ACTION.getValue() // 197
    );

    /**
     * 从封包字节数组中读取前 2 字节小端 short 值（通常为操作码）。
     *
     * @param bytes 封包原始字节
     * @return 前 2 字节解析结果
     */
    public static short readFirstShort(byte[] bytes) {
        return Unpooled.wrappedBuffer(bytes).readShortLE();
    }

    /**
     * 判断该接收操作码是否应忽略调试日志（如移动类高频封包）。
     *
     * @param opcode 接收操作码
     * @return 应忽略返回 true
     */
    public static boolean isIgnoredRecvPacket(short opcode) {
        return ignoredDebugRecvPackets.contains(opcode);
    }
}
