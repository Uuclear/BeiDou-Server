
/**
 * 脚本类型：反应堆
 * 对象 ID：6109017
 * 功能描述：地图反应堆交互脚本。
 */
function act() {
    var eim = rm.getEventInstance();
    if (eim != null) {
        eim.dropMessage(6, "The Archer Sigil has been activated!");
        eim.setIntProperty("glpq4", eim.getIntProperty("glpq4") + 1);
        if (eim.getIntProperty("glpq4") == 5) { //all 5 done
            eim.dropMessage(6, "The Antellion grants you access to the next portal! Proceed!");

            eim.showClearEffect(610030400, "4pt", 2);
            eim.giveEventPlayersStageReward(4);
        }
    }
}