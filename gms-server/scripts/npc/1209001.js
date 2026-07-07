
/**
 * 脚本类型：NPC
 * 对象 ID：1209001
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.sendOk("The #rBlack Mage#k's forces approaches here in an unstoppable pace... We have no other way than to flee this area now, leaving our home behind. Oh, the tragedy!");
    cm.dispose();
}