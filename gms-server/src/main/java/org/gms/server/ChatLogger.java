package org.gms.server;

import org.gms.client.Client;
import org.gms.config.GameConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 聊天消息日志记录器。
 */
public class ChatLogger {
    private static final Logger log = LoggerFactory.getLogger(ChatLogger.class);

    /**
     * 记录日志。
     * @param c c
     * @param chatType chatType
     * @param message message
     */
    public static void log(Client c, String chatType, String message) {
        if (GameConfig.getServerBoolean("use_enable_chat_log")) {
            log.info("({}) {}: {}", chatType, c.getPlayer().getName(), message);
        }
    }
}
