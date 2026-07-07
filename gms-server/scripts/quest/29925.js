
/**
 * 脚本类型：任务
 * 对象 ID：29925
 * 功能描述：任务脚本，完成任务后发放物品奖励。
 */
var status = -1;

function start(mode, type, selection) {
    if (qm.canHold(1142130) && !qm.haveItem(1142130, 1) && qm.getPlayer().getLevel() >= 30 && ((qm.getPlayer().getJob() / 100) | 0) == 21) {
        qm.gainItem(1142130, 1);
        qm.forceStartQuest();
        qm.forceCompleteQuest();
    }
    qm.dispose();
}

function end(mode, type, selection) {
    if (qm.canHold(1142130) && !qm.haveItem(1142130, 1) && qm.getPlayer().getLevel() >= 30 && ((qm.getPlayer().getJob() / 100) | 0) == 21) {
        qm.gainItem(1142130, 1);
        qm.forceStartQuest();
        qm.forceCompleteQuest();
    }
    qm.dispose();
}