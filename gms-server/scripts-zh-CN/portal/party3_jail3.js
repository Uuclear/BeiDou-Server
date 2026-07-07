
/**
 * 脚本类型：传送门
 * 对象 ID：party3_jail3
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getEventInstance().getIntProperty("statusStg8") == 1) {
        pi.playPortalSound();
        pi.warp(920010930, 0);
        return true;
    } else {
        pi.playerMessage(5, "当前无法使用仓库，因为精灵的力量仍在塔内生效。");
        return false;
    }
}