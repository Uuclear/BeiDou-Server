
/**
 * 脚本类型：任务
 * 对象 ID：2197
 * 功能描述：任务对话脚本，推进任务剧情与分支选择。
 */
var status = -1;

function start(mode, type, selection) {
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
            qm.sendNext("哦，你已经有怪物书了。祝你旅途好运~!");
        } else if (status == 1) {
            qm.forceCompleteQuest();
            qm.dispose();
        }
    }
}

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
            qm.sendNext("哦，你已经有怪物书了。祝你旅途好运~!");
        } else if (status == 1) {
            qm.forceCompleteQuest();
            qm.dispose();
        }
    }
}
