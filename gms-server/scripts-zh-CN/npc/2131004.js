
/**
 * 脚本类型：NPC
 * 对象 ID：2131004
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.sendNext("呼呼...");
    cm.dispose();
}