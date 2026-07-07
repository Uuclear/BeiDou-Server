
/**
 * 脚本类型：任务
 * 对象 ID：unidentifiedQuest
 * 功能描述：任务脚本（ID: unidentifiedQuest）。
 */
function start() {
	qm.getPlayer().dropMessage("Quest: " + qm.getQuest() + " 无法找到，请联系管理员.");
	qm.dispose();
}