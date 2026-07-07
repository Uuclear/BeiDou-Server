
/**
 * 脚本类型：传送门
 * 对象 ID：elevator
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    try {
        var elevator = pi.getEventManager("Elevator");
        if (elevator == null) {
            pi.getPlayer().dropMessage(5, "The elevator is under maintenance.");
        } else if (elevator.getProperty(pi.getMapId() == 222020100 ? ("goingUp") : ("goingDown")) === "false") {
            pi.playPortalSound();
            pi.warp(pi.getMapId() == 222020100 ? 222020110 : 222020210, 0);
            return true;
        } else if (elevator.getProperty(pi.getMapId() == 222020100 ? ("goingUp") : ("goingDown")) === "true") {
            pi.getPlayer().dropMessage(5, "The elevator is currently moving.");
        } else {
            pi.getPlayer().dropMessage(5, "Dafuq is happening?!");
        }
    } catch (e) {
        pi.getPlayer().dropMessage(5, "Error: " + e);
    }
    return false;
}