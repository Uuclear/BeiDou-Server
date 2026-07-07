
/**
 * 脚本类型：传送门
 * 对象 ID：party6_out
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    var eim = pi.getEventInstance();

    if (eim.isEventCleared()) {
        if (pi.isEventLeader()) {
            pi.playPortalSound();
            eim.warpEventTeam(930000800);
            return true;
        } else {
            pi.playerMessage(5, "请等待队长先通过传送门");
            return false;
        }
    } else {
        pi.playerMessage(5, "请先消灭剧毒魔像");
        return false;
    }
}