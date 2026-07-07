
/**
 * 脚本类型：地图
 * 对象 ID：240000111
 * 功能描述：地图脚本（240000111），控制地图内特殊逻辑。
 */
var eventName = "Cabin";
var toMap = 200090210;

function start(ms) {
    var em = ms.getClient().getEventManager(eventName);

    //is the player late to start the travel?
    if (em.getProperty("docked") == "false") {
        ms.getClient().getPlayer().warpAhead(toMap);
    }
}
