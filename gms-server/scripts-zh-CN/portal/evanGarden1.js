
/**
 * 脚本类型：传送门
 * 对象 ID：evanGarden1
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.isQuestStarted(22008)) {
        pi.playPortalSound();
        pi.warp(100030103, "west00");
    } else {
        pi.playerMessage(5, "后院区域需完成任务后方可进入！");
    }
    return true;
}  