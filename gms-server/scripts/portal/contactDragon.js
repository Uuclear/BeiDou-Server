
/**
 * 脚本类型：传送门
 * 对象 ID：contactDragon
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
//Author: kevintjuh93

function enter(pi) {
    pi.playPortalSound();
    pi.warp(900090100, 0);
    return true;
}