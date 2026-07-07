
/**
 * 脚本类型：NPC
 * 对象 ID：2101003
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
/* 	Ardin
	Ariant	
*/


function start() {
    cm.sendNext("Hey hey, don't try to start trouble with anyone. I want nothing to do with you.");
}

function action() {
    cm.dispose();
}