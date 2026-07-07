


/**
 * 脚本类型：NPC
 * 对象 ID：1101001
 * 功能描述：Divine Bird，三转职业任务。
 */
function start() {
    const GameConstants = Java.type('org.gms.constants.game.GameConstants');
    if (cm.getPlayer().isCygnus() && GameConstants.getJobBranch(cm.getJob()) > 2) {
        cm.useItem(2022458);
        cm.sendOk("Let me cast you my blessings, my Knight. Please protect the world of Maple....");
    } else {
        cm.sendOk("Don't stop training. Every ounce of your energy is required to protect the world of Maple....");
    }

    cm.dispose();
}