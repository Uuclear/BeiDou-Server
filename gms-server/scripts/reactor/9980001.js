
/**
 * 脚本类型：反应堆
 * 对象 ID：9980001
 * 功能描述：地图反应堆交互脚本。
 */
function act() {
    rm.dispelAllMonsters(parseInt(rm.getReactor().getName().substring(1, 2)), parseInt(rm.getReactor().getName().substring(0, 1)));
}