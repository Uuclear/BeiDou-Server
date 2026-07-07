
/**
 * 脚本类型：传送门
 * 对象 ID：enterBackStreet
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.isQuestActive(21747) || pi.isQuestActive(21744) && pi.isQuestCompleted(21745)) {
        pi.playPortalSound();
        pi.warp(925040000, 0);
        return true;
    } else {
        pi.message("You don't have permission to access this area.");
        return false;
    }
}