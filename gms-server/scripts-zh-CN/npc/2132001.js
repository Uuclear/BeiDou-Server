
/**
 * 脚本类型：NPC
 * 对象 ID：2132001
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.sendNext("叫我黑暗领主吧。我会让盗贼在社会中找到自己的位置...几年后你就会看到！");
    cm.dispose();
}