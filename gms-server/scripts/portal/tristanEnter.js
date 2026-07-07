
/**
 * 脚本类型：传送门
 * 对象 ID：tristanEnter
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.isQuestCompleted(2238)) {
        pi.playPortalSound();
        pi.warp(105100101, "in00");
        return true;
    } else {
        pi.message("A mysterious force won't let you in.");
        return false;
    }
}