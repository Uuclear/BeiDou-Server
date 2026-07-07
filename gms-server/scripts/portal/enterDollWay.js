
/**
 * 脚本类型：传送门
 * 对象 ID：enterDollWay
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.isQuestCompleted(20730) || pi.isQuestCompleted(21734)) {  // puppeteer defeated, newfound secret path
        pi.playPortalSound();
        pi.warp(105070300, 3);
        return true;
    } else if (pi.isQuestStarted(21734)) {
        pi.playPortalSound();
        pi.warp(910510100, 0);
        return true;
    } else {
        pi.message("An ominous power prevents you from passing here.");
        return false;
    }
}