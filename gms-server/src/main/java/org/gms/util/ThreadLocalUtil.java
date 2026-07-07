package org.gms.util;

import org.gms.client.Client;

import java.util.Optional;

/**
 * 线程本地存储工具，用于在当前处理线程中绑定与获取 {@link Client} 实例。
 * <p>
 * 主要用于国际化、字符集解析等需要感知当前客户端上下文的场景。
 */
public class ThreadLocalUtil {
    private static final ThreadLocal<Client> threadLocal = new ThreadLocal<>();

    /**
     * 将客户端实例绑定到当前线程。
     *
     * @param c 当前请求的客户端
     */
    public static void setCurrentClient(Client c) {
        threadLocal.set(c);
    }

    /**
     * 获取当前线程绑定的客户端实例。
     *
     * @return 客户端实例，未绑定时返回 {@code null}
     */
    public static Client getCurrentClient() {
        return threadLocal.get();
    }

    /**
     * 清除当前线程的客户端绑定，防止线程池复用时泄漏上下文。
     */
    public static void removeCurrentClient() {
        threadLocal.remove();
    }

    /**
     * 获取当前客户端的语言代码；无客户端绑定时返回 {@code 0}。
     *
     * @return 语言代码
     */
    public static int getClientLang() {
        return Optional.ofNullable(threadLocal.get()).map(Client::getLanguage).orElse(0);
    }
}
