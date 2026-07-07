
/**
 * 脚本类型：传送门
 * 对象 ID：thief_in1
 * 功能描述：地图传送门入口脚本。
 */
function enter(pi) {
    // unexpected warp condition noticed thanks to IxianMace

    pi.openNpc(1063011, "ThiefPassword");
    return false;
}