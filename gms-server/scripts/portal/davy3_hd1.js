
/**
 * 脚本类型：传送门
 * 对象 ID：davy3_hd1
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 * 地图：S
 */
function enter(pi) {
    var eim = pi.getEventInstance();
    if (eim == null) {
        return false;
    }
    var map = pi.getMap(925100302);
    map.killAllMonsters();
    map.restoreMapSpawnPoints();
    map.instanceMapForceRespawn();

    pi.playPortalSound();
    pi.warp(925100302, 0);
    return true;
}