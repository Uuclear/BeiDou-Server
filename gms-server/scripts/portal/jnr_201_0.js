
/**
 * 脚本类型：传送门
 * 对象 ID：jnr_201_0
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getMap().getReactorByName("jnr31_out").getState() == 1) {
        pi.playPortalSound();
        pi.warp(926110200, 1);
        return true;
    } else {
        pi.playerMessage(5, "The door is not opened yet.");
        return false;
    }
}