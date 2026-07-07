


/**
 * 脚本类型：传送门
 * 对象 ID：obstacle
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 * 原作者：Ronan
 */
function enter(pi) {
    if (pi.isQuestStarted(100202)) {
        pi.playPortalSound();
        pi.warp(106020400, 2);
        return true;
    } else if (pi.hasItem(4000507)) {
        pi.gainItem(4000507, -1);
        pi.message("You have used a Poison Spore to pass through the barrier.");

        pi.playPortalSound();
        pi.warp(106020400, 2);
        return true;
    }

    pi.message("The overgrown vines is blocking the way.");
    return false;
}