
/**
 * 脚本类型：反应堆
 * 对象 ID：6109024
 * 功能描述：地图反应堆交互脚本。
 */
function action() {
    var flames = Array("d6", "d7", "e6", "e7", "f6", "f7");
    for (var i = 0; i < flames.length; i++) {
        rm.getMap().toggleEnvironment(flames[i]);
    }
}

var fid = "glpq_f4";

function touch() {
    var eim = rm.getEventInstance();

    if (eim.getIntProperty(fid) == 0) {
        action();
    }
    eim.setIntProperty(fid, eim.getIntProperty(fid) + 1);
}

function untouch() {
    var eim = rm.getEventInstance();

    if (eim.getIntProperty(fid) == 1) {
        action();
    }
    eim.setIntProperty(fid, eim.getIntProperty(fid) - 1);
}