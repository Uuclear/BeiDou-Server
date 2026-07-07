
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
    cm.sendOk("叔叔，请冷静一点。我们正在启程前往金银岛，一旦到达那里我们就会安全了。所以，加油！");
    cm.dispose();
}