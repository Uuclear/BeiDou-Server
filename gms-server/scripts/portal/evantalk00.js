
/**
 * 脚本类型：传送门
 * 对象 ID：evantalk00
 * 功能描述：(22013, "mo00=o")) {（原版描述）。
 */
//Author: kevintjuh93

function enter(pi) {
    pi.blockPortal();
    if (pi.containsAreaInfo(22013, "mo00=o")) {
        return false;
    }
    pi.updateAreaInfo(22013, "mo00=o");
    pi.showInfo("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon00");
    return true;
}  