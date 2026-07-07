
/**
 * 脚本类型：任务
 * 对象 ID：2332
 * 功能描述：任务脚本（ID: 2332）。
 */
/*
	QUEST: Where's Violetta?
	NPC: none
*/

var status = -1;

function start(mode, type, selection) {
    if (qm.hasItem(4032388) && !qm.isQuestStarted(2332)) {
        qm.forceStartQuest();
        qm.getPlayer().showHint("我必须找到碧欧蕾塔公主！ (#b任务开始#k)");
    }
    qm.dispose();
}

function end(mode, type, selection) {
}
