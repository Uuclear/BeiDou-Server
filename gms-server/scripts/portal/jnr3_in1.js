
/**
 * 脚本类型：传送门
 * 对象 ID：jnr3_in1
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getMap().getReactorByName("jnr3_out2").getState() == 1) {
        pi.playPortalSound();
        pi.warp(926110202, 0);
        return true;
    } else {
        pi.playerMessage(5, "The door is not opened yet.");
        return false;
    }
}