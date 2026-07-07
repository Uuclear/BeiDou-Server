
/**
 * 脚本类型：地图
 * 对象 ID：540010100
 * 功能描述：地图脚本（540010100），控制地图内特殊逻辑。
 */
var eventName = "AirPlane";
var toMap = 540010101;

function start(ms) {
    var em = ms.getClient().getEventManager(eventName);

    //is the player late to start the travel?
    if (em.getProperty("docked") == "false") {
        ms.getClient().getPlayer().warpAhead(toMap);
    }
}
