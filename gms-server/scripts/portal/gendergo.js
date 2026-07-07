
/**
 * 脚本类型：传送门
 * 对象 ID：gendergo
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    var map = pi.getPlayer().getMap();
    if (pi.getPortal().getName() == "female00") {
        if (pi.getPlayer().getGender() == 1) {
            pi.playPortalSound();
            pi.warp(map.getId(), "female01");
            return true;
        } else {
            pi.message("This portal leads to the girls' area, try the portal at the other side.");
            return false;
        }
    } else {
        if (pi.getPlayer().getGender() == 0) {
            pi.playPortalSound();
            pi.warp(map.getId(), "male01");
            return true;
        } else {
            pi.message("This portal leads to the boys' area, try the portal at the other side.");
            return false;
        }
    }
}