
/**
 * 脚本类型：传送门
 * 对象 ID：glpqPortal0
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getEventInstance().getIntProperty("glpq1") == 0) {
        pi.getEventInstance().dropMessage(5, "This path is currently blocked.");
        return false;

    } else {
        pi.playPortalSound();
        pi.warp(610030200, 0);
        return true;
    }
}

