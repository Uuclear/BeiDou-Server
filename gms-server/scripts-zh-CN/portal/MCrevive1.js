
/**
 * 脚本类型：传送门
 * 对象 ID：MCrevive1
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
/*
 [CelticMS] Monster Carnival Reviving Field 1
 */

function enter(pi) {
    pi.warp(980000101, 0);
    pi.playPortalSound();
    return true;
}
