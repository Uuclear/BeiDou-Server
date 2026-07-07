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
package org.gms.net.encryption;

/**
 * MapleStory 自定义封包混淆层，在 AES-OFB 之外对载荷进行二次变换。
 * <p>
 * v83 封包加解密顺序：
 * <ul>
 *   <li>发送：明文 → {@link #encryptData} → AES-OFB</li>
 *   <li>接收：AES-OFB → {@link #decryptData} → 明文</li>
 * </ul>
 * 算法对载荷执行 6 轮正向/反向遍历，结合循环移位、XOR 与取反操作。
 * </p>
 */
public class MapleCustomEncryption {
    /** 8 位循环左移 */
    private static byte rollLeft(byte in, int count) {
        int tmp = (int) in & 0xFF;
        tmp = tmp << (count % 8);
        return (byte) ((tmp & 0xFF) | (tmp >> 8));
    }

    /** 8 位循环右移 */
    private static byte rollRight(byte in, int count) {
        int tmp = (int) in & 0xFF;
        tmp = (tmp << 8) >>> (count % 8);

        return (byte) ((tmp & 0xFF) | (tmp >>> 8));
    }

    /**
     * 发送前对封包载荷加密（在 AES 之前调用）。
     *
     * @param data 载荷字节数组，就地修改
     * @return 同一数组引用
     */
    public static byte[] encryptData(byte[] data) {
        for (int j = 0; j < 6; j++) {
            byte remember = 0; // 链式 XOR 状态，每字节依赖前一字节结果
            byte dataLength = (byte) (data.length & 0xFF);
            if (j % 2 == 0) {
                // 偶数轮：正向遍历
                for (int i = 0; i < data.length; i++) {
                    byte cur = data[i];
                    cur = rollLeft(cur, 3);
                    cur += dataLength;
                    cur ^= remember;
                    remember = cur;
                    cur = rollRight(cur, (int) dataLength & 0xFF);
                    cur = ((byte) ((~cur) & 0xFF));
                    cur += 0x48;
                    dataLength--;
                    data[i] = cur;
                }
            } else {
                // 奇数轮：反向遍历，使用不同的移位与常量
                for (int i = data.length - 1; i >= 0; i--) {
                    byte cur = data[i];
                    cur = rollLeft(cur, 4);
                    cur += dataLength;
                    cur ^= remember;
                    remember = cur;
                    cur ^= 0x13;
                    cur = rollRight(cur, 3);
                    dataLength--;
                    data[i] = cur;
                }
            }
        }
        return data;
    }

    /**
     * 接收后对封包载荷解密（在 AES 之后调用），为 {@link #encryptData} 的逆操作。
     *
     * @param data 载荷字节数组，就地修改
     * @return 同一数组引用
     */
    public static byte[] decryptData(byte[] data) {
        for (int j = 1; j <= 6; j++) {
            byte remember = 0;
            byte dataLength = (byte) (data.length & 0xFF);
            byte nextRemember;
            if (j % 2 == 0) {
                for (int i = 0; i < data.length; i++) {
                    byte cur = data[i];
                    cur -= 0x48;
                    cur = ((byte) ((~cur) & 0xFF));
                    cur = rollLeft(cur, (int) dataLength & 0xFF);
                    nextRemember = cur;
                    cur ^= remember;
                    remember = nextRemember;
                    cur -= dataLength;
                    cur = rollRight(cur, 3);
                    data[i] = cur;
                    dataLength--;
                }
            } else {
                // 奇数轮：反向遍历，使用不同的移位与常量
                for (int i = data.length - 1; i >= 0; i--) {
                    byte cur = data[i];
                    cur = rollLeft(cur, 3);
                    cur ^= 0x13;
                    nextRemember = cur;
                    cur ^= remember;
                    remember = nextRemember;
                    cur -= dataLength;
                    cur = rollRight(cur, 4);
                    data[i] = cur;
                    dataLength--;
                }
            }
        }
        return data;
    }
}
