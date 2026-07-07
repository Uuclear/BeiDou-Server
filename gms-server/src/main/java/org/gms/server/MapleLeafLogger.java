package org.gms.server;

import org.gms.client.Character;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 枫叶（活动货币）获取日志记录器。
 */
public class MapleLeafLogger {
    private static final Logger log = LoggerFactory.getLogger(MapleLeafLogger.class);

    /**
     * 记录日志。
     * @param player 玩家
     * @param gotPrize gotPrize
     * @param operation operation
     */
    public static void log(Character player, boolean gotPrize, String operation) {
        String action = gotPrize ? " used a maple leaf to buy " + operation : " redeemed " + operation + " VP for a leaf";
        log.info("{} {}", player.getName(), action);
    }
}
