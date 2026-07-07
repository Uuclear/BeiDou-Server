
/**
 * 脚本类型：地图
 * 对象 ID：926120300
 * 功能描述：地图脚本（926120300），控制地图内特殊逻辑。
 */
function getInactiveReactors(map) {
    var reactors = [];

    var iter = map.getReactors().iterator();
    while (iter.hasNext()) {
        var mo = iter.next();
        if (mo.getState() >= 7) {
            reactors.push(mo);
        }
    }

    return reactors;
}

function start(ms) {
    var map = ms.getClient().getChannelServer().getMapFactory().getMap(926120300);
    map.resetReactors(getInactiveReactors(map));

    return (true);
}