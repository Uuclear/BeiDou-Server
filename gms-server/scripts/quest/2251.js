


/**
 * 脚本类型：任务
 * 对象 ID：2251
 * 功能描述：任务脚本，完成任务后发放经验与物品奖励。
 * 原作者：Kevin
 */
var status = -1;      // script restored thanks to kvmba

function end(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 0 && type > 0) {
            qm.dispose();
            return;
        }

        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            if (!qm.haveItem(4032399, 20)) {
                qm.sendOk("Please bring me 20 #b#t4032399##k...  #i4032399#");
            } else {
                qm.gainItem(4032399, -20);
                qm.sendOk("Oh, you brought 20 #b#t4032399##k! Thank you.");
                qm.gainExp(8000);
                qm.forceCompleteQuest();
            }
        } else if (status == 1) {
            qm.dispose();
        }
    }
}