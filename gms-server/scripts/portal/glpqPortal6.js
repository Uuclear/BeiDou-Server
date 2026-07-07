
/**
 * 脚本类型：传送门
 * 对象 ID：glpqPortal6
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    var eim = pi.getEventInstance();
    if (eim != null) {
        if (eim.getIntProperty("glpq6") < 3) {
            pi.playerMessage(5, "The portal is not opened yet.");
            return false;
        } else {
            pi.playPortalSound();
            pi.warp(610030700, 0);
            return true;
        }
    }

    return false;
}