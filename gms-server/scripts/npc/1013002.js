
/**
 * 脚本类型：NPC
 * 对象 ID：1013002
 * 功能描述：Message（原版描述）。
 */
function start() {
    cm.forceCompleteQuest(22011);
    cm.playerMessage(5, "You have acquired a Dragon Egg.");//actually getInfoMessage
    cm.warp(900090103, 0);
    cm.dispose();
}