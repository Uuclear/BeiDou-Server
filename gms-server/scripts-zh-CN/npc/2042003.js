
/**
 * 脚本类型：NPC
 * 对象 ID：2042003
 * 功能描述：NPC脚本，提供地图传送。
 */
var status = 0;
var request;

function start() {
    status = -1;
    action(1, 0, 0);
}


function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            cm.warpParty(980000000);
            cm.cancelCPQLobby();
            cm.dispose();
        }
    }
}

