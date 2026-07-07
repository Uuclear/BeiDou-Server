package org.gms.util;

import org.gms.client.Client;
import org.gms.constants.string.CharsetConstants;
import org.gms.manager.ServerManager;
import org.gms.property.ServiceProperty;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * 国际化（i18n）消息工具类，封装 Spring {@link MessageSource} 的多资源文件读取。
 * <p>
 * messageSource.getMessage 底层通过循环遍历文件名去读取的，
 * 因此将不同文件名定义为不同的 Bean，扫描时可少扫描其他文件，直接定位目标资源以节约时间。
 * <p>
 * 提供三类消息源：通用 UI 文案（messageSource）、日志模板（logSource）、异常消息（exceptionSource）。
 */
public class I18nUtil {
    public static final Locale LANGUAGE = Locale.forLanguageTag(ServerManager.getApplicationContext().getBean(ServiceProperty.class).getLanguage());
    public static final MessageSource messageSource = ServerManager.getApplicationContext().getBean("messageSource", MessageSource.class);
    public static final MessageSource logSource = ServerManager.getApplicationContext().getBean("logSource", MessageSource.class);
    public static final MessageSource exceptionSource = ServerManager.getApplicationContext().getBean("exceptionSource", MessageSource.class);

    /**
     * 获取通用 UI 文案；若当前线程绑定了客户端，则使用客户端语言，否则使用服务端默认语言。
     *
     * @param code 消息键
     * @param args 占位参数（会转为字符串以避免千分符等问题）
     * @return 本地化后的消息文本
     */
    public static String getMessage(String code, Object... args) {
        // 如果当前存在客户端请求，则以客户端的语言为准。如果当前非客户端请求，是服务端主动发给客户端的，则以服务端语言为准
        Locale clientLang = CharsetConstants.getLanguageLocale(ThreadLocalUtil.getClientLang());
        // 确保所有参数转为字符串，包括数字类型（避免千分符问题）
        String[] stringArgs = Arrays.stream(args)
                .map(String::valueOf)
                .toArray(String[]::new);
        return messageSource.getMessage(code, stringArgs, clientLang);
    }

    /**
     * 按指定 {@link Locale} 获取通用 UI 文案。
     *
     * @param locale 目标语言区域
     * @param code   消息键
     * @param args   占位参数
     * @return 本地化后的消息文本
     */
    public static String getMessage(Locale locale, String code, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }

    /**
     * 获取日志模板消息（使用服务端默认语言），占位符格式为 {@code {0}}、{@code {1}} 等。
     *
     * @param code 消息键
     * @param args 占位参数
     * @return 组合后的日志消息
     */
    public static String getLogMessage(String code, Object... args) {
        return logSource.getMessage(code, args, LANGUAGE);
    }

    /**
     * 按指定 {@link Locale} 获取日志模板消息。
     *
     * @param locale 目标语言区域
     * @param code   消息键
     * @param args   占位参数
     * @return 组合后的日志消息
     */
    public static String getLogMessage(Locale locale, String code, Object... args) {
        return logSource.getMessage(code, args, locale);
    }

    /**
     * 获取异常消息（使用服务端默认语言）。
     *
     * @param code 消息键
     * @param args 占位参数
     * @return 本地化后的异常描述
     */
    public static String getExceptionMessage(String code, Object... args) {
        return exceptionSource.getMessage(code, args, LANGUAGE);
    }

    /**
     * 按指定 {@link Locale} 获取异常消息。
     *
     * @param locale 目标语言区域
     * @param code   消息键
     * @param args   占位参数
     * @return 本地化后的异常描述
     */
    public static String getExceptionMessage(Locale locale, String code, Object... args) {
        return exceptionSource.getMessage(code, args, locale);
    }
}
