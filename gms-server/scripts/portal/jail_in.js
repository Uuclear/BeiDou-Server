
/**
 * 脚本类型：传送门
 * 对象 ID：jail_in
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
/**
 * @author: Ronan
 * @event: Jail - visit to jail :3
 */

function enter(pi) {
    pi.playPortalSound();
    pi.warp(300000012, "portal");
    return true;
}