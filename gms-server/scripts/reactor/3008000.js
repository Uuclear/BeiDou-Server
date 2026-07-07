
/**
 * 脚本类型：反应堆
 * 对象 ID：3008000
 * 功能描述：地图反应堆交互脚本。
 */
/**
 * @author: Ronan
 * @reactor: Water Fountain
 * @map: 930000800 - Forest of Poison Haze - Outer Forest Exit
 * @func: Water Fountain
 */

function hit() {
    var players = rm.getMap().getAllPlayers().toArray();

    for (var i = 0; i < players.length; i++) {
        rm.giveCharacterExp(52000, players[i]);
    }
}

function act() {} //do nothing