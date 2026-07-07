
/**
 * 脚本类型：传送门
 * 对象 ID：MCrevive4
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
/*
[CelticMS] Monster Carnival Reviving Field 1
*/

function enter(pi) {
    var portal = 0;
    switch (pi.getPlayer().getTeam()) {
        case 0:
            portal = 4;
            break;
        case 1:
            portal = 3;
            break;
    }
    pi.warp(980000401, portal);
    pi.playPortalSound();
    return true;
}
