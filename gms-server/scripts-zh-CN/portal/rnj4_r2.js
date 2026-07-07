
/**
 * 脚本类型：传送门
 * 对象 ID：rnj4_r2
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    var eim = pi.getEventInstance();
    var area = eim.getIntProperty("statusStg5");
    var reg = 1;

    if ((area >> reg) % 2 == 0) {
        area |= (1 << reg);
        eim.setIntProperty("statusStg5", area);

        pi.playPortalSound();
        pi.warp(926100301 + reg, 0); //next
        return true;
    } else {
        pi.playerMessage(5, "该房间正在被探索中。");
        return false;
    }
}