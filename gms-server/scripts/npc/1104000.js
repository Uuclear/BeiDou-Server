
/**
 * 脚本类型：NPC
 * 对象 ID：1104000
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
var status;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || (mode == 0 && status == 0)) {
        cm.dispose();
        return;
    } else if (mode == 0) {
        status--;
    } else {
        status++;
    }

    if (status == 0) {
        cm.sendNext("What the... you don't belong here!");
    } else if (status == 1) {
        var puppet = cm.getEventManager("Puppeteer");
        puppet.setProperty("player", cm.getPlayer().getName());
        puppet.startInstance(cm.getPlayer());
        cm.dispose();

    }
}