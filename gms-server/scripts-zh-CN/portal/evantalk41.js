
/**
 * 脚本类型：传送门
 * 对象 ID：evantalk41
 * 功能描述：(22014, "mo41=o")) {（原版描述）。
 */
//Author: kevintjuh93

function enter(pi) {
    pi.blockPortal();
    if (pi.containsAreaInfo(22014, "mo41=o")) {
        return false;
    }
    pi.updateAreaInfo(22014, "mo30=o;mo40=o;mo41=o");
    pi.showInfo("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon41");
    return true;
}  