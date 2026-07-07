
/**
 * 脚本类型：传送门
 * 对象 ID：gaga_success
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
//Author: kevintjuh93

function enter(pi) {
    pi.playPortalSound();
    pi.warp(922240100 + (pi.getPlayer().getMapId() - 922240000), 0);
    return true;
}  