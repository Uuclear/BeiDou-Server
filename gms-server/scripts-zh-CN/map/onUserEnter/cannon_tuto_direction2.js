
/**
 * 脚本类型：地图
 * 对象 ID：cannon_tuto_direction2
 * 功能描述：地图脚本（cannon_tuto_direction2），控制地图内特殊逻辑。
 */
function start(ms) {
    ms.playSound("cannonshooter/bang");
    ms.setDirectionStatus(true);
    ms.showIntro("Effect/Direction4.img/cannonshooter/Scene01");
    ms.showIntro("Effect/Direction4.img/cannonshooter/out02");
}