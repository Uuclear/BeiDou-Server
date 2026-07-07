
/**
 * 脚本类型：地图
 * 对象 ID：rienArrow
 * 功能描述：(21019, "miss=o;helper=clear")) {（原版描述）。
 */
function start(ms) {
    if (ms.containsAreaInfo(21019, "miss=o;helper=clear")) {
        ms.updateAreaInfo(21019, "miss=o;arr=o;helper=clear");
        ms.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
    }
}