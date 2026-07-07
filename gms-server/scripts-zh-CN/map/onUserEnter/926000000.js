
/**
 * 脚本类型：地图
 * 对象 ID：926000000
 * 功能描述：地图脚本（926000000），控制地图内特殊逻辑。
 */
function start(ms) {
    var map = ms.getClient().getChannelServer().getMapFactory().getMap(926000000);
    map.resetPQ(1);

    if (map.countMonster(9100013) == 0) {
        map.spawnMonsterOnGroundBelow(LifeFactory.getMonster(9100013), new java.awt.Point(82, 200));
    }

    return (true);
}
