
/**
 * 脚本类型：传送门
 * 对象 ID：rnj1_pt00
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getEventInstance().getIntProperty("statusStg1") == 1) {
        pi.playPortalSound();
        pi.warp(926100001, 0); //next
        return true;
    } else {
        pi.playerMessage(5, "The portal is not opened yet.");
        return false;
    }
}