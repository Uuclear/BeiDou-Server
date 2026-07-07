
/**
 * 脚本类型：NPC
 * 对象 ID：2100003
 * 功能描述：NPC脚本，提供打开商店。
 */
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.openShopNPC(2100003);
    cm.dispose();
}