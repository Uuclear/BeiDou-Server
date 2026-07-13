package org.gms.model.pojo;

import lombok.Data;
import org.gms.constants.game.NextLevelType;

/**
 * 下一级上下文实体类
 * 用于存储等级提升或权限升级时的上下文信息
 */
@Data
public class NextLevelContext {
    /**
     * 等级类型
     */
    private NextLevelType levelType;

    /**
     * 上一等级标识
     */
    private String lastLevel;

    /**
     * 下一等级标识
     */
    private String nextLevel;

    /**
     * 前缀标识
     */
    private String prefix;

    /**
     * 清空所有上下文信息
     */
    public void clear() {
        this.levelType = null;
        this.lastLevel = null;
        this.nextLevel = null;
        this.prefix = null;
    }
}
