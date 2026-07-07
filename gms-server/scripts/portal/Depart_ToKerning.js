
/**
 * 脚本类型：传送门
 * 对象 ID：Depart_ToKerning
 * 功能描述：启动关联事件实例并传送玩家（如班车/副本入口）。
 */
function enter(pi) {
    var em = pi.getEventManager("KerningTrain");
    if (!em.startInstance(pi.getPlayer())) {
        pi.message("The passenger wagon is already full. Try again a bit later.");
        return false;
    }

    pi.playPortalSound();
    return true;
}