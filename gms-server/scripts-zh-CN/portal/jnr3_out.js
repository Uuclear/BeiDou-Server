
/**
 * 脚本类型：传送门
 * 对象 ID：jnr3_out
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getMap().getReactorByName("jnr3_out3").getState() == 1) {
        pi.playPortalSound();
        pi.warp(926110203, 0); //next
        return true;
    } else {
        pi.playerMessage(5, "传送门尚未开启。");
        return false;
    }
}