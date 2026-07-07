
/**
 * 脚本类型：传送门
 * 对象 ID：babyPigOut
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.isQuestCompleted(22015)) {
        pi.playPortalSound();
        pi.warp(100030300, 2);
    } else {
        pi.playerMessage(5, "请救救小猪！");//not gms like
    }
    return true;
}