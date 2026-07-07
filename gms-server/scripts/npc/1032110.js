
/**
 * 脚本类型：NPC
 * 对象 ID：1032110
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
/*
	NPC: Corner of the Magic Library
	MAP: Hidden Street - Magic Library (910110000)
	QUEST: Maybe it's Grendel! (20718)
*/

var status;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || (mode == 0 && status == 0)) {
        cm.dispose();
        return;
    } else if (mode == 0) {
        status--;
    } else {
        status++;
    }


    if (status == 0) {
        cm.sendOk("Nothing remarkable here.");
    } else if (status == 1) {
        cm.dispose();

    }
}