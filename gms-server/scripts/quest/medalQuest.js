
/**
 * 脚本类型：任务
 * 对象 ID：medalQuest
 * 功能描述：任务脚本（ID: medalQuest）。
 */
/**
 *
 * @author Arnah, Ronan
 */

function start(mode, type, selection) {
    qm.forceStartQuest();
    qm.forceCompleteQuest();

    var medalname = qm.getMedalName();
    qm.message("<" + medalname + "> is not coded.");
    qm.earnTitle("<" + medalname + "> has been awarded.");
    qm.dispose();
}

function end(mode, type, selection) {
    qm.forceCompleteQuest();

    var medalname = qm.getMedalName();
    qm.message("<" + medalname + "> is not coded.");
    qm.earnTitle("<" + medalname + "> has been awarded.");
    qm.dispose();
}