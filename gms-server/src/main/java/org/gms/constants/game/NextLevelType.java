package org.gms.constants.game;

import lombok.Getter;

/**
 * NPC对话下一级类型枚举
 * <p>
 * 定义与NPC对话时各种对话窗口的类型，用于控制对话流程和交互方式。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Getter
public enum NextLevelType {

    /**
     * 发送下一页对话（带Next按钮）
     */
    SEND_NEXT("sendNextLevel"),

    /**
     * 发送最后一页对话（带Prev/Next按钮）
     */
    SEND_LAST("sendLastLevel"),

    /**
     * 发送最后下一页对话
     */
    SEND_LAST_NEXT("sendLastNextLevel"),

    /**
     * 发送OK确认对话框
     */
    SEND_OK("sendOkLevel"),

    /**
     * 发送选择对话框
     */
    SEND_SELECT("sendSelectLevel"),

    /**
     * 发送下一页选择对话框
     */
    SEND_NEXT_SELECT("sendNextSelectLevel"),

    /**
     * 获取数字输入对话框
     */
    GET_INPUT_NUMBER("getInputNumberLevel"),

    /**
     * 获取文本输入对话框
     */
    GET_INPUT_TEXT("getInputTextLevel"),

    /**
     * 发送接受/拒绝对话框
     */
    SEND_ACCEPT_DECLINE("sendAcceptDeclineLevel"),

    /**
     * 发送是/否对话框
     */
    SEND_YES_NO("sendYesNoLevel"),
    ;

    /**
     * 类型字符串标识
     */
    private final String type;

    /**
     * 构造函数
     *
     * @param type 类型字符串标识
     */
    NextLevelType(String type) {
        this.type = type;
    }
}
