
/**
 * 脚本类型：传送门
 * 对象 ID：Spacegaga_out2
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
//Author: kevintjuh93

function enter(pi) {
    var eim = pi.getPlayer().getEventInstance();
    var fc = eim.getIntProperty("falls");

    if (fc >= 3) {
        pi.playPortalSound();
        pi.warp(922240200, 0);
    } else {
        eim.setIntProperty("falls", fc + 1);
        pi.playPortalSound();
        pi.warp(pi.getPlayer().getMapId(), 0);
    }

    return true;
}