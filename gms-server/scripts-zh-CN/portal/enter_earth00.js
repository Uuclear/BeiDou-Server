
/**
 * 脚本类型：传送门
 * 对象 ID：enter_earth00
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (!pi.haveItem(4031890)) {
        pi.getPlayer().dropMessage(6, "需要霍夫卡才能激活此传送门。");
        return false;
    }

    pi.playPortalSound();
    pi.warp(221000300, "earth00");
    return true;
}