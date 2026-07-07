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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.HexTool;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * MapleStory v83 AES-OFB 流密码实现，用于 TCP 封包载荷的对称加密。
 * <p>
 * 虽名为 OFB，实际实现为：用固定 AES 密钥对 IV 块加密生成密钥流，再与明文 XOR。
 * 每个封包加解密后通过 {@link #getNewIv(byte[])} 推进 IV 状态，保证密钥流不复用。
 * 封包头 4 字节由 {@link #getPacketHeader(int)} 生成，内含长度与版本校验信息，本身不参与 AES 加密。
 * </p>
 */
public class MapleAESOFB {
    private static final Logger log = LoggerFactory.getLogger(MapleAESOFB.class);
    /** MapleStory 客户端硬编码的 AES-128 密钥（32 字节，按 4 字节小端整数排列） */
    private final static SecretKeySpec skey = new SecretKeySpec(
            new byte[]{
                    0x13, 0x00, 0x00, 0x00,
                    0x08, 0x00, 0x00, 0x00,
                    0x06, 0x00, 0x00, 0x00,
                    (byte) 0xB4, 0x00, 0x00, 0x00,
                    0x1B, 0x00, 0x00, 0x00,
                    0x0F, 0x00, 0x00, 0x00,
                    0x33, 0x00, 0x00, 0x00,
                    0x52, 0x00, 0x00, 0x00}, "AES");

    /** IV 更新算法使用的 256 字节查找表（Maple 自定义 PRNG） */
    private static final byte[] funnyBytes = new byte[]{
            (byte) 0xEC, (byte) 0x3F, (byte) 0x77, (byte) 0xA4, (byte) 0x45, (byte) 0xD0, (byte) 0x71, (byte) 0xBF,
            (byte) 0xB7, (byte) 0x98, (byte) 0x20, (byte) 0xFC, (byte) 0x4B, (byte) 0xE9, (byte) 0xB3, (byte) 0xE1,
            (byte) 0x5C, (byte) 0x22, (byte) 0xF7, (byte) 0x0C, (byte) 0x44, (byte) 0x1B, (byte) 0x81, (byte) 0xBD,
            (byte) 0x63, (byte) 0x8D, (byte) 0xD4, (byte) 0xC3, (byte) 0xF2, (byte) 0x10, (byte) 0x19, (byte) 0xE0,
            (byte) 0xFB, (byte) 0xA1, (byte) 0x6E, (byte) 0x66, (byte) 0xEA, (byte) 0xAE, (byte) 0xD6, (byte) 0xCE,
            (byte) 0x06, (byte) 0x18, (byte) 0x4E, (byte) 0xEB, (byte) 0x78, (byte) 0x95, (byte) 0xDB, (byte) 0xBA,
            (byte) 0xB6, (byte) 0x42, (byte) 0x7A, (byte) 0x2A, (byte) 0x83, (byte) 0x0B, (byte) 0x54, (byte) 0x67,
            (byte) 0x6D, (byte) 0xE8, (byte) 0x65, (byte) 0xE7, (byte) 0x2F, (byte) 0x07, (byte) 0xF3, (byte) 0xAA,
            (byte) 0x27, (byte) 0x7B, (byte) 0x85, (byte) 0xB0, (byte) 0x26, (byte) 0xFD, (byte) 0x8B, (byte) 0xA9,
            (byte) 0xFA, (byte) 0xBE, (byte) 0xA8, (byte) 0xD7, (byte) 0xCB, (byte) 0xCC, (byte) 0x92, (byte) 0xDA,
            (byte) 0xF9, (byte) 0x93, (byte) 0x60, (byte) 0x2D, (byte) 0xDD, (byte) 0xD2, (byte) 0xA2, (byte) 0x9B,
            (byte) 0x39, (byte) 0x5F, (byte) 0x82, (byte) 0x21, (byte) 0x4C, (byte) 0x69, (byte) 0xF8, (byte) 0x31,
            (byte) 0x87, (byte) 0xEE, (byte) 0x8E, (byte) 0xAD, (byte) 0x8C, (byte) 0x6A, (byte) 0xBC, (byte) 0xB5,
            (byte) 0x6B, (byte) 0x59, (byte) 0x13, (byte) 0xF1, (byte) 0x04, (byte) 0x00, (byte) 0xF6, (byte) 0x5A,
            (byte) 0x35, (byte) 0x79, (byte) 0x48, (byte) 0x8F, (byte) 0x15, (byte) 0xCD, (byte) 0x97, (byte) 0x57,
            (byte) 0x12, (byte) 0x3E, (byte) 0x37, (byte) 0xFF, (byte) 0x9D, (byte) 0x4F, (byte) 0x51, (byte) 0xF5,
            (byte) 0xA3, (byte) 0x70, (byte) 0xBB, (byte) 0x14, (byte) 0x75, (byte) 0xC2, (byte) 0xB8, (byte) 0x72,
            (byte) 0xC0, (byte) 0xED, (byte) 0x7D, (byte) 0x68, (byte) 0xC9, (byte) 0x2E, (byte) 0x0D, (byte) 0x62,
            (byte) 0x46, (byte) 0x17, (byte) 0x11, (byte) 0x4D, (byte) 0x6C, (byte) 0xC4, (byte) 0x7E, (byte) 0x53,
            (byte) 0xC1, (byte) 0x25, (byte) 0xC7, (byte) 0x9A, (byte) 0x1C, (byte) 0x88, (byte) 0x58, (byte) 0x2C,
            (byte) 0x89, (byte) 0xDC, (byte) 0x02, (byte) 0x64, (byte) 0x40, (byte) 0x01, (byte) 0x5D, (byte) 0x38,
            (byte) 0xA5, (byte) 0xE2, (byte) 0xAF, (byte) 0x55, (byte) 0xD5, (byte) 0xEF, (byte) 0x1A, (byte) 0x7C,
            (byte) 0xA7, (byte) 0x5B, (byte) 0xA6, (byte) 0x6F, (byte) 0x86, (byte) 0x9F, (byte) 0x73, (byte) 0xE6,
            (byte) 0x0A, (byte) 0xDE, (byte) 0x2B, (byte) 0x99, (byte) 0x4A, (byte) 0x47, (byte) 0x9C, (byte) 0xDF,
            (byte) 0x09, (byte) 0x76, (byte) 0x9E, (byte) 0x30, (byte) 0x0E, (byte) 0xE4, (byte) 0xB2, (byte) 0x94,
            (byte) 0xA0, (byte) 0x3B, (byte) 0x34, (byte) 0x1D, (byte) 0x28, (byte) 0x0F, (byte) 0x36, (byte) 0xE3,
            (byte) 0x23, (byte) 0xB4, (byte) 0x03, (byte) 0xD8, (byte) 0x90, (byte) 0xC8, (byte) 0x3C, (byte) 0xFE,
            (byte) 0x5E, (byte) 0x32, (byte) 0x24, (byte) 0x50, (byte) 0x1F, (byte) 0x3A, (byte) 0x43, (byte) 0x8A,
            (byte) 0x96, (byte) 0x41, (byte) 0x74, (byte) 0xAC, (byte) 0x52, (byte) 0x33, (byte) 0xF0, (byte) 0xD9,
            (byte) 0x29, (byte) 0x80, (byte) 0xB1, (byte) 0x16, (byte) 0xD3, (byte) 0xAB, (byte) 0x91, (byte) 0xB9,
            (byte) 0x84, (byte) 0x7F, (byte) 0x61, (byte) 0x1E, (byte) 0xCF, (byte) 0xC5, (byte) 0xD1, (byte) 0x56,
            (byte) 0x3D, (byte) 0xCA, (byte) 0xF4, (byte) 0x05, (byte) 0xC6, (byte) 0xE5, (byte) 0x08, (byte) 0x49};

    private final short mapleVersion;
    private final Cipher cipher;
    private byte[] iv;

    /**
     * @param iv           初始向量（4 字节），来自 Hello 握手
     * @param mapleVersion 版本号（发送方向为 {@code 0xFFFF - VERSION}，接收方向为 {@code VERSION}）
     */
    public MapleAESOFB(InitializationVector iv, short mapleVersion) {
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            log.warn("Cypher initialization error with skey: {}", skey, e);
            throw new RuntimeException(e);
        }

        this.iv = iv.getBytes();
        // 版本号字节序与客户端一致：高低字节交换
        this.mapleVersion = (short) (((mapleVersion >> 8) & 0xFF) | ((mapleVersion << 8) & 0xFF00));
    }

    /** 将 IV 重复 mul 次拼接，得到 AES 加密的输入块（16 字节） */
    private static byte[] multiplyBytes(byte[] in, int count, int mul) {
        final int size = count * mul;
        byte[] ret = new byte[size];
        for (int x = 0; x < size; x++) {
            ret[x] = in[x % count];
        }
        return ret;
    }

    /**
     * 就地加解密封包载荷（XOR 对称，加密与解密为同一操作）。
     * <p>
     * 按 0x5B0 / 0x5B4 字节分块处理；每 16 字节密钥流用尽时重新 AES 加密 IV 块生成新流。
     * 处理完毕后推进 IV。
     * </p>
     *
     * @param data 封包载荷字节数组（不含 4 字节包头），就地修改
     * @return 同一数组引用
     */
    public synchronized byte[] crypt(byte[] data) {
        int remaining = data.length;
        int llength = 0x5B0; // 首块最大 1456 字节
        int start = 0;
        while (remaining > 0) {
            byte[] myIv = multiplyBytes(this.iv, 4, 4); // 16 字节 AES 输入
            if (remaining < llength) {
                llength = remaining;
            }
            for (int x = start; x < (start + llength); x++) {
                // 每 16 字节重新生成密钥流
                if ((x - start) % myIv.length == 0) {
                    try {
                        byte[] newIv = cipher.doFinal(myIv);
                        System.arraycopy(newIv, 0, myIv, 0, myIv.length);
                    } catch (IllegalBlockSizeException | BadPaddingException e) {
                        e.printStackTrace();
                    }
                }
                data[x] ^= myIv[(x - start) % myIv.length];
            }
            start += llength;
            remaining -= llength;
            llength = 0x5B4; // 后续块最大 1460 字节
        }
        updateIv();
        return data;
    }

    /** 封包处理完成后，用 Maple 自定义算法推进 IV */
    private synchronized void updateIv() {
        this.iv = getNewIv(this.iv);
    }

    /**
     * 生成发送方向的 4 字节封包头（明文，不经 AES 加密）。
     * <p>
     * 格式：前 2 字节为 IV 与版本 XOR 后的值；后 2 字节为长度 XOR 后的值。
     * 长度字段为大端序 short 的字节交换形式。
     * </p>
     *
     * @param length 封包载荷长度（不含包头）
     * @return 4 字节包头
     */
    public byte[] getPacketHeader(int length) {
        int iiv = (iv[3]) & 0xFF;
        iiv |= (iv[2] << 8) & 0xFF00;
        iiv ^= mapleVersion;
        // 长度高低字节交换（Maple 封包惯例）
        int mlength = ((length << 8) & 0xFF00) | (length >>> 8);
        int xoredIv = iiv ^ mlength;
        byte[] ret = new byte[4];
        ret[0] = (byte) ((iiv >>> 8) & 0xFF);
        ret[1] = (byte) (iiv & 0xFF);
        ret[2] = (byte) ((xoredIv >>> 8) & 0xFF);
        ret[3] = (byte) (xoredIv & 0xFF);
        return ret;
    }

    /**
     * 从 4 字节包头解析封包载荷长度。
     *
     * @param packetHeader 包头整型表示（大端 4 字节）
     * @return 载荷字节数
     */
    public static int getPacketLength(int packetHeader) {
        int packetLength = ((packetHeader >>> 16) ^ (packetHeader & 0xFFFF));
        packetLength = ((packetLength << 8) & 0xFF00) | ((packetLength >>> 8) & 0xFF);
        return packetLength;
    }

    /** 校验包头前 2 字节解密后是否匹配当前 IV 与版本号 */
    private boolean checkPacket(byte[] packet) {
        return ((((packet[0] ^ iv[2]) & 0xFF) == ((mapleVersion >> 8) & 0xFF)) &&
                (((packet[1] ^ iv[3]) & 0xFF) == (mapleVersion & 0xFF)));
    }

    /**
     * 校验接收封包包头是否合法（版本/IV 匹配），用于抵御垃圾连接。
     *
     * @param packetHeader 4 字节包头整型
     * @return 合法返回 {@code true}
     */
    public boolean isValidHeader(int packetHeader) {
        byte[] packetHeaderBuf = new byte[2];
        packetHeaderBuf[0] = (byte) ((packetHeader >> 24) & 0xFF);
        packetHeaderBuf[1] = (byte) ((packetHeader >> 16) & 0xFF);
        return checkPacket(packetHeaderBuf);
    }

    /**
     * Maple 自定义 IV 推进算法，每处理一个封包后调用。
     *
     * @param oldIv 当前 4 字节 IV
     * @return 新的 4 字节 IV
     */
    public static byte[] getNewIv(byte[] oldIv) {
        byte[] in = {(byte) 0xf2, 0x53, (byte) 0x50, (byte) 0xc6};
        for (int x = 0; x < 4; x++) {
            funnyShit(oldIv[x], in);
        }
        return in;
    }

    @Override
    public String toString() {
        return "IV: " + HexTool.toHexString(this.iv);
    }

    /** IV 更新的单字节混合步骤，使用 funnyBytes 查找表与位旋转 */
    private static byte[] funnyShit(byte inputByte, byte[] in) {
        byte elina = in[1];
        byte anna = inputByte;
        byte moritz = funnyBytes[(int) elina & 0xFF];
        moritz -= inputByte;
        in[0] += moritz;
        moritz = in[2];
        moritz ^= funnyBytes[(int) anna & 0xFF];
        elina -= (int) moritz & 0xFF;
        in[1] = elina;
        elina = in[3];
        moritz = elina;
        elina -= (int) in[0] & 0xFF;
        moritz = funnyBytes[(int) moritz & 0xFF];
        moritz += inputByte;
        moritz ^= in[2];
        in[2] = moritz;
        elina += (int) funnyBytes[(int) anna & 0xFF] & 0xFF;
        in[3] = elina;
        int merry = ((int) in[0]) & 0xFF;
        merry |= (in[1] << 8) & 0xFF00;
        merry |= (in[2] << 16) & 0xFF0000;
        merry |= (in[3] << 24) & 0xFF000000;
        int ret_value = merry;
        ret_value = ret_value >>> 0x1d;
        merry = merry << 3;
        ret_value = ret_value | merry;
        in[0] = (byte) (ret_value & 0xFF);
        in[1] = (byte) ((ret_value >> 8) & 0xFF);
        in[2] = (byte) ((ret_value >> 16) & 0xFF);
        in[3] = (byte) ((ret_value >> 24) & 0xFF);
        return in;
    }
}
