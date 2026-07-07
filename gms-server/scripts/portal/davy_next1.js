
/**
 * 脚本类型：传送门
 * 对象 ID：davy_next1
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    try {
        var eim = pi.getEventInstance();
        if (eim != null && eim.getProperty("stage2") === "3") {
            pi.playPortalSound();
            pi.warp(925100200, 0); //next
            return true;
        } else {
            pi.playerMessage(5, "The portal is not opened yet.");
            return false;
        }
    } catch (e) {
        pi.playerMessage(5, "Error: " + e);
    }

    return false;
}