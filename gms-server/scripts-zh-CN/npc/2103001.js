
/**
 * 脚本类型：NPC
 * 对象 ID：2103001
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (cm.isQuestStarted(3927)) {
        cm.sendNext("如果我有一把铁锤和一把匕首，一张弓和一支箭……");
        cm.setQuestProgress(3927, 1);
    }

    cm.dispose();
}