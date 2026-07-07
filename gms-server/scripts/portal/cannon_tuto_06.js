
/**
 * 脚本类型：传送门
 * 对象 ID：cannon_tuto_06
 * 功能描述：地图传送门入口脚本。
 */
function enter(pi) {
    pi.setDirectionStatus(true);
    pi.lockUI2();
    pi.openNpc(3, 1096003);
    return true;
}