
/**
 * 脚本类型：传送门
 * 对象 ID：party3_gardenin
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getPlayer().getParty() != null && pi.isEventLeader() && pi.haveItem(4001055, 1)) {
        pi.playPortalSound();
        pi.getEventInstance().warpEventTeam(920010100);
        return true;
    } else {
        pi.playerMessage(5, "请让队长进入此传送门，并确保持有生命草。");
        return false;
    }
}