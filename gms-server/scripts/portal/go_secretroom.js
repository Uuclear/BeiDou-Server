
/**
 * 脚本类型：传送门
 * 对象 ID：go_secretroom
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    if (!pi.isQuestCompleted(2335) && !(pi.isQuestStarted(2335) && pi.hasItem(4032405))) {
        pi.getPlayer().message("The door is locked securely. I will need a key if I want to go in there.");
        return false;
    }

    if (pi.isQuestStarted(2335)) {
        pi.forceCompleteQuest(2335, 1300002);
        pi.giveCharacterExp(5000, pi.getPlayer());
        pi.gainItem(4032405, -1);
    }
    pi.playPortalSound();
    pi.warp(106021001, 1);
    return true;
}