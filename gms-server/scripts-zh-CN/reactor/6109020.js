
/**
 * 脚本类型：反应堆
 * 对象 ID：6109020
 * 功能描述：地图反应堆交互脚本。
 */
function act() {
    var eim = rm.getEventInstance();
    if (eim != null) {
        eim.dropMessage(6, "海盗印记已被激活！");
        eim.setIntProperty("glpq4", eim.getIntProperty("glpq4") + 1);
        if (eim.getIntProperty("glpq4") == 5) { //5个全部完成
            eim.dropMessage(6, "安特利昂已为你开启下一传送门！前进吧！");

            eim.showClearEffect(610030400, "4pt", 2);
            eim.giveEventPlayersStageReward(4);
        }
    }
}