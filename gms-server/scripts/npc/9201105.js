
/**
 * 脚本类型：NPC
 * 对象 ID：9201105
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
/**
 *9201105 - Sage
 *@author Ronan
 */

function start() {
    if (cm.getMapId() == 610020005) {
        cm.sendOk("The Crimsonwood Keep lies right ahead, a great feat has been made by you this day, salute to thee. Pass through these woods to enter the gates of the Keep.");
    } else {
        cm.sendOk("So far your progress is splendid, good job. However, to make it to the Keep, you must face and accomplish this ordeal, carry on.");
    }
    cm.dispose();
}
