
/**
 * 脚本类型：传送门
 * 对象 ID：aranTutorOut3
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    //lol nexon does this xD
    pi.teachSkill(20000016, 0, -1, -1);
    //nexon sends updatePlayerStats Stat.AVAILABLESP 0
    pi.teachSkill(20000016, 1, 0, -1);
    //actually nexon does enableActions here :P
    pi.playPortalSound();
    pi.warp(914000220, 1);
    return true;
}