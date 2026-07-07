


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
            qm.sendOk("You don't seem to follow instructions well. Come see me when you are ready.");
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendAcceptDecline("Now you'll be able to penetrate the spiny vine barrier of Mushroom Forest, but before that, #bMinister of Home Affairs#k wants to have a word with you. Please go see him immediately.");
    } else if (status == 1) {
        qm.forceStartQuest();
        qm.sendOk("Good luck.");
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
        qm.sendOk("I have been keeping up on your fabulour work. I am aware that you have successfully created the #bKiller Mushroom Spores#k, which penetrates through the unpenetrable barrier of the forest. Congratulations!");
    } else if (status == 1) {
        qm.forceCompleteQuest();
        qm.gainExp(2500);
        qm.sendOk("The problem now is to figure out how to enter the castle.");
    } else if (status == 2) {
        qm.dispose();
    }
}
	