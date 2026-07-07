package org.gms.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 国际化 MessageSource 配置，分别注册通用消息、日志与异常文案资源束。
 * 拆分多个 Bean 以减少 getMessage 扫描范围，提升文案查找效率。
 */
@Configuration
public class I18nConfig {

    /**
     * 注册通用国际化消息源（i18n/message）。
     * @return MessageSource Bean
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * 注册日志文案国际化消息源（i18n/log）。
     * @return MessageSource Bean
     */
    @Bean
    public MessageSource logSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/log");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * 注册异常文案国际化消息源（i18n/exception）。
     * @return MessageSource Bean
     */
    @Bean
    public MessageSource exceptionSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/exception");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
