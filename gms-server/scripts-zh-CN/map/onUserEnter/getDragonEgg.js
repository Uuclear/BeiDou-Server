
/**
 * 脚本类型：地图
 * 对象 ID：getDragonEgg
 * 功能描述：地图脚本（getDragonEgg），控制地图内特殊逻辑。
 */
function start(ms) {
    ms.lockUI();
    ms.showIntro("Effect/Direction4.img/getDragonEgg/Scene" + ms.getPlayer().getGender());
}