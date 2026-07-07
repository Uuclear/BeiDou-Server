


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
            qm.sendOk("没有时间了。请你快一点。");
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendAcceptDecline("我已经把你的事情跟我们的#b内务大臣#k说了。请你去见见#b内务大臣#k。");
    } else if (status == 1) {
        qm.forceStartQuest();
        qm.sendOk("请你一定要拯救我们王国！");
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
