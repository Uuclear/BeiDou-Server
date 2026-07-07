
/**
 * 脚本类型：传送门
 * 对象 ID：s4common1_exit
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
//Author: Ronan

function enter(pi) {
    if (pi.hasItem(4031495)) {
        pi.playPortalSound();
        pi.warp(921100301);
    } else {
        pi.playPortalSound();
        pi.warp(211040100);
    }

    return true;
}