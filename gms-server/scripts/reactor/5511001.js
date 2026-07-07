


/**
 * 脚本类型：反应堆
 * 对象 ID：5511001
 * 功能描述：地图反应堆交互脚本。
 * 原作者：SharpAceX
 */
function act() {
    const scarlionMobId = 9420547;
    if (rm.getReactor().getMap().getMonsterById(scarlionMobId) == null) {
        rm.summonBossDelayed(scarlionMobId, 3200, -238, 636, "Bgm09/TimeAttack", "Beware! The furious Scarlion has shown himself!");
    }
}