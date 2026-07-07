
/**
 * 脚本类型：传送门
 * 对象 ID：infoAttack
 * 功能描述：("UI/tutorial.img/20");（原版描述）。
 */
function enter(pi) {
    if (pi.isQuestStarted(1035)) {
        pi.showInfo("UI/tutorial.img/20");
    }

    pi.blockPortal();
    return true;
}