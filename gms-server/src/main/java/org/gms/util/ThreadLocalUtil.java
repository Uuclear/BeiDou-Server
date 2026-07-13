package org.gms.util;

import org.gms.client.Client;

import java.util.Optional;

/**
 * 线程本地变量工具类
 * <p>
 * 使用ThreadLocal存储当前线程的客户端（Client）对象，
 * 用于在请求处理过程中传递客户端上下文信息。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public class ThreadLocalUtil {

    /**
     * 线程本地变量，存储当前线程的客户端对象
     */
    private static final ThreadLocal<Client> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的客户端对象
     *
     * @param c 客户端对象
     */
    public static void setCurrentClient(Client c) {
        threadLocal.set(c);
    }

    /**
     * 获取当前线程的客户端对象
     *
     * @return 当前线程的客户端对象，如果未设置则返回null
     */
    public static Client getCurrentClient() {
        return threadLocal.get();
    }

    /**
     * 移除当前线程的客户端对象
     * <p>
     * 在请求处理完成后应调用此方法，避免内存泄漏。
     * </p>
     */
    public static void removeCurrentClient() {
        threadLocal.remove();
    }

    /**
     * 获取当前客户端的语言设置
     *
     * @return 客户端语言代码，如果客户端不存在则返回0（默认语言）
     */
    public static int getClientLang() {
        return Optional.ofNullable(threadLocal.get()).map(Client::getLanguage).orElse(0);
    }
}
