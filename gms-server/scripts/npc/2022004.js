
/**
 * 脚本类型：NPC
 * 对象 ID：2022004
 * 功能描述：NPC脚本，提供地图传送。
 */
function start() {
    cm.sendNext("You did a great job back there, " + cm.getPlayer().getName() + ", well done. Now I will transport you back to El Nath. Have the pendant in your possession and talk to me when you feel ready to receive the new skill.");
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        cm.warp(211000000, "in01");
        cm.dispose();
    }
}