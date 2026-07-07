
/**
 * 脚本类型：NPC
 * 对象 ID：1209004
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.sendOk("I hope for this travel to be a safe one, and that we get to live on a more peaceful place there... Hey, darling, let's go.");
    cm.dispose();
}