package org.gms.exception;

/**
 * ID类型不支持异常
 * <p>
 * 当使用了不支持的ID类型时抛出此异常。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public class IdTypeNotSupportedException extends Exception {

    /**
     * 默认构造函数，使用默认错误消息
     */
    public IdTypeNotSupportedException() {
        super("The given ID type is not supported");
    }

    /**
     * 构造函数，自定义错误消息
     *
     * @param message 错误消息
     */
    public IdTypeNotSupportedException(String message) {
        super(message);
    }
}
