
/**
 * 脚本类型：传送门
 * 对象 ID：glpqPortal5
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    var eim = pi.getEventInstance();
    if (eim != null) {
        if (eim.getIntProperty("glpq5") < 5) {
            pi.playerMessage(5, "传送门尚未开启。");
            return false;
        } else {
            pi.playPortalSound();
            pi.warp(610030600, 0);
            return true;
        }
    }

    return false;
}