
/**
 * 脚本类型：反应堆
 * 对象 ID：3009000
 * 功能描述：地图反应堆交互脚本。
 */
/**
 * @author: Ronan
 * @reactor: Spine
 * @map: 930000200 - Forest of Poison Haze - Deteriorated Forest
 * @func: Water Fountain
 */

function act() {
    if (rm.getReactor().getState() == 4) {
        rm.getEventInstance().showClearEffect(rm.getMap().getId());
    }
}