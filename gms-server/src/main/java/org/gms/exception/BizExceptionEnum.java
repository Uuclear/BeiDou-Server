package org.gms.exception;

import org.gms.util.I18nUtil;

/**
 * 业务异常枚举类
 * <p>
 * 定义系统中常见的业务异常错误码和错误信息，支持国际化。
 * 错误信息通过I18nUtil从国际化资源文件中获取。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public enum BizExceptionEnum implements BaseErrorInfoInterface {

    /**
     * 操作成功
     */
    SUCCESS(20000, I18nUtil.getExceptionMessage("SUCCESS")),

    /**
     * 请求体格式不匹配
     */
    BODY_NOT_MATCH(40000, I18nUtil.getExceptionMessage("BODY_NOT_MATCH")),

    /**
     * 请求方法不支持
     */
    REQUEST_METHOD_SUPPORT(40001, I18nUtil.getExceptionMessage("REQUEST_METHOD_SUPPORT")),

    /**
     * 非法参数
     */
    ILLEGAL_PARAMETERS(40002, I18nUtil.getExceptionMessage("ILLEGAL_PARAMETERS")),

    /**
     * 资源未找到
     */
    NOT_FOUND(40004, I18nUtil.getExceptionMessage("NOT_FOUND")),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(50000, I18nUtil.getExceptionMessage("INTERNAL_SERVER_ERROR")),

    /**
     * 服务器繁忙
     */
    SERVER_BUSY(50003, I18nUtil.getExceptionMessage("SERVER_BUSY"));

    /**
     * 错误码
     */
    private final Integer resultCode;

    /**
     * 错误信息（已国际化）
     */
    private final String resultMsg;

    /**
     * 构造函数
     *
     * @param resultCode 错误码
     * @param resultMsg  错误信息
     */
    BizExceptionEnum(Integer resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    @Override
    public Integer getResultCode() {
        return resultCode;
    }

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
