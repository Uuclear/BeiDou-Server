
/**
 * 脚本类型：任务
 * 对象 ID：29924
 * 功能描述：任务脚本，完成任务后发放物品奖励。
 */
var status = -1;

function start(mode, type, selection) {
    if (qm.getPlayer().getLevel() >= 10 && ((qm.getPlayer().getJob().getId() / 100) | 0) == 21) {
        if (!qm.haveItem(1142129)) {
            if (qm.canHold(1142129)) {
                qm.gainItem(1142129, 1);
            } else {
                qm.dispose();
                return;
            }
        }

        var medalname = qm.getMedalName();
        qm.message("<" + medalname + "> has been awarded.");
        qm.earnTitle("<" + medalname + "> has been awarded.");

        qm.forceStartQuest();
        qm.forceCompleteQuest();
    }

    qm.dispose();
}

function end(mode, type, selection) {
    if (qm.getPlayer().getLevel() >= 10 && ((qm.getPlayer().getJob().getId() / 100) | 0) == 21) {
        if (!qm.haveItem(1142129)) {
            if (qm.canHold(1142129)) {
                qm.gainItem(1142129, 1);
            } else {
                qm.dispose();
                return;
            }
        }

        var medalname = qm.getMedalName();
        qm.message("<" + medalname + "> has been awarded.");
        qm.earnTitle("<" + medalname + "> has been awarded.");

        qm.forceStartQuest();
        qm.forceCompleteQuest();
    }

    qm.dispose();
}