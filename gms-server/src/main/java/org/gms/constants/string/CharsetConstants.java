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
 * 字符集与编码常量，按客户端语言选择 GBK/UTF-8 等编码。
 */

public class CharsetConstants {
    // 保证只加载一次
    private static final Language SERVICE_LANGUAGE = loadServiceLanguage();

    /**
     * 根据语言代码获取对应字符集。
     */
    public static Charset getCharset(int language) {
        return Charset.forName(Language.fromLang(language).getCharset());
    }

    /**
     * 根据语言代码获取 Locale。
     */
    public static Locale getLanguageLocale(int language) {
        return Locale.forLanguageTag(Language.fromLang(language).getLanguageTag());
    }

    /**
     * 判断ZhCN相关条件是否成立。
     */
    public static boolean isZhCN() {
        return Language.LANGUAGE_CN == SERVICE_LANGUAGE;
    }

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
     * @see LanguageConstants
     */
    @Getter
    private enum Language {
        LANGUAGE_US(2, "US-ASCII", "en-US"),
        LANGUAGE_CN(3, "GBK", "zh-CN"),
        LANGUAGE_PT_BR(-1, "ISO-8859-1", "en-US"),
        LANGUAGE_THAI(-1, "TIS620", "th-TH"),
        LANGUAGE_KOREAN(-1, "MS949", "ko-KR");

        private final int lang;
        private final String charset;
        private final String languageTag;

        Language(int lang, String charset, String languageTag) {
            this.lang = lang;
            this.charset = charset;
            this.languageTag = languageTag;
        }

        /**
         * fromLang 相关查询或判定。
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