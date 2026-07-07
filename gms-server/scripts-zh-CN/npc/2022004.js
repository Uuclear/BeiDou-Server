
/**
 * 脚本类型：NPC
 * 对象 ID：2022004
 * 功能描述：NPC脚本，提供地图传送。
 */
function start() {
    cm.sendNext("你在那里做得很好，" + cm.getPlayer().getName() + "，干得漂亮。现在我会把你送回埃尔奈斯。当你准备好学习新技能时，带着护身符和我交谈。");
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        cm.warp(211000000, "in01");
        cm.dispose();
    }
}