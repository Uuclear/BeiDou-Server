
/**
 * 脚本类型：传送门
 * 对象 ID：party6_stage513
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (Math.random() < 0.1) {
        pi.playPortalSound();
        pi.warp(930000300, "16st");
    } else {
        pi.playPortalSound();
        pi.warp(930000300, "14st");
    }

    return true;
}