
/**
 * 脚本类型：NPC
 * 对象 ID：2101003
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
/* 	Ardin
	Ariant	
*/


function start() {
    cm.sendNext("嘿，嘿，不要试图和任何人闹事。我和你无关。");
}

function action() {
    cm.dispose();
}