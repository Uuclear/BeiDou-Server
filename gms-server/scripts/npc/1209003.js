
/**
 * 脚本类型：NPC
 * 对象 ID：1209003
 * 功能描述：NPC对话脚本，与玩家交互并提供相关服务。
 */
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.sendOk("We are departing to #bVictoria Island#k briefly. I've heard the #rBlack Mage#k himself cannot take that place on his grasp yet, thanks to #bthe seal that has been casted on that area#k. We pray for their safety, but if fortune does not favor the Heroes, at least we will be safe once we reach the continent.");
    cm.dispose();
}