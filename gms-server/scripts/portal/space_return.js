
/**
 * 脚本类型：传送门
 * 对象 ID：space_return
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
//Author: kevintjuh93

function enter(pi) {
    pi.playPortalSound();
    pi.warp(pi.getPlayer().getSavedLocation("EVENT"));
    return true;
}