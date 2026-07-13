package org.gms.exception;

/**
 * 功能未启用异常
 * <p>
 * 当尝试使用未在ServerConstant中启用的功能时抛出此异常。
 * 这是一个运行时异常，不需要显式捕获。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public class NotEnabledException extends RuntimeException {

    /**
     * 默认构造函数，使用默认错误消息
     */
    public NotEnabledException() {
        super("Feature not enabled, please enable the feature in ServerConstant");
    }

    /**
     * 构造函数，自定义错误消息
     *
     * @param message 错误消息
     */
    public NotEnabledException(String message) {
        super(message);
    }
}
