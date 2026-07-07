
/**
 * 脚本类型：传送门
 * 对象 ID：investigate1
 * 功能描述：地图传送门入口脚本。
 */
function enter(pi) {
    if (pi.isQuestActive(2314) || pi.isQuestCompleted(2314)) {
        pi.openNpc(1300014);
        return true;
    }
    return false;
}