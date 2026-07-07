
/**
 * 脚本类型：NPC
 * 对象 ID：1209002
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.sendOk("Please calm down, uncle. We are embarking to #bVictoria Island#k, we will be safe once we reach there. So, come on!");
    cm.dispose();
}