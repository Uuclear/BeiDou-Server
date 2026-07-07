


/**
 * 脚本类型：任务
 * 对象 ID：8223
 * 功能描述：Lukan，任务相关对话与奖励。
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
        qm.sendAcceptDecline("Oh, Jack sent you here? Good timing, I'm planning alongside Jack and others to storm the Keep and retake it from the Twisted Masters what is ours by right. You seem ready to fight alongside us, right?");
    } else if (status == 1) {
        qm.sendOk("Great! Your mission now is to rack down some numbers of their army and weaken their defenses by all effects. Defeat 75 of each: Windraider, Firebrand and Nightshadow, then return to me to report.");
        qm.forceStartQuest();
    } else if (status == 2) {
        qm.dispose();
    }
}
