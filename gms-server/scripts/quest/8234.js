


/**
 * 脚本类型：任务
 * 对象 ID：8234
 * 功能描述：Lita Lawless，任务相关对话与奖励。
 */
var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.sendOk("Okay, then. See you around.");
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        var target = "are Elderwraiths";
        qm.sendAcceptDecline("Hey, traveler! I need your help. A new threat has appeared to the citizens of the New Leaf City. I'm currently recruiting anyone, and this time's target #r" + target + "#k. Are you in?");
    } else if (status == 1) {
        var reqs = "#r30 #t4032011##k";
        qm.sendOk("Very well. Get me #r" + reqs + "#k, asap. The NLC is counting on you.");
        qm.forceStartQuest();
    } else if (status == 2) {
        qm.dispose();
    }
}