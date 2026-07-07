package org.gms.model.pojo;

import lombok.Data;
import org.gms.constants.game.NextLevelType;

/**
 * 文件/配置层级导航上下文 POJO，记录当前浏览的层级类型、上一级与下一级路径前缀，用于配置树遍历。
 */
@Data
public class NextLevelContext {
    private NextLevelType levelType;
    private String lastLevel;
    private String nextLevel;
    private String prefix;

    /**
     * 清空层级类型、路径前缀等全部导航状态。
     */
    public void clear() {
        this.levelType = null;
        this.lastLevel = null;
        this.nextLevel = null;
        this.prefix = null;
    }
}
