package org.gms.constants.net;

/**
 * 服务器常量类
 * <p>
 * 定义游戏服务器的基本配置常量，包括服务器版本、调试变量、屏蔽名称列表、
 * 200级祝贺消息，以及北斗系统版本和构建时间等信息。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public class ServerConstants {

    /** 服务器版本号 */
    public static final short VERSION = 83;

    /** 调试变量数组，用于数据包测试 */
    public static int[] DEBUG_VALUES = new int[10];

    /** 被屏蔽的角色名称列表，包含管理员名称、敏感词和不良词汇 */
    public static final String[] BLOCKED_NAMES = {"admin", "owner", "moderator", "intern", "donor", "administrator", "FREDRICK", "help", "helper", "alert", "notice", "maplestory", "fuck", "wizet", "fucking", "negro", "fuk", "fuc", "penis", "pussy", "asshole", "gay",
            "nigger", "homo", "suck", "cum", "shit", "shitty", "condom", "security", "official", "rape", "nigga", "sex", "tit", "boner", "orgy", "clit", "asshole", "fatass", "bitch", "support", "gamemaster", "cock", "gaay", "gm",
            "operate", "master", "sysop", "party", "GameMaster", "community", "message", "event", "test", "meso", "Scania", "yata", "AsiaSoft", "henesys"};

    /** 角色达到200级时的全服祝贺消息模板 */
    public static final String LEVEL_200 = "[Congrats] %s has reached Level %d! Congratulate %s on such an amazing achievement!";

    /** 北斗系统版本号 */
    public static final String BEI_DOU_VERSION = "1.11";

    /** 北斗系统构建时间 */
    public static final String BEI_DOU_BUILD_TIME = "2026-05-31 16:03:25";
}
