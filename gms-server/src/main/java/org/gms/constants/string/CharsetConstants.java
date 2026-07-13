/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gms.constants.string;

/*
 * Thanks to GabrielSin (EllinMS) - gabrielsin@playellin.net
 * Ellin
 * MapleStory Server
 * CharsetConstants
 */

import lombok.Getter;
import org.gms.manager.ServerManager;
import org.gms.property.ServiceProperty;

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * 字符集常量类
 * <p>
 * 提供游戏中不同语言的字符集和区域设置支持，包括英语、中文、葡萄牙语、泰语、韩语等。
 * 根据服务配置加载默认语言，并提供根据语言ID获取字符集和区域设置的方法。
 * </p>
 *
 * @author GabrielSin (EllinMS)
 */
public class CharsetConstants {
    /** 服务默认语言，保证只加载一次 */
    private static final Language SERVICE_LANGUAGE = loadServiceLanguage();

    /**
     * 根据语言ID获取对应的字符集
     *
     * @param language 语言ID
     * @return 对应的字符集
     */
    public static Charset getCharset(int language) {
        return Charset.forName(Language.fromLang(language).getCharset());
    }

    /**
     * 根据语言ID获取对应的区域设置
     *
     * @param language 语言ID
     * @return 对应的区域设置
     */
    public static Locale getLanguageLocale(int language) {
        return Locale.forLanguageTag(Language.fromLang(language).getLanguageTag());
    }

    /**
     * 判断当前服务语言是否为简体中文
     *
     * @return 如果是简体中文返回true
     */
    public static boolean isZhCN() {
        return Language.LANGUAGE_CN == SERVICE_LANGUAGE;
    }

    /**
     * 从服务配置中加载默认语言
     *
     * @return 加载的语言枚举值
     */
    private static Language loadServiceLanguage() {
        ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);
        String language = serviceProperty.getLanguage();
        if (language.equals("zh-CN")) {
            return Language.LANGUAGE_CN;
        } else {
            return Language.LANGUAGE_US;
        }
    }

    /**
     * 语言枚举类
     *
     * @see LanguageConstants
     */
    @Getter
    private enum Language {
        /** 英语（美国） */
        LANGUAGE_US(2, "US-ASCII", "en-US"),
        /** 简体中文 */
        LANGUAGE_CN(3, "GBK", "zh-CN"),
        /** 葡萄牙语（巴西） */
        LANGUAGE_PT_BR(-1, "ISO-8859-1", "en-US"),
        /** 泰语 */
        LANGUAGE_THAI(-1, "TIS620", "th-TH"),
        /** 韩语 */
        LANGUAGE_KOREAN(-1, "MS949", "ko-KR");

        /** 语言ID */
        private final int lang;
        /** 字符集名称 */
        private final String charset;
        /** 语言标签 */
        private final String languageTag;

        Language(int lang, String charset, String languageTag) {
            this.lang = lang;
            this.charset = charset;
            this.languageTag = languageTag;
        }

        /**
         * 根据语言ID获取语言枚举
         *
         * @param lang 语言ID
         * @return 对应的语言枚举，未找到则返回服务默认语言
         */
        public static Language fromLang(int lang) {
            for (Language value : values()) {
                if (value.getLang() == lang) {
                    return value;
                }
            }
            return SERVICE_LANGUAGE;
        }
    }
}
