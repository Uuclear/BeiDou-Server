
/**
 * 脚本类型：反应堆
 * 对象 ID：3001000
 * 功能描述：反应堆触发后召唤怪物。
 */
function act() {
    rm.playerMessage(5, "毒液石头人已苏醒！");
    rm.spawnMonster(9300180, 1);
}