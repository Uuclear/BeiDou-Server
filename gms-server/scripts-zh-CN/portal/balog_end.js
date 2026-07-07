
/**
 * 脚本类型：传送门
 * 对象 ID：balog_end
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (!pi.canHold(4001261, 1)) {
        pi.playerMessage(5, "请给装备栏腾出至少1个空格子。");
        return false;
    }
    pi.gainItem(4001261, 1);
    pi.playPortalSound();
    pi.warp(105100100, 0);
    return true;
}