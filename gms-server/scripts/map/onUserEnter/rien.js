
/**
 * 脚本类型：地图
 * 对象 ID：rien
 * 功能描述：(21019, "miss=o;arr=o;helper=clear")) {（原版描述）。
 */
function start(ms) {
    if (ms.isQuestCompleted(21101) && ms.containsAreaInfo(21019, "miss=o;arr=o;helper=clear")) {
        ms.updateAreaInfo(21019, "miss=o;arr=o;ck=1;helper=clear");
    }
    ms.unlockUI();
}