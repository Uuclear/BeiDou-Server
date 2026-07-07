
/**
 * 脚本类型：传送门
 * 对象 ID：rnj_exit
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    pi.playPortalSound();
    pi.warp(261000011, 0);
    return true;
}