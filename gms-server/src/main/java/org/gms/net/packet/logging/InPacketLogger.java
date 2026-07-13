package org.gms.net.packet.logging;

import org.gms.config.GameConfig;
import org.gms.constants.net.OpcodeConstants;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.gms.net.packet.InPacket;
import org.gms.net.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.HexTool;

/**
 * 入站数据包日志记录器
 * Netty入站处理器，用于记录客户端发送的数据包
 * 可共享单例，在调试模式下输出数据包的操作码、十六进制内容和文本内容
 *
 * @author OdinMS开发团队
 */
@Sharable
public class InPacketLogger extends ChannelInboundHandlerAdapter implements PacketLogger {
    private static final Logger log = LoggerFactory.getLogger(InPacketLogger.class);

    /**
     * 日志内容阈值，超过此大小的数据包只记录头部
     */
    private static final int LOG_CONTENT_THRESHOLD = 3_000;

    /**
     * 处理入站消息
     * 如果开启了调试模式且消息是InPacket，则记录日志
     *
     * @param ctx Netty通道上下文
     * @param msg 入站消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (GameConfig.getServerBoolean("use_debug_show_packet") && msg instanceof InPacket packet) {
            log(packet);
        }

        ctx.fireChannelRead(msg);
    }

    /**
     * 记录入站数据包
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
            final String opcodeHex = Integer.toHexString(opcode).toUpperCase();
            final String opcodeName = getRecvOpcodeName(opcode);
            final String prefix = opcodeName == null ? "<UnknownPacket> " : "";
            log.info("{}ClientSend:{} [{}] ({}) <HEX> {} <TEXT> {}", prefix, opcodeName, opcodeHex, packetLength,
                    HexTool.toHexString(content), HexTool.toStringFromCharset(content));
        } else {
            log.info("{}...", HexTool.toHexString(new byte[]{content[0], content[1]}));
        }
    }

    /**
     * 根据操作码值获取接收操作码名称
     *
     * @param opcode 操作码值
     * @return 操作码名称，未找到返回null
     */
    private String getRecvOpcodeName(short opcode) {
        return OpcodeConstants.recvOpcodeNames.get((int) opcode);
    }
}
