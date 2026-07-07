
/**
 * 脚本类型：传送门
 * 对象 ID：tutoChatNPC
 * 功能描述：地图传送门入口脚本。
 */
function enter(pi) {
    if (pi.hasLevel30Character()) {
        pi.openNpc(2007);
    }
    pi.blockPortal();
    return true;
}