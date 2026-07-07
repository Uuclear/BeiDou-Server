
/**
 * 脚本类型：传送门
 * 对象 ID：enterDollcave
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.isQuestCompleted(20730) || pi.isQuestCompleted(21734)) {  // puppeteer defeated, newfound secret path
        pi.playPortalSound();
        pi.warp(105040201, 2);
        return true;
    }

    pi.openNpc(1063011, "PupeteerPassword");
    return false;
}