
/**
 * 脚本类型：传送门
 * 对象 ID：evantalk10
 * 功能描述：(22013, "mo10=o")) {（原版描述）。
 */
//Author: kevintjuh93

function enter(pi) {
    pi.blockPortal();
    if (pi.containsAreaInfo(22013, "mo10=o")) {
        return false;
    }
    pi.updateAreaInfo(22013, "dt00=o;mo00=o;mo01=o;mo10=0;mo02=o");
    pi.showInfo("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon10");
    return true;
}  