
/**
 * 脚本类型：传送门
 * 对象 ID：evantalk02
 * 功能描述：(22013, "mo02=o")) {（原版描述）。
 */
//Author: kevintjuh93

function enter(pi) {
    pi.blockPortal();
    if (pi.containsAreaInfo(22013, "mo02=o")) {
        return false;
    }
    pi.updateAreaInfo(22013, "dt00=o;mo00=o;mo01=o;mo02=o");
    pi.showInfo("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon02");
    return true;
}  