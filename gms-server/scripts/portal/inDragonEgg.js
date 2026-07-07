
/**
 * 脚本类型：传送门
 * 对象 ID：inDragonEgg
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    pi.playPortalSound();
    if (pi.isQuestStarted(22005)) {
        pi.playPortalSound();
        pi.warp(900020100, 0);
    } else {
        pi.playPortalSound();
        pi.warp(100030301, 0);
    }
    return true;
}  