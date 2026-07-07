
/**
 * 脚本类型：传送门
 * 对象 ID：rienCaveEnter
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.isQuestStarted(21201) || pi.isQuestStarted(21302)) { //aran first job
        pi.playPortalSound();
        pi.warp(140030000, 1);
        return true;
    } else {
        pi.playerMessage(5, "Something seems to be blocking this portal!");
        return false;
    }

}