


/**
 * 脚本类型：任务
 * 对象 ID：8231
 * 功能描述：Lita Lawless，任务相关对话与奖励。
 */
var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.sendOk("好吧，那就这样。回头见。");
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        var target = "小妖精";
        qm.sendAcceptDecline("嘿，旅行者！我需要你的帮助。新叶城的居民正面临新的威胁。我正在招募愿意出手的人，这次的目标是 #r" + target + "#k。你愿意加入吗？");
    } else if (status == 1) {
        var reqs = "#r30 #t4032031##k";
        qm.sendOk("很好。请尽快把 " + reqs + " 带回来给我。新叶城就指望你了。");
        qm.forceStartQuest();
    } else if (status == 2) {
        qm.dispose();
    }
}
