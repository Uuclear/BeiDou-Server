
/**
 * 脚本类型：反应堆
 * 对象 ID：6109013
 * 功能描述：地图反应堆交互脚本。
 */
var fid = "glpq_s";

function action() {
    rm.mapMessage(6, "All stirges have disappeared.");
    rm.getMap().killAllMonsters(true);
    eim.setIntProperty(fid, 777);
}

function touch() {
    var eim = rm.getEventInstance();

    if (eim.getIntProperty(fid) == 5) {
        action();
    }
    eim.setIntProperty(fid, eim.getIntProperty(fid) + 1);
}

function untouch() {
    var eim = rm.getEventInstance();

    if (eim.getIntProperty(fid) == 5) {
        action();
    }
    eim.setIntProperty(fid, eim.getIntProperty(fid) - 1);
}