
/**
 * 脚本类型：传送门
 * 对象 ID：aranTutorOut2
 * 功能描述：传送门脚本，将玩家传送至目标地图。
 */
function enter(pi) {
    //lol nexon does this xD
    pi.teachSkill(20000014, 0, -1, -1);
    pi.teachSkill(20000015, 0, -1, -1);
    //nexon sends updatePlayerStats Stat.AVAILABLESP 0
    pi.teachSkill(20000014, 1, 0, -1);
    pi.teachSkill(20000015, 1, 0, -1);
    //actually nexon does enableActions here :P
    pi.playPortalSound();
    pi.warp(914000210, 1);
    return true;
}