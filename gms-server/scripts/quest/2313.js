


/**
 * 脚本类型：任务
 * 对象 ID：2313
 * 功能描述：Head Patrol Officer，任务相关对话与奖励。
 */
var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.sendOk("There's not much time. Please hurry.");
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendAcceptDecline("I have told our #bMinister of Home Affairs#k of your abilities. Please go pay a visit to him immediately.");
    } else if (status == 1) {
        qm.forceStartQuest();
        qm.sendOk("Save our kingdom! We believe in you!");
    } else if (status == 2) {
        qm.dispose();
    }
}

function end(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.forceCompleteQuest();
        qm.gainExp(4000);
        qm.dispose();
    }
}
