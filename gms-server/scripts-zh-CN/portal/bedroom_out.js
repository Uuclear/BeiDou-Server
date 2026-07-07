
/**
 * 脚本类型：传送门
 * 对象 ID：bedroom_out
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.isQuestStarted(2570)) {
        pi.playPortalSound();
        pi.warp(120000101, 0);
        return true;
    }
    pi.earnTitle("你似乎还有一些未完成的事情，我从你的眼神中可以看出来。等等……不，那些只是眼屎。");
    return false;
}