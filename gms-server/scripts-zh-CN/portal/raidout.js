
/**
 * 脚本类型：传送门
 * 对象 ID：raidout
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    var map = pi.getPlayer().getSavedLocation("BOSSPQ");
    if (map == -1) {
        map = 100000000;
    }

    pi.playPortalSound();
    pi.warp(map, 0);
    return true;
}