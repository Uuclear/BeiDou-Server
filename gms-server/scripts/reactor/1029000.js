
/**
 * 脚本类型：反应堆
 * 对象 ID：1029000
 * 功能描述：地图反应堆交互脚本。
 */
function act() {
    if (rm.isAllReactorState(1029000, 0x04)) { // 0x04 appears to be the destroyed state
        rm.killMonster(3230300);
        rm.killMonster(3230301);
        rm.playerMessage(6, "Once the rock crumbled, Jr. Boogie was in great pain and disappeared.");
    }    
}