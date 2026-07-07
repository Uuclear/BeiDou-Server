
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
        cm.sendNext("If I had an iron hammer and a dagger, a bow and an arrow...");
        cm.setQuestProgress(3927, 1);
    }

    cm.dispose();
}