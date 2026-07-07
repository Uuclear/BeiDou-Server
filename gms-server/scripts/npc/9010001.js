
/**
 * 脚本类型：NPC
 * 对象 ID：9010001
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.sendNext("Hi, I'm #p9010001#.");
    cm.dispose();
}