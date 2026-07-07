package org.gms.provider.wz;

import org.gms.manager.ServerManager;
import org.gms.property.ServiceProperty;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 标准 WZ 包枚举，定义各游戏数据包路径及语言目录回退规则。
 * <p>
 * 原始包位于 {@code wz/}，本地化包位于 {@code wz-语言/}（如 {@code wz-zh-CN/}）。
 * </p>
 */
public enum WZFiles {
    QUEST("Quest"),
    ETC("Etc"),
    ITEM("Item"),
    CHARACTER("Character"),
    STRING("String"),
    LIST("List"),
    MOB("Mob"),
    MAP("Map"),
    NPC("Npc"),
    REACTOR("Reactor"),
    SKILL("Skill"),
    SOUND("Sound"),
    UI("UI");

    private final String fileName;
    public static final String DIRECTORY = "wz";

    WZFiles(String name) {
        this.fileName = name + ".wz";
    }

/** 获取 WZ 文件路径（含语言回退） */
    public Path getFile() {
        Path langPath = getLanguageFile();

        // 兼容旧调用：语言目录存在时优先使用，否则使用原始 WZ。
        return Files.exists(langPath) ? langPath : getBaseFile();
    }

/** 获取默认 WZ 文件路径 */
    public Path getBaseFile() {
        return Path.of(DIRECTORY, fileName);
    }

/** 获取语言目录 WZ 文件路径 */
    public Path getLanguageFile() {
        ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);
        return Path.of(DIRECTORY + "-" + serviceProperty.getLanguage(), fileName);
    }

/** 获取 WZ 文件路径字符串 */
    public String getFilePath() {
        return getFile().toString();
    }
}
