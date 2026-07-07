
/**
 * 脚本类型：传送门
 * 对象 ID：TD_MC_enterboss1
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    var questProgress = pi.getQuestProgressInt(2330, 3300005) + pi.getQuestProgressInt(2330, 3300006) + pi.getQuestProgressInt(2330, 3300007); //3 Yetis

    if (pi.isQuestStarted(2330) && questProgress < 3) {
        pi.openNpc(1300013);
    } else {
        pi.playPortalSound();
        pi.warp(106021401, 1);
    }

    return true;
}