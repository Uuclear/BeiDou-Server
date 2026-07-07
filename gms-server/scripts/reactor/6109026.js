
/**
 * 脚本类型：反应堆
 * 对象 ID：6109026
 * 功能描述：地图反应堆交互脚本。
 */
function action() {
    var flames = Array("g3", "g4", "g5", "h3", "h4", "h5", "i3", "i4", "i5");
    for (var i = 0; i < flames.length; i++) {
        rm.getMap().toggleEnvironment(flames[i]);
    }
}

var fid = "glpq_f6";

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