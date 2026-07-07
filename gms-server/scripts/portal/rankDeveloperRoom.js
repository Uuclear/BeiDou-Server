
/**
 * 脚本类型：传送门
 * 对象 ID：rankDeveloperRoom
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getPlayer().getMapId() != 777777777) {
        const Server = Java.type('org.gms.net.server.Server');
        if (!Server.getInstance().canEnterDeveloperRoom()) {
            pi.message("The next room is currently unavailable.");
            return false;
        }

        pi.getPlayer().saveLocation("DEVELOPER");
        pi.playPortalSound();
        pi.warp(777777777, "out00");
    } else {
        try {
            var toMap = pi.getPlayer().getSavedLocation("DEVELOPER");
            pi.playPortalSound();
            pi.warp(toMap, "in00");
        } catch (err) {
            pi.playPortalSound();
            pi.warp(100000000, 0);
        }
    }

    return true;
}