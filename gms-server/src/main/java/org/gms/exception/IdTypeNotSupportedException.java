package org.gms.exception;

/**
 * 不支持的 ID 类型异常，用于标识解析或转换时遇到未知 ID 分类。
 */
public class IdTypeNotSupportedException extends Exception {
    /**
     * 构造 IdTypeNotSupportedException。
     */
    public IdTypeNotSupportedException() {
        super("The given ID type is not supported");
    }

    /**
     * 构造 IdTypeNotSupportedException。
     *
     * @param message 消息文本
     */
    public IdTypeNotSupportedException(String message) {
        super(message);
    }
}
