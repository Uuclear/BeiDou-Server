
/**
 * 脚本类型：地图
 * 对象 ID：Massacre_result
 * 功能描述：地图脚本（Massacre_result），控制地图内特殊逻辑。
 */
function start(ms) {
    var py = ms.getPyramid();
    if (py != null) {
        py.sendScore(ms.getPlayer());
    }
}