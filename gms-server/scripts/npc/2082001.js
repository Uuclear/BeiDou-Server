
/**
 * 脚本类型：NPC
 * 对象 ID：2082001
 * 功能描述：NPC脚本，提供地图传送、物品发放。
 */
function start() {
    if (cm.haveItem(4031045)) {
        var em = cm.getEventManager("Cabin");
        if (em.getProperty("entry") == "true") {
            cm.sendYesNo("Do you wish to board the flight?");
        } else {
            cm.sendOk("The flight has not arrived yet. Come back soon.");
            cm.dispose();
        }
    } else {
        cm.sendOk("Make sure you got an Orbis ticket to travel in this flight. Check your inventory.");
        cm.dispose();
    }
}

function action(mode, type, selection) {
    if (mode <= 0) {
        cm.sendOk("Okay, talk to me if you change your mind!");
        cm.dispose();
        return;
    }
    var em = cm.getEventManager("Cabin");
    if (em.getProperty("entry") == "true") {
        cm.warp(240000111);
        cm.gainItem(4031045, -1);
    } else {
        cm.sendOk("The flight has not arrived yet. Come back soon.");
    }
    cm.dispose();
}