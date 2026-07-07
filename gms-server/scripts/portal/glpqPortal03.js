
/**
 * 脚本类型：传送门
 * 对象 ID：glpqPortal03
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getPlayer().getJob().getJobNiche() == 4) {
        pi.playPortalSound();
        pi.warp(610030530, 0);
        return true;
    } else {
        pi.playerMessage(5, "Only thieves may enter this portal.");
        return false;
    }
}