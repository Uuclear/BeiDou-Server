
/**
 * 脚本类型：传送门
 * 对象 ID：outRider
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.canHold(4001193, 1)) {
        pi.gainItem(4001193, 1);
        pi.playPortalSound();
        pi.warp(211050000, 4);
        return true;
    } else {
        pi.playerMessage(5, "Free a slot on your inventory before receiving the couse clear's token.");
        return false;
    }
}