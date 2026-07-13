/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.util;

/**
 * 字符串工具类
 * <p>
 * 提供字符串填充、连接、格式化等常用操作。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public class StringUtil {

    /**
     * 获取左填充到指定长度的字符串
     *
     * @param in      输入字符串
     * @param padchar 用于填充的字符
     * @param length  目标长度
     * @return 左填充后的字符串
     */
    public static String getLeftPaddedStr(String in, char padchar, int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int x = in.length(); x < length; x++) {
            builder.append(padchar);
        }
        builder.append(in);
        return builder.toString();
    }

    /**
     * 获取右填充到指定长度的字符串
     *
     * @param in      输入字符串
     * @param padchar 用于填充的字符
     * @param length  目标长度
     * @return 右填充后的字符串
     */
    public static String getRightPaddedStr(String in, char padchar, int length) {
        StringBuilder builder = new StringBuilder(in);
        for (int x = in.length(); x < length; x++) {
            builder.append(padchar);
        }
        return builder.toString();
    }

    /**
     * 从指定索引开始用空格连接字符串数组
     *
     * @param arr   要连接的字符串数组
     * @param start 起始索引
     * @return 连接后的字符串
     */
    public static String joinStringFrom(String[] arr, int start) {
        return joinStringFrom(arr, start, " ");
    }

    /**
     * 从指定索引开始用指定分隔符连接字符串数组
     *
     * @param arr   要连接的字符串数组
     * @param start 起始索引
     * @param sep   分隔符
     * @return 连接后的字符串
     */
    public static String joinStringFrom(String[] arr, int start, String sep) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < arr.length; i++) {
            builder.append(arr[i]);
            if (i != arr.length - 1) {
                builder.append(sep);
            }
        }
        return builder.toString();
    }

    /**
     * 将枚举名称转换为人类可读格式
     * <p>
     * 处理下划线分隔的枚举名称，调整大小写，使其更易读。
     * 长度小于等于2的单词（通常是缩写）保持大写。
     * </p>
     *
     * @param enumName 枚举名称（如"MAX_VALUE"）
     * @return 人类可读的字符串（如"Max Value"）
     */
    public static String makeEnumHumanReadable(String enumName) {
        StringBuilder builder = new StringBuilder(enumName.length() + 1);
        String[] words = enumName.split("_");
        for (String word : words) {
            if (word.length() <= 2) {
                builder.append(word);
            } else {
                builder.append(word.charAt(0));
                builder.append(word.substring(1).toLowerCase());
            }
            builder.append(' ');
        }
        return builder.substring(0, enumName.length());
    }

    /**
     * 统计字符串中指定字符出现的次数
     *
     * @param str 要检查的字符串
     * @param chr 要统计的字符
     * @return 字符出现的次数
     */
    public static int countCharacters(String str, char chr) {
        int ret = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == chr) {
                ret++;
            }
        }
        return ret;
    }

    /**
     * 判断字符串是否为数值（整数或小数）
     *
     * @param str 要判断的字符串
     * @return 如果是数值返回true，否则返回false
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}