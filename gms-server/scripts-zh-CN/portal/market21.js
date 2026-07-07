
/**
 * 脚本类型：传送门
 * 对象 ID：market21
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getPlayer().getMapId() != 910000000) {
        pi.getPlayer().saveLocation("FREE_MARKET");
        pi.playPortalSound();
        pi.warp(910000000, "out00");
        return true;
    }
    return false;
}