package org.gms.util;

/**
 * 数值与字节数组互转工具类。
 *
 * @author Shavit
 */
public class NumberTool {

    /**
     * 将 8 字节数组按大端序解析为 {@code long}。
     *
     * @param aToConvert 长度为 8 的字节数组
     * @return 解析后的长整型值
     * @throws IllegalArgumentException 若输入长度不为 8
     */
    public static long BytesToLong(byte[] aToConvert) {
        if (aToConvert.length != Long.BYTES) {
            throw new IllegalArgumentException(String.format("Size of input should be %d", (Long.SIZE / 8)));
        }

        long nResult = 0;

        for (int i = 0; i < Long.BYTES; i++) {
            nResult <<= Byte.SIZE;
            nResult |= (aToConvert[i] & 0xFF);
        }

        return nResult;
    }

    /**
     * 将 {@code long} 按大端序编码为 8 字节数组。
     *
     * @param nToConvert 待编码的长整型值
     * @return 8 字节数组
     */
    public static byte[] LongToBytes(long nToConvert) {
        byte[] aBytes = new byte[Long.BYTES];

        for (int i = aBytes.length - 1; i >= 0; i--) {
            aBytes[i] = (byte) (nToConvert & 0xFF);
            nToConvert >>= Byte.SIZE;
        }

        return aBytes;
    }

    /**
     * 将 {@code float} 安全转换为 {@code int}，超出 {@link Integer#MAX_VALUE} 时截断为最大值。
     *
     * @param f 浮点值
     * @return 整型结果
     */
    public static int floatToInt(float f) {
        return doubleToInt(f);
    }

    /**
     * 将 {@code double} 安全转换为 {@code int}，超出 {@link Integer#MAX_VALUE} 时截断为最大值。
     *
     * @param d 双精度值
     * @return 整型结果
     */
    public static int doubleToInt(double d) {
        if (d > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) d;
    }
}
