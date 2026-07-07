


/**
 * 脚本类型：任务
 * 对象 ID：8223
 * 功能描述：Lukan，任务相关对话与奖励。
 */
var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.sendOk("好的，那就这样吧。再见。");
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendAcceptDecline("哦，杰克派你来的？好时机，我正与杰克和其他人计划突袭堡垒，夺回属于我们的权利。你看起来准备好和我们并肩作战了，对吧？");
    } else if (status == 1) {
        qm.sendOk("太棒了！你现在的任务是削弱敌人的力量，瓦解他们的防御。击败75个风刃袭击者、火焰使者和夜影使者，然后回到我这里汇报。");
        qm.forceStartQuest();
    } else if (status == 2) {
        qm.dispose();
    }
}
