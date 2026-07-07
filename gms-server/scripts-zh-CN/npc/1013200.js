
/**
 * 脚本类型：NPC
 * 对象 ID：1013200
 * 功能描述：NPC脚本，提供物品发放。
 */
function start() {
    if (!cm.isQuestStarted(22015)) {
        cm.sendOk("#b(你离小猪太远了。靠近一点抓住它。)");
    } else {
        cm.gainItem(4032449, true);
        cm.forceCompleteQuest(22015);
        cm.playerMessage(5, "你救了小猪。");
    }
    cm.dispose();
}