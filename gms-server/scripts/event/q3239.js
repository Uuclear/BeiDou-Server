
/**
 * 脚本类型：事件
 * 对象 ID：q3239
 * 功能描述：游戏事件脚本（q3239），管理实例生命周期与玩家进出。
 */
var entryMap;
var exitMap;
var eventLength = 20;

function init() {
    em.setProperty("noEntry", "false");
    entryMap = em.getChannelServer().getMapFactory().getMap(922000000);
    exitMap = em.getChannelServer().getMapFactory().getMap(922000009);
}

function setup(level, lobbyid) {
    var eim = em.newInstance("q3239_" + lobbyid);
    eim.setExclusiveItems([4031092]);
    return eim;
}

function playerEntry(eim, player) {
    var im = eim.getInstanceMap(entryMap.getId());

    // Reset instance
    im.clearDrops();
    im.resetReactors();
    im.shuffleReactors();

    // Start timer
    eim.startEventTimer(eventLength * 60 * 1000);

    // Warp player and mark event as occupied
    player.changeMap(entryMap, 0);
    em.setProperty("noEntry", "true");
}

function changedMap(eim, player, mapid) {
    if (mapid != entryMap.getId())
        playerExit(eim, player);
}

function playerExit(eim, player) {
    end(eim);
}

function playerDisconnected(eim, player) {
    end(eim);
}

function scheduledTimeout(eim) {
    end(eim);
}

function end(eim) {
    var party = eim.getPlayers(); // should only ever be one player
    for (var i = 0; i < party.size(); i++) {
        var player = party.get(i);
        eim.unregisterPlayer(player);
        player.changeMap(exitMap);
    }

    eim.dispose();
    em.setProperty("noEntry", "false");
}

// Stub/filler functions

function disbandParty(eim, player) {}
function afterSetup(eim) {}
function playerUnregistered(eim, player) {}
function changedLeader(eim, leader) {}
function leftParty(eim, player) {}
function clearPQ(eim) {}
function dispose() {}
function cancelSchedule() {}
function allMonstersDead(eim) {}
function monsterValue(eim, mobId) {}
function monsterKilled(mob, eim) {}
