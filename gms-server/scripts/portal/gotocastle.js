
/**
 * 脚本类型：传送门
 * 对象 ID：gotocastle
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.isQuestCompleted(2321)) {
        pi.playPortalSound();
        pi.warp(pi.isQuestCompleted(2324) ? 106020501 : 106020500, 0);
        return true;
    } else {
        pi.playerMessage(5, "The path ahead is covered with sprawling vine thorns, only a Thorn Remover to clear this out...");
        return false;
    }
}