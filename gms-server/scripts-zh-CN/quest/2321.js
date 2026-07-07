


/**
 * 脚本类型：任务
 * 对象 ID：2321
 * 功能描述：Minister of Magic，任务相关对话与奖励。
 */
var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.sendOk("真是个不听话的孩子。如果你改变了主意，可以再来找我。"); //你的脑袋没问题吧？我不是让你去找#b内务大臣#k了吗？
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendAcceptDecline("我们也得开始准备了。还要把这件事告诉#p1300005#。我先告诉#p1300005#后再跟你说吧。");
    } else if (status == 1) {
        qm.forceStartQuest();
        qm.sendOk("祝你好运。");
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
        qm.sendOk("我一直听闻勇士你的丰功伟绩，据说你用一种叫#b奇拉蘑菇孢子#k的神秘药物打破了树林的结界？真是了不起啊！");
    } else if (status == 1) {
        qm.forceCompleteQuest();
        qm.gainExp(2500);
        qm.sendOk("接下去的问题，就是怎么进入蘑菇城了。");
    } else if (status == 2) {
        qm.dispose();
    }
}
	
