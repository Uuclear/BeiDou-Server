package org.gms.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 国际化（i18n）配置类
 * <p>
 * 配置Spring的国际化消息源，为不同类型的消息资源创建独立的MessageSource Bean。
 * 由于messageSource.getMessage底层是通过循环遍历文件名去读取的，
 * 将不同文件名定义为不同的bean，可以在扫描时直接找到对应文件，提升性能。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Configuration
public class I18nConfig {

    /**
     * 通用消息源Bean
     * <p>
     * 加载classpath:i18n/message系列资源文件，用于普通业务消息的国际化。
     * </p>
     *
     * @return MessageSource实例
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * 日志消息源Bean
     * <p>
     * 加载classpath:i18n/log系列资源文件，用于日志消息的国际化。
     * </p>
     *
     * @return MessageSource实例
     */
    @Bean
    public MessageSource logSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/log");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * 异常消息源Bean
     * <p>
     * 加载classpath:i18n/exception系列资源文件，用于异常消息的国际化。
     * </p>
     *
     * @return MessageSource实例
     */
    @Bean
    public MessageSource exceptionSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/exception");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
