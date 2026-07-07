
/**
 * 脚本类型：地图
 * 对象 ID：cannon_tuto_direction1
 * 功能描述：("Effect/Direction4.img/effect/cannonshooter/balloon/0", 9000, 0, 0, 0, -1);（原版描述）。
 */
function start(ms) {
    ms.playSound("cannonshooter/flying");
    ms.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balloon/0", 9000, 0, 0, 0, -1);
    ms.sendDirectionInfo(1, 1500);
    ms.setDirectionStatus(true);
}