package org.gms.net.packet.logging;

import org.gms.config.GameConfig;
import org.gms.constants.net.OpcodeConstants;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.gms.net.packet.OutPacket;
import org.gms.net.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.HexTool;

/**
 * 出站数据包日志记录器
 * Netty出站处理器，用于记录服务器发送给客户端的数据包
 * 可共享单例，在调试模式下输出数据包的操作码、十六进制内容和文本内容
 *
 * @author OdinMS开发团队
 */
@Sharable
public class OutPacketLogger extends ChannelOutboundHandlerAdapter implements PacketLogger {
    private static final Logger log = LoggerFactory.getLogger(OutPacketLogger.class);

    /**
     * 日志内容阈值，超过此大小的数据包只记录头部
     */
    private static final int LOG_CONTENT_THRESHOLD = 50_000;

    /**
     * 处理出站消息写入
     * 如果开启了调试模式且消息是OutPacket，则记录日志
     *
     * @param ctx Netty通道上下文
     * @param msg 出站消息
     * @param promise 通道Promise
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (GameConfig.getServerBoolean("use_debug_show_packet") && msg instanceof OutPacket packet) {
            log(packet);
        }

        ctx.write(msg);
    }

    /**
     * 记录出站数据包
     * 输出操作码名称、十六进制值、数据包长度、十六进制内容和文本内容
     *
     * @param packet 要记录的数据包
     */
    @Override
    public void log(Packet packet) {
        final byte[] content = packet.getBytes();
        final int packetLength = content.length;

        if (packetLength <= LOG_CONTENT_THRESHOLD) {
            final short opcode = LoggingUtil.readFirstShort(content);
            String opcodeHex = Integer.toHexString(opcode).toUpperCase();
            String opcodeName = getSendOpcodeName(opcode);
            String prefix = opcodeName == null ? "<UnknownPacket> " : "";
            log.info("{}ServerSend:{} [{}] ({}) <HEX> {} <TEXT> {}", prefix, opcodeName, opcodeHex, packetLength,
                    HexTool.toHexString(content), HexTool.toStringFromCharset(content));
        } else {
            log.info("{} ...", HexTool.toHexString(new byte[]{content[0], content[1]}));
        }
    }

    /**
     * 根据操作码值获取发送操作码名称
     *
     * @param opcode 操作码值
     * @return 操作码名称，未找到返回null
     */
    private String getSendOpcodeName(short opcode) {
        return OpcodeConstants.sendOpcodeNames.get((int) opcode);
    }
}
