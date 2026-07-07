
/**
 * 脚本类型：地图
 * 对象 ID：912000000
 * 功能描述：地图脚本（912000000），控制地图内特殊逻辑。
 */
function start(ms) {

    if (ms.getQuestStatus(2175) == 1) {
        var mobId = 9300156;
        var player = ms.getPlayer();
        var map = player.getMap();

        if (map.getMonsterById(mobId) != null) {
            return;
        }

        const LifeFactory = Java.type('org.gms.server.life.LifeFactory');
        const Point = Java.type('java.awt.Point');
        map.spawnMonsterOnGroundBelow(LifeFactory.getMonster(mobId), new Point(624, 180));
    }
}