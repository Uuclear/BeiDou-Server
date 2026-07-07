
/**
 * 脚本类型：传送门
 * 对象 ID：outPerrion_1
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    pi.message("你发现了通往地下神殿入口的捷径");
    pi.playPortalSound();
    pi.warp(105100000, 2);
    return true;
}