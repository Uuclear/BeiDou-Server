
/**
 * 脚本类型：传送门
 * 对象 ID：enterGym
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.isQuestStarted(21701)) {
        pi.playPortalSound();
        pi.warp(914010000, 1);
        return true;
    } else if (pi.isQuestStarted(21702)) {
        pi.playPortalSound();
        pi.warp(914010100, 1);
        return true;
    } else if (pi.isQuestStarted(21703)) {
        pi.playPortalSound();
        pi.warp(914010200, 1);
        return true;
    } else {
        pi.playerMessage(5, "You will be allowed to enter the Penguin Training Ground only if you are receiving a lesson from Puo.");
        return false;
    }
}