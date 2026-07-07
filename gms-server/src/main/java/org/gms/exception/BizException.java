package org.gms.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 业务异常基类，携带错误码与国际化错误信息，供服务层主动抛出。
 * 重写 fillInStackTrace 以抑制堆栈填充，减少日志噪音。
 */
@Setter
@Getter
public class BizException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    
    protected Integer errorCode;
    protected String errorMsg;

    /**
     * 构造 BizException。
     */
    public BizException() {
        super();
    }

    /**
     * 构造 BizException。
     *
     * @param errorInfoInterface 错误码枚举
     */
    public BizException(BaseErrorInfoInterface errorInfoInterface) {
        super(String.valueOf(errorInfoInterface.getResultCode()));
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    /**
     * 构造 BizException。
     *
     * @param errorInfoInterface 错误码枚举
     * @param cause 原始异常原因
     */
    public BizException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(String.valueOf(errorInfoInterface.getResultCode()), cause);
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    /**
     * 构造 BizException。
     *
     * @param errorMsg 错误描述
     */
    public BizException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    /**
     * 构造 BizException。
     *
     * @param errorCode 业务错误码
     * @param errorMsg 错误描述
     */
    public BizException(Integer errorCode, String errorMsg) {
        super(String.valueOf(errorCode));
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 构造 BizException。
     *
     * @param errorCode 业务错误码
     * @param errorMsg 错误描述
     * @param cause 原始异常原因
     */
    public BizException(Integer errorCode, String errorMsg, Throwable cause) {
        super(String.valueOf(errorCode), cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 创建参数非法业务异常实例。
     * @return BizException 实例
     */
    public static BizException illegalArgument() {
        return new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS);
    }

    /**
     * 创建参数非法业务异常实例。
     * @return BizException 实例
     */
    public static BizException illegalArgument(String errorMsg) {
        return new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), errorMsg);
    }

    /**
     * 直接抛出参数非法业务异常。
     */
    public static void throwIllegalArgument() {
        // 在这里throw堆栈会多一层
        throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS);
    }

    /**
     * 直接抛出参数非法业务异常。
     */
    public static void throwIllegalArgument(String errorMsg) {
        // 在这里throw堆栈会多一层
        throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), errorMsg);
    }

    /**
     * 执行 getMessage 相关业务逻辑。
     * @return String 类型结果
     */
    public String getMessage() {
        return errorMsg;
    }

    /**
     * 抑制堆栈跟踪填充，减少异常开销与日志噪音。
     * @return 当前异常实例
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
