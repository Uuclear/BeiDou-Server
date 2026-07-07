
/**
 * 脚本类型：地图
 * 对象 ID：startEreb
 * 功能描述：地图脚本（startEreb），控制地图内特殊逻辑。
 */
function start(ms) {
    if (ms.getJobId() == 1000 && ms.getLevel() >= 10) {
        ms.unlockUI();
    }
}