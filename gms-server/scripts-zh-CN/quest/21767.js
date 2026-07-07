
/**
 * 脚本类型：任务
 * 对象 ID：21767
 * 功能描述：任务脚本，完成任务后发放物品奖励。
 */
var status = -1;
var canStart;

function start(mode, type, selection) {
	status++;
	if (status == 0) {
                if(qm.haveItem(4032423, 1)) {
                        qm.forceStartQuest();
                        qm.dispose();
                        return;
                }
                
                canStart = qm.canHold(4032423, 1);
                if(!canStart) {
                        qm.sendNext("请确认你的背包里是否还有空位.");
                        return;
                }
            
		qm.sendNext("#b嗯，盒子里有一种药材。这可能是什么？你最好把这个带给约翰，问他是什么。#k");
	} else if (status == 1) {
                if(canStart) {
                        qm.gainItem(4032423,1);
                        qm.forceStartQuest();
                }
                
		qm.dispose();
	}
}