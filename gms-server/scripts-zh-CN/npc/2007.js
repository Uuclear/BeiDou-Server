
/**
 * 脚本类型：NPC
 * 对象 ID：2007
 * 功能描述：NPC脚本，提供地图传送。
 */
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.sendNext("愉快旅行。");
        cm.dispose();
    } else {
        if (status == 0 && mode == 0) {
            cm.sendNext("愉快旅行。");
            cm.dispose();
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            cm.sendYesNo("您想要跳过教程，直接前往明珠港吗？");
        } else if (status == 1) {
            cm.warp(104000000, 0);
            cm.dispose();
        }
    }
}