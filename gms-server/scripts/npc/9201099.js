
/**
 * 脚本类型：NPC
 * 对象 ID：9201099
 * 功能描述：NPC脚本，提供打开商店。
 */
/**
 *9201098 - Mo
 *@author Ronan
 */

function start() {
    if (cm.getQuestStatus(8224) == 2) {
        cm.openShopNPC(9201099);
    } else {
        cm.sendOk("Hm, at who do you think you are looking at?");
    }

    cm.dispose();
}
