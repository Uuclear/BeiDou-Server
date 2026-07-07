
/**
 * 脚本类型：地图
 * 对象 ID：922000000
 * 功能描述：地图脚本（922000000），控制地图内特殊逻辑。
 */
function start(ms) {
    var map = ms.getClient().getChannelServer().getMapFactory().getMap(922000000);
    map.clearDrops();
    map.resetReactors();
    map.shuffleReactors();

    return (true);
}