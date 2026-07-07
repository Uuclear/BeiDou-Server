


/**
 * 脚本类型：反应堆
 * 对象 ID：5511000
 * 功能描述：地图反应堆交互脚本。
 * 原作者：SharpAceX
 */
function act() {
    const targaMobId = 9420542;
    if (rm.getReactor().getMap().getMonsterById(targaMobId) == null) {
        rm.summonBossDelayed(targaMobId, 3200, -527, 637, "Bgm09/TimeAttack", "Beware! The furious Targa has shown himself!");
    }
}