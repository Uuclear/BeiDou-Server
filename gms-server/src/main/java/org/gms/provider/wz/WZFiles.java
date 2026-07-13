package org.gms.provider.wz;

import org.gms.manager.ServerManager;
import org.gms.property.ServiceProperty;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * WZ文件枚举，定义了冒险岛服务端使用的所有WZ资源文件类型。
 * 每个枚举项对应一个.wz资源目录，包含游戏数据如任务、物品、地图、怪物、NPC、技能等。
 * 支持多语言资源，自动根据配置选择对应语言的WZ目录。
 *
 * @author OdinMS Team
 */
public enum WZFiles {
    /** 任务数据文件 */
    QUEST("Quest"),
    /** 杂项数据文件 */
    ETC("Etc"),
    /** 物品数据文件 */
    ITEM("Item"),
    /** 角色相关数据文件 */
    CHARACTER("Character"),
    /** 字符串数据文件（包含各种名称文本） */
    STRING("String"),
    /** 列表数据文件 */
    LIST("List"),
    /** 怪物数据文件 */
    MOB("Mob"),
    /** 地图数据文件 */
    MAP("Map"),
    /** NPC数据文件 */
    NPC("Npc"),
    /** 反应堆（触发物体）数据文件 */
    REACTOR("Reactor"),
    /** 技能数据文件 */
    SKILL("Skill"),
    /** 音效数据文件 */
    SOUND("Sound"),
    /** 用户界面数据文件 */
    UI("UI");

    /** WZ文件名（带.wz后缀） */
    private final String fileName;
    /** WZ根目录名称 */
    public static final String DIRECTORY = "wz";

    /**
     * 构造WZ文件枚举
     * @param name WZ目录基础名称（不含.wz后缀）
     */
    WZFiles(String name) {
        this.fileName = name + ".wz";
    }

    /**
     * 获取WZ文件路径，优先使用语言特定目录，不存在则回退到基础目录
     * @return WZ目录路径
     */
    public Path getFile() {
        Path langPath = getLanguageFile();

        // 兼容旧调用：语言目录存在时优先使用，否则使用原始 WZ。
        return Files.exists(langPath) ? langPath : getBaseFile();
    }

    /**
     * 获取基础WZ文件路径（原始未翻译版本）
     * @return 基础WZ目录路径
     */
    public Path getBaseFile() {
        return Path.of(DIRECTORY, fileName);
    }

    /**
     * 获取当前语言的WZ文件路径（如wz-zh-CN/xxx.wz）
     * @return 语言特定WZ目录路径
     */
    public Path getLanguageFile() {
        ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);
        return Path.of(DIRECTORY + "-" + serviceProperty.getLanguage(), fileName);
    }

    /**
     * 获取WZ文件路径的字符串表示
     * @return 文件路径字符串
     */
    public String getFilePath() {
        return getFile().toString();
    }
}
