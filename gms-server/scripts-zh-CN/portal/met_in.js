
/**
 * 脚本类型：传送门
 * 对象 ID：met_in
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    //pi.warp(910320000, 2); event not implemented

    pi.playPortalSound();
    pi.warp(103000103, 1);
    return true;
}