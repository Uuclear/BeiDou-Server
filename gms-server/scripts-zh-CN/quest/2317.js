


/**
 * 脚本类型：任务
 * 对象 ID：2317
 * 功能描述：Scarrs，任务相关对话与奖励。
 */
var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.sendOk("要想穿过结界，必须要有紫色毒蘑菇盖。如果你改变了主意，请随时来找我。");
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendAcceptDecline("啊！#b奇拉蘑菇孢子#k嘛，小时候我好像在书里看到过。想起来了。那是用紫色毒蘑菇中提取的强力毒素制成的，所以需要紫色毒蘑菇盖。如果你能帮我搜集紫色毒蘑菇盖，我就能帮你制作。");
    } else if (status == 1) {
        qm.forceStartQuest();
        qm.sendOk("请你去打猎#b紫色毒蘑菇#k，搜集100个#b紫色毒蘑菇盖#k。");
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
        qm.sendOk("100个紫色毒蘑菇盖全部搜集到了吗？");
    } else if (status == 1) {
        qm.sendOk("紫色毒蘑菇盖很难搜集到，没想到你这么快就搜集到了100个。这样应该就能制作出#b奇拉蘑菇孢子#k了。");
    } else if (status == 2) {
        qm.forceCompleteQuest();
        qm.gainExp(13500);
        qm.gainItem(4000500, -100);
        qm.dispose();
    }
}
	
