package org.gms.constants.game;

/**
 * 延迟任务更新类型枚举
 * <p>
 * 定义任务（Quest）的延迟更新操作类型。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public enum DelayedQuestUpdate {
    /**
     * 更新任务状态
     */
    UPDATE,

    /**
     * 放弃任务
     */
    FORFEIT,

    /**
     * 完成任务
     */
    COMPLETE,

    /**
     * 任务信息更新
     */
    INFO
}
