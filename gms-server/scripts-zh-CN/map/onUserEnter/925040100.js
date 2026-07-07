
/**
 * 脚本类型：地图
 * 对象 ID：925040100
 * 功能描述：地图脚本（925040100），控制地图内特殊逻辑。
 */
function start(ms) {
    var player = ms.getPlayer();
    var map = player.getMap();

    if (ms.isQuestStarted(21747) && ms.getQuestProgressInt(21747, 9300351) == 0) {
        const LifeFactory = Java.type('org.gms.server.life.LifeFactory');
        const Point = Java.type('java.awt.Point');
        map.spawnMonsterOnGroundBelow(LifeFactory.getMonster(9300351), new Point(897, 51));
    }
}