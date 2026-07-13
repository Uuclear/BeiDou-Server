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

import org.gms.constants.string.CharsetConstants;

import java.util.HexFormat;

/**
 * 十六进制转换工具类
 * <p>
 * 该类提供字节数组与十六进制字符串之间的双向转换功能，
 * 主要用于网络数据包解析、日志输出和调试场景。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public class HexTool {

    /**
     * 将字节数组转换为带空格分隔的大写十六进制字符串
     * <p>
     * 每个字节转换为两个十六进制字符，字节之间用空格分隔，便于阅读。
     * 例如：{1, 16, 127, -1} 转换为 "01 10 7F FF"
     * </p>
     *
     * @param bytes 要转换的字节数组
     * @return 带空格分隔的大写十六进制字符串
     */
    public static String toHexString(byte[] bytes) {
        return HexFormat.ofDelimiter(" ").withUpperCase().formatHex(bytes);
    }

    /**
     * 将字节数组转换为紧凑格式（无空格分隔）的大写十六进制字符串
     * <p>
     * 与 {@link #toHexString(byte[])} 类似，但不包含空格分隔符，适用于存储或传输场景。
     * </p>
     *
     * @param bytes 要转换的字节数组
     * @return 紧凑格式的大写十六进制字符串
     */
    public static String toCompactHexString(byte[] bytes) {
        return HexFormat.of().withUpperCase().formatHex(bytes);
    }

    /**
     * 将十六进制字符串转换为字节数组
     * <p>
     * 每两个连续的十六进制字符转换为一个字节。支持大小写混合，支持带空格或不带空格的格式。
     * 以下格式的字符串都会被正确解析为相同的字节数组：
     * "01 10 7F FF"、"01107FFF"、"01 10 7f ff"、"01107fff"
     * </p>
     *
     * @param hexString 要转换的十六进制字符串
     * @return 转换后的字节数组
     */
    public static byte[] toBytes(String hexString) {
        return HexFormat.of().parseHex(removeAllSpaces(hexString));
    }

    /**
     * 移除字符串中的所有空白字符
     *
     * @param input 原始字符串
     * @return 移除空白字符后的字符串
     */
    private static String removeAllSpaces(String input) {
        return input.replaceAll("\\s", "");
    }

    /**
     * 根据当前客户端语言编码将字节数组转换为可读字符串
     * <p>
     * 对于不可打印的控制字符（ASCII 0-31），替换为点号(.)显示。
     * 使用客户端当前语言对应的字符集进行解码。
     * </p>
     *
     * @param bytes 要转换的字节数组
     * @return 使用客户端字符集解码后的可读字符串
     */
    public static String toStringFromCharset(final byte[] bytes) {
        byte[] filteredBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            if (isSpecialCharacter(bytes[i])) {
                filteredBytes[i] = '.';
            } else {
                filteredBytes[i] = (byte) (bytes[i] & 0xFF);
            }
        }

        return new String(filteredBytes, CharsetConstants.getCharset(ThreadLocalUtil.getClientLang()));
    }

    /**
     * 判断是否为特殊控制字符
     *
     * @param asciiCode ASCII码值
     * @return 如果是ASCII 0-31范围内的控制字符则返回true
     */
    private static boolean isSpecialCharacter(byte asciiCode) {
        return asciiCode >= 0 && asciiCode <= 31;
    }
}
