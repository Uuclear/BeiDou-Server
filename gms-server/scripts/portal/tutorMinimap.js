
/**
 * 脚本类型：传送门
 * 对象 ID：tutorMinimap
 * 功能描述：地图传送门入口脚本。
 */
function enter(pi) {
    pi.guideHint(1);
    pi.blockPortal();
    return true;
}