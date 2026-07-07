
/**
 * 脚本类型：传送门
 * 对象 ID：glpqEnter
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.haveItem(3992041, 1)) {
        pi.playPortalSound();
        pi.warp(610030020, "out00");
        return true;
    } else {
        pi.playerMessage(5, "※ 巨大的铁门纹丝不动，但可以看到一个明显的钥匙孔。");
        return false;
    }
}