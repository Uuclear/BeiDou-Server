
/**
 * 脚本类型：传送门
 * 对象 ID：glpqPortal02
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getPlayer().getJob().getJobNiche() == 2) {
        pi.playPortalSound();
        pi.warp(610030521, 0);
        return true;
    } else {
        pi.playerMessage(5, "Only mages may enter this portal.");
        return false;
    }
}