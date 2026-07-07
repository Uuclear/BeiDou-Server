
/**
 * 脚本类型：地图
 * 对象 ID：677000001
 * 功能描述：地图脚本（677000001），控制地图内特殊逻辑。
 */
function start(ms) {
    const Point = Java.type('java.awt.Point');
    var pos = new Point(461, 61);
    var mobId = 9400612;
    var mobName = "Marbas";

    var player = ms.getPlayer();
    var map = player.getMap();

    if (map.getMonsterById(mobId) != null) {
        return;
    }

    const LifeFactory = Java.type('org.gms.server.life.LifeFactory');
    const mobobj = LifeFactory.getMonster(mobId);
    mobName = mobobj.getName() || mobName;
    map.spawnMonsterOnGroundBelow(mobobj, pos);
    player.message(mobName + " 已现身！");
}