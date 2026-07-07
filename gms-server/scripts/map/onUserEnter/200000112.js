
/**
 * 脚本类型：地图
 * 对象 ID：200000112
 * 功能描述：地图脚本（200000112），控制地图内特殊逻辑。
 */
var eventName = "Boats";
var toMap = 200090000;

function start(ms) {
    var em = ms.getClient().getEventManager(eventName);

    //is the player late to start the travel?
    if (em.getProperty("docked") == "false") {
        ms.getClient().getPlayer().warpAhead(toMap);
    }
}
