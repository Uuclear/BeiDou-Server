package org.gms.util;

import org.gms.constants.string.CharsetConstants;
import org.gms.manager.ServerManager;
import org.gms.property.ServiceProperty;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.Locale;

/**
 * 国际化（I18n）消息工具类
 * <p>
 * 提供统一的国际化消息获取接口，支持三种消息源：
 * <ul>
 *   <li>messageSource - 游戏内消息（发给玩家的消息）</li>
 *   <li>logSource - 日志消息</li>
 *   <li>exceptionSource - 异常消息</li>
 * </ul>
 * messageSource.getMessage底层是通过循环遍历文件名去读取的，
 * 所以将不同文件名定义不同的bean，这样扫描的时候可以少扫描其他文件，直接找到想要对应的文件，节约时间。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public class I18nUtil {

    /**
     * 服务器默认语言区域设置，从服务配置中读取
     */
    public static final Locale LANGUAGE = Locale.forLanguageTag(ServerManager.getApplicationContext().getBean(ServiceProperty.class).getLanguage());

    /**
     * 游戏内消息源，用于获取发给玩家的消息
     */
    public static final MessageSource messageSource = ServerManager.getApplicationContext().getBean("messageSource", MessageSource.class);

    /**
     * 日志消息源，用于获取服务器日志消息
     */
    public static final MessageSource logSource = ServerManager.getApplicationContext().getBean("logSource", MessageSource.class);

    /**
     * 异常消息源，用于获取异常提示消息
     */
    public static final MessageSource exceptionSource = ServerManager.getApplicationContext().getBean("exceptionSource", MessageSource.class);

    /**
     * 获取游戏内消息（自动检测客户端语言）
     * <p>
     * 如果当前存在客户端请求，则以客户端的语言为准。
     * 如果当前非客户端请求（服务端主动发给客户端的），则以服务端语言为准。
     * 所有参数会被转换为字符串，避免数字类型的千分符格式化问题。
     * </p>
     *
     * @param code 消息代码
     * @param args 消息参数，用于替换消息模板中的占位符
     * @return 本地化后的游戏消息
     */
    public static String getMessage(String code, Object... args) {
        Locale clientLang = CharsetConstants.getLanguageLocale(ThreadLocalUtil.getClientLang());
        String[] stringArgs = Arrays.stream(args)
                .map(String::valueOf)
                .toArray(String[]::new);
        return messageSource.getMessage(code, stringArgs, clientLang);
    }

    /**
     * 获取指定语言的游戏内消息
     *
     * @param locale 语言区域设置
     * @param code   消息代码
     * @param args   消息参数
     * @return 本地化后的游戏消息
     */
    public static String getMessage(Locale locale, String code, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }

    /**
     * 获取日志消息（使用服务器默认语言）
     * <p>
     * 日志消息使用StringFormat格式，传参通过{0} {1}占位符指定。
     * </p>
     *
     * @param code 消息代码
     * @param args 消息参数
     * @return 本地化后的日志消息
     */
    public static String getLogMessage(String code, Object... args) {
        return logSource.getMessage(code, args, LANGUAGE);
    }

    /**
     * 获取指定语言的日志消息
     *
     * @param locale 语言区域设置
     * @param code   消息代码
     * @param args   消息参数
     * @return 本地化后的日志消息
     */
    public static String getLogMessage(Locale locale, String code, Object... args) {
        return logSource.getMessage(code, args, locale);
    }

    /**
     * 获取异常消息（使用服务器默认语言）
     *
     * @param code 消息代码
     * @param args 消息参数
     * @return 本地化后的异常消息
     */
    public static String getExceptionMessage(String code, Object... args) {
        return exceptionSource.getMessage(code, args, LANGUAGE);
    }

    /**
     * 获取指定语言的异常消息
     *
     * @param locale 语言区域设置
     * @param code   消息代码
     * @param args   消息参数
     * @return 本地化后的异常消息
     */
    public static String getExceptionMessage(Locale locale, String code, Object... args) {
        return exceptionSource.getMessage(code, args, locale);
    }
}
