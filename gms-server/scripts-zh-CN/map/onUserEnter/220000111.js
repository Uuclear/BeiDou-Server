
/**
 * 脚本类型：地图
 * 对象 ID：220000111
 * 功能描述：地图脚本（220000111），控制地图内特殊逻辑。
 */
var eventName = "Trains";
var toMap = 200090110;

function start(ms) {
    var em = ms.getClient().getEventManager(eventName);

    //is the player late to start the travel?
    if (em.getProperty("docked") == "false") {
        ms.getClient().getPlayer().warpAhead(toMap);
    }
}