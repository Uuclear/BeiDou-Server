
/**
 * 脚本类型：传送门
 * 对象 ID：investigate2
 * 功能描述：地图传送门入口脚本。
 */
function enter(pi) {
    if (pi.isQuestActive(2322) || pi.isQuestCompleted(2322)) {
        pi.openNpc(1300014);
        return true;
    }
    return false;
}