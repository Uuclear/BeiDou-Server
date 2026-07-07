package org.gms.constants.game;

import lombok.Getter;

@Getter
/**
 * 下一等级类型枚举常量，用于升级、转职等流程中的等级变更分类。
 */
public enum NextLevelType {
    SEND_NEXT("sendNextLevel"),
    SEND_LAST("sendLastLevel"),
    SEND_LAST_NEXT("sendLastNextLevel"),
    SEND_OK("sendOkLevel"),
    SEND_SELECT("sendSelectLevel"),
    SEND_NEXT_SELECT("sendNextSelectLevel"),
    GET_INPUT_NUMBER("getInputNumberLevel"),
    GET_INPUT_TEXT("getInputTextLevel"),
    SEND_ACCEPT_DECLINE("sendAcceptDeclineLevel"),
    SEND_YES_NO("sendYesNoLevel"),
    ;

    private final String type;

    NextLevelType(String type) {
        this.type = type;
    }
}
