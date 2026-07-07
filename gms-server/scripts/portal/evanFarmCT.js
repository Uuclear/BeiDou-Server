
/**
 * 脚本类型：传送门
 * 对象 ID：evanFarmCT
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.isQuestStarted(22010) || pi.getPlayer().getJob().getId() != 2001) {
        pi.playPortalSound();
        pi.warp(100030310, 0);
    } else {
        pi.playerMessage(5, "Cannot enter the Lush Forest without a reason.");
    }
    return true;
}