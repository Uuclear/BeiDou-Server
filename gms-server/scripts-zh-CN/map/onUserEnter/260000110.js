
/**
 * 脚本类型：地图
 * 对象 ID：260000110
 * 功能描述：地图脚本（260000110），控制地图内特殊逻辑。
 */
var eventName = "Genie";
var toMap = 200090410;

function start(ms) {
    var em = ms.getClient().getEventManager(eventName);

    //is the player late to start the travel?
    if (em.getProperty("docked") == "false") {
        ms.getClient().getPlayer().warpAhead(toMap);
    }
}
