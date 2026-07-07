
/**
 * 脚本类型：传送门
 * 对象 ID：DragonEggNotice
 * 功能描述：(22014, "egg=o")) {（原版描述）。
 */
//Author: kevintjuh93

function enter(pi) {
    pi.blockPortal();
    if (pi.containsAreaInfo(22014, "egg=o")) {
        return false;
    }
    pi.updateAreaInfo(22014, "egg=o;mo30=o;mo40=o;mo41=o;mo50=o;mo42=o;mo60=o");
    pi.sendImage("UI/tutorial/evan/8/0");
    return true;
}  