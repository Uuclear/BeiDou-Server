package org.gms.exception;

/**
 * 错误信息接口
 * <p>
 * 定义错误信息的基础接口，用于统一业务异常和错误枚举的规范。
 * 实现该接口的类需要提供错误码和错误信息。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public interface BaseErrorInfoInterface {

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    Integer getResultCode();

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    String getResultMsg();
}
