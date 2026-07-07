
/**
 * 脚本类型：传送门
 * 对象 ID：party3_jail2
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getEventInstance().getIntProperty("statusStg8") == 1) {
        pi.playPortalSound();
        pi.warp(920010920, 0);
        return true;
    } else {
        pi.playerMessage(5, "The storage is currently inaccessible, as the powers of the Pixies remains active within the tower.");
        return false;
    }
}