
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
        pi.message("一股神秘的力量阻止你进入。");
        return false;
    }
}