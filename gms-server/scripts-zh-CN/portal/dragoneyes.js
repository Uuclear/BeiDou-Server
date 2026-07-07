
/**
 * 脚本类型：传送门
 * 对象 ID：dragoneyes
 * 功能描述：地图传送门入口脚本。
 */
function enter(pi) {
    if (pi.isQuestCompleted(22012)) {
        return false;
    } else {
        pi.forceCompleteQuest(22012);
    }
    pi.blockPortal();
    return true;
}