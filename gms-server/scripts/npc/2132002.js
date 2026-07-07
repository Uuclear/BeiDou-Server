
/**
 * 脚本类型：NPC
 * 对象 ID：2132002
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.sendNext("The magic of this forest is amazing...");
    cm.dispose();
}