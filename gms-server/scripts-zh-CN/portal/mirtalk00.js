
/**
 * 脚本类型：传送门
 * 对象 ID：mirtalk00
 * 功能描述：(22013, "dt00=o")) {（原版描述）。
 */
//Author: kevintjuh93

function enter(pi) {
    pi.blockPortal();
    if (pi.containsAreaInfo(22013, "dt00=o")) {
        return false;
    }
    pi.mapEffect("evan/dragonTalk00");
    pi.updateAreaInfo(22013, "dt00=o;mo00=o");
    return true;
}  