


/**
 * 脚本类型：任务
 * 对象 ID：8225
 * 功能描述：Taggrin，任务相关对话与奖励。
 */
var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.sendOk("好的，那么。再见了。");
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendAcceptDecline("嘿，伙伴。现在你成为了渡鸦之爪团队的一员，我有一个任务给你。你准备好了吗？");
    } else if (status == 1) {
        qm.sendOk("非常好。为了证明你在我们队伍中的价值，你必须先通过一个小挑战：你必须能够在这里移动得非常出色，了解这片森林所隐藏的所有秘密。追踪一下#b射手村的地图#k，然后来找我谈谈。我会评估你是否值得加入我们。");
        qm.forceStartQuest();
    } else if (status == 2) {
        qm.dispose();
    }
}
