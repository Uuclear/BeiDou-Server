package org.gms.exception;

/**
 * 功能未启用异常，当服务器常量中对应功能开关关闭时抛出。
 */
public class NotEnabledException extends RuntimeException {

    /**
     * 构造 NotEnabledException。
     */
    public NotEnabledException() {
        super("Feature not enabled, please enable the feature in ServerConstant");
    }

    /**
     * 构造 NotEnabledException。
     *
     * @param message 消息文本
     */
    public NotEnabledException(String message) {
        super(message);
    }
}
