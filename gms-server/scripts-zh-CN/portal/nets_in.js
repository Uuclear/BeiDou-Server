
/**
 * 脚本类型：传送门
 * 对象 ID：nets_in
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    pi.getPlayer().saveLocation("MIRROR");
    pi.playPortalSound();
    pi.warp(926010000, 4);
    return true;
}