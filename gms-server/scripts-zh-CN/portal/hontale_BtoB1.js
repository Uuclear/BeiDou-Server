
/**
 * 脚本类型：传送门
 * 对象 ID：hontale_BtoB1
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (pi.getMap().countPlayers() == 1) {
        pi.getPlayer().dropMessage(6, "当前地图仅剩你一人，请等待其他玩家携带钥匙进入。");
        return false;
    } else {
        if (pi.haveItem(4001087)) {
            pi.getPlayer().dropMessage(6, "背包中持有【第一个迷宫的水晶钥匙】时无法进入下一地图！");
            return false;
        }
        pi.playPortalSound();
        pi.warp(240050101, 0);
        return true;
    }
}