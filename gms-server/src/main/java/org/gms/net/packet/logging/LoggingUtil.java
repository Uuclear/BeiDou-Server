package org.gms.net.packet.logging;

import io.netty.buffer.Unpooled;
import org.gms.net.opcodes.RecvOpcode;

import java.util.Set;

/**
 * 数据包日志工具类
 * 提供日志相关的辅助方法，定义需要忽略日志的高频操作码
 *
 * @author OdinMS开发团队
 */
public class LoggingUtil {
    /**
     * 调试时忽略记录的接收数据包集合
     * 这些高频数据包（如移动、治疗等）在调试时会产生大量日志，默认忽略
     */
    private static final Set<Short> ignoredDebugRecvPackets = Set.of(
            (short) RecvOpcode.MOVE_PLAYER.getValue(),
            (short) RecvOpcode.HEAL_OVER_TIME.getValue(),
            (short) RecvOpcode.SPECIAL_MOVE.getValue(),
            (short) RecvOpcode.QUEST_ACTION.getValue(),
            (short) RecvOpcode.MOVE_PET.getValue(),
            (short) RecvOpcode.MOVE_LIFE.getValue(),
            (short) RecvOpcode.NPC_ACTION.getValue()
    );

    /**
     * 从字节数组中读取第一个短整数（小端序，即数据包操作码）
     *
     * @param bytes 数据包字节数组
     * @return 第一个短整数（操作码）
     */
    public static short readFirstShort(byte[] bytes) {
        return Unpooled.wrappedBuffer(bytes).readShortLE();
    }

    /**
     * 检查指定操作码是否在忽略日志列表中
     *
     * @param opcode 操作码值
     * @return 如果需要忽略返回true，否则返回false
     */
    public static boolean isIgnoredRecvPacket(short opcode) {
        return ignoredDebugRecvPackets.contains(opcode);
    }
}
