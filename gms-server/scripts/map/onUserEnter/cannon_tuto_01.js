
/**
 * 脚本类型：地图
 * 对象 ID：cannon_tuto_01
 * 功能描述：地图脚本（cannon_tuto_01），控制地图内特殊逻辑。
 */
function start(ms) {
    ms.setDirection(0);
    ms.setDirectionStatus(true);
    ms.lockUI2();
    ms.startDirection("cannon_tuto_02");
}