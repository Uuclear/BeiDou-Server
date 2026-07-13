package org.gms.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 业务异常类
 * <p>
 * 自定义业务异常，用于封装业务逻辑中的错误信息。
 * 包含错误码和错误信息，支持多种构造方式。
 * 重写fillInStackTrace方法以减少性能开销。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Setter
@Getter
public class BizException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected Integer errorCode;

    /**
     * 错误信息
     */
    protected String errorMsg;

    /**
     * 默认构造函数
     */
    public BizException() {
        super();
    }

    /**
     * 根据错误信息接口构造异常
     *
     * @param errorInfoInterface 错误信息接口实现
     */
    public BizException(BaseErrorInfoInterface errorInfoInterface) {
        super(String.valueOf(errorInfoInterface.getResultCode()));
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    /**
     * 根据错误信息接口和原因构造异常
     *
     * @param errorInfoInterface 错误信息接口实现
     * @param cause              异常原因
     */
    public BizException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(String.valueOf(errorInfoInterface.getResultCode()), cause);
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    /**
     * 根据错误信息构造异常
     *
     * @param errorMsg 错误信息
     */
    public BizException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    /**
     * 根据错误码和错误信息构造异常
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     */
    public BizException(Integer errorCode, String errorMsg) {
        super(String.valueOf(errorCode));
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 根据错误码、错误信息和原因构造异常
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @param cause     异常原因
     */
    public BizException(Integer errorCode, String errorMsg, Throwable cause) {
        super(String.valueOf(errorCode), cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 创建非法参数异常（使用默认错误码）
     *
     * @return BizException实例
     */
    public static BizException illegalArgument() {
        return new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS);
    }

    /**
     * 创建非法参数异常（自定义错误信息）
     *
     * @param errorMsg 自定义错误信息
     * @return BizException实例
     */
    public static BizException illegalArgument(String errorMsg) {
        return new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), errorMsg);
    }

    /**
     * 抛出非法参数异常（使用默认错误码）
     */
    public static void throwIllegalArgument() {
        throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS);
    }

    /**
     * 抛出非法参数异常（自定义错误信息）
     *
     * @param errorMsg 自定义错误信息
     */
    public static void throwIllegalArgument(String errorMsg) {
        throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), errorMsg);
    }

    /**
     * 获取异常消息
     *
     * @return 错误信息
     */
    @Override
    public String getMessage() {
        return errorMsg;
    }

    /**
     * 重写fillInStackTrace方法，不填充堆栈信息以提高性能
     *
     * @return 当前异常实例
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
