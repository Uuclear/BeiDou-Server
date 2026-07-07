
/**
 * 脚本类型：传送门
 * 对象 ID：evantalk50
 * 功能描述：(22014, "mo50=o")) {（原版描述）。
 */
//Author: kevintjuh93

function enter(pi) {
    pi.blockPortal();
    if (pi.containsAreaInfo(22014, "mo50=o")) {
        return false;
    }
    pi.updateAreaInfo(22014, "mo30=o;mo40=o;mo41=o;mo50=o;mo42=o");
    pi.showInfo("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon50");
    return true;
}  