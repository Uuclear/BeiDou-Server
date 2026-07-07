
/**
 * 脚本类型：传送门
 * 对象 ID：davy_next0
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function passedGrindMode(map, eim) {
    if (eim.getIntProperty("grindMode") == 0) {
        return true;
    }
    return eim.activatedAllReactorsOnMap(map, 2511000, 2517999);
}

function enter(pi) {
    if (pi.getMap().getMonsters().size() == 0 && passedGrindMode(pi.getMap(), pi.getEventInstance())) {
        pi.playPortalSound();
        pi.warp(925100100, 0); //next
        return true;
    } else {
        pi.playerMessage(5, "The portal is not opened yet.");
        return false;
    }
}