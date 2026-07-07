
/**
 * 脚本类型：传送门
 * 对象 ID：outPerrion_1
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    pi.message("You found a shortcut to the start of the underground temple.");
    pi.playPortalSound();
    pi.warp(105100000, 2);
    return true;
}