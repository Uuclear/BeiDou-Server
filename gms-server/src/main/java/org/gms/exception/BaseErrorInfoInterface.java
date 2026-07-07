package org.gms.exception;

/**
 * 业务错误信息契约接口，统一错误码与错误消息的获取方式。
 */
public interface BaseErrorInfoInterface {
    Integer getResultCode();
    String getResultMsg();
}
