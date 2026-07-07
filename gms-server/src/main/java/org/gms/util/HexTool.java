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
 * 十六进制与字节数组互转工具，以及按客户端字符集将字节转为可读字符串。
 */
public class HexTool {

    /**
     * 将字节数组转为大写十六进制字符串，字节间以空格分隔。
     * <p>示例：{@code {1, 16, 127, -1}} → {@code "01 F0 7F FF"}
     *
     * @param bytes 待转换的字节数组
     * @return 带空格分隔的十六进制字符串
     */
    public static String toHexString(byte[] bytes) {
        return HexFormat.ofDelimiter(" ").withUpperCase().formatHex(bytes);
    }

    /**
     * 将字节数组转为紧凑的大写十六进制字符串（无空格分隔）。
     *
     * @param bytes 待转换的字节数组
     * @return 紧凑十六进制字符串
     */
    public static String toCompactHexString(byte[] bytes) {
        return HexFormat.of().withUpperCase().formatHex(bytes);
    }

    /**
     * 将十六进制字符串解析为字节数组；每两个十六进制字符对应一个字节。
     * <p>大小写均可，是否含空格均可。示例：{@code "01 10 7F FF"} → {@code {1, 16, 127, -1}}
     *
     * @param hexString 十六进制字符串
     * @return 解析后的字节数组
     */
    public static byte[] toBytes(String hexString) {
        return HexFormat.of().parseHex(removeAllSpaces(hexString));
    }

    private static String removeAllSpaces(String input) {
        return input.replaceAll("\\s", "");
    }

    /**
     * 按当前客户端字符集将字节数组转为可读字符串；控制字符（0–31）显示为 {@code '.'}。
     *
     * @param bytes 原始字节数组
     * @return 可读字符串表示
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

    private static boolean isSpecialCharacter(byte asciiCode) {
        return asciiCode >= 0 && asciiCode <= 31;
    }
}
