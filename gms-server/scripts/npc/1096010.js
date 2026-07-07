
/**
 * 脚本类型：NPC
 * 对象 ID：1096010
 * 功能描述：NPC脚本，提供物品发放。
 */
function start() {
    if (cm.isQuestStarted(2566)) {
        if (!cm.haveItem(4032985)) {
            if (cm.canHold(4032985)) {
                cm.gainItem(4032985, true);
                cm.earnTitle("You found the Ignition Device. Bring it to Cutter.");
            }
        } else {
            cm.earnTitle("You already have the Ignition Device.");
        }
    }
    cm.dispose();
}