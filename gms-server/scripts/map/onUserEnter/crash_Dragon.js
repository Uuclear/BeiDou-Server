
/**
 * 脚本类型：地图
 * 对象 ID：crash_Dragon
 * 功能描述：地图脚本（crash_Dragon），控制地图内特殊逻辑。
 */
function start(ms) {
    ms.lockUI();
    ms.showIntro("Effect/Direction4.img/crash/Scene" + ms.getPlayer().getGender());
}