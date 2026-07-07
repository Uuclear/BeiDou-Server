
/**
 * 脚本类型：传送门
 * 对象 ID：entertraining
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.isQuestStarted(1041)) {
        pi.playPortalSound();
        pi.warp(1010100, 4);
    } else if (pi.isQuestStarted(1042)) {
        pi.playPortalSound();
        pi.warp(1010200, 4);
    } else if (pi.isQuestStarted(1043)) {
        pi.playPortalSound();
        pi.warp(1010300, 4);
    } else if (pi.isQuestStarted(1044)) {
        pi.playPortalSound();
        pi.warp(1010400, 4);
    } else {
        pi.message("只有接受了麦加训练的人才可以进入训练场");
        return false;
    }
    return true;
}