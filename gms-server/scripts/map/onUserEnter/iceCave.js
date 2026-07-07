
/**
 * 脚本类型：地图
 * 对象 ID：iceCave
 * 功能描述：地图脚本（iceCave），控制地图内特殊逻辑。
 */
function start(ms) {
    ms.teachSkill(20000014, -1, 0, -1);
    ms.teachSkill(20000015, -1, 0, -1);
    ms.teachSkill(20000016, -1, 0, -1);
    ms.teachSkill(20000017, -1, 0, -1);
    ms.teachSkill(20000018, -1, 0, -1);
    ms.unlockUI();
    ms.showIntro("Effect/Direction1.img/aranTutorial/ClickLilin");
}