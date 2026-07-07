
/**
 * 脚本类型：NPC
 * 对象 ID：1100000
 * 功能描述：NPC脚本（ID: 1100000）。
 */
function start() {
    cm.getPlayer().getStorage().sendStorage(cm.getClient(), 1100000);
    cm.dispose();
}