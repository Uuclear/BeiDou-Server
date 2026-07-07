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
 * 字符串处理工具类，提供填充、拼接、枚举名格式化等常用操作。
 */
public class StringUtil {
    /**
     * 在字符串左侧用指定字符填充至目标长度。
     *
     * @param in      待填充的输入字符串
     * @param padchar 填充字符
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
     * 在字符串右侧用指定字符填充至目标长度。
     *
     * @param in      待填充的输入字符串
     * @param padchar 填充字符
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
     * 从指定下标起，以空格为分隔符拼接字符串数组。
     *
     * @param arr   字符串数组
     * @param start 起始下标
     * @return 拼接后的字符串
     */
    public static String joinStringFrom(String[] arr, int start) {
        return joinStringFrom(arr, start, " ");
    }

    /**
     * 从指定下标起，以自定义分隔符拼接字符串数组。
     *
     * @param arr   字符串数组
     * @param start 起始下标
     * @param sep   分隔符
     * @return 拼接后的字符串
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
     * 将枚举常量名（下划线分隔）格式化为可读文本。
     *
     * @param enumName 枚举常量名
     * @return 格式化后的可读字符串
     */
    public static String makeEnumHumanReadable(String enumName) {
        StringBuilder builder = new StringBuilder(enumName.length() + 1);
        String[] words = enumName.split("_");
        for (String word : words) {
            if (word.length() <= 2) {
                builder.append(word); // assume that it's an abbrevation
            } else {
                builder.append(word.charAt(0));
                builder.append(word.substring(1).toLowerCase());
            }
            builder.append(' ');
        }
        return builder.substring(0, enumName.length());
    }

    /**
     * 统计字符串中指定字符出现的次数。
     *
     * @param str 待统计的字符串
     * @param chr 目标字符
     * @return 出现次数
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
     * 判断字符串是否为数值（整数或小数，可带负号）。
     *
     * @param str 待判断的字符串
     * @return 若为合法数值则返回 {@code true}
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}