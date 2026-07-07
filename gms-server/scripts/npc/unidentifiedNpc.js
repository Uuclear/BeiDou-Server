
/**
 * 脚本类型：NPC
 * 对象 ID：unidentifiedNpc
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
function start() {
    cm.sendOk("NPC: " + cm.getNpc() + " is not found, please report this.");
}