
/**
 * 脚本类型：地图
 * 对象 ID：677000007
 * 功能描述：地图脚本（677000007），控制地图内特殊逻辑。
 */
function start(ms) {
    const Point = Java.type('java.awt.Point');
    var pos = new Point(171, 50);
    var mobId = 9400611;
    var mobName = "Crocell";

    var player = ms.getPlayer();
    var map = player.getMap();

    if (map.getMonsterById(mobId) != null) {
        return;
    }

    const LifeFactory = Java.type('org.gms.server.life.LifeFactory');
    map.spawnMonsterOnGroundBelow(LifeFactory.getMonster(mobId), pos);
    player.message(mobName + " has appeared!");
}