
/**
 * 脚本类型：NPC
 * 对象 ID：9201100
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
/**
 *9201100 - Taggrin
 *@author Ronan
 */

function start() {
    if (cm.getQuestStatus(8224) == 2) {
        cm.sendOk("Well met, fellow clan member. If you need anything we can be of help, try talking to one of our members.");
    } else {
        cm.sendOk("Hello there, stranger. We are the renowned Raven Claw clan of mercenaries, and I'm their leader.");
    }

    cm.dispose();
}
