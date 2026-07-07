
/**
 * 脚本类型：传送门
 * 对象 ID：foxLaidy_map
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (!(pi.isQuestStarted(3647) && pi.haveItem(4031793, 1))) {
        pi.playPortalSound();
        pi.warp(222010200, "east00");
    } else {
        if (!pi.isQuestStarted(23647)) {
            pi.forceStartQuest(23647);
        }
        pi.playPortalSound();
        pi.warp(922220000, "east00");
    }

    return true;
}
