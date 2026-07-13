package org.gms.util;

/**
 * 数值转换工具类
 * <p>
 * 提供基本数据类型之间的转换功能，包括字节数组与long类型的互转、
 * 浮点数到整数的安全转换等。
 * </p>
 *
 * @author Shavit
 * @since 1.0.0
 */
public class NumberTool {

    /**
     * 将8字节的字节数组转换为long类型
     * <p>
     * 采用大端字节序（高位在前）进行转换。
     * </p>
     *
     * @param aToConvert 要转换的字节数组，长度必须为8字节
     * @return 转换后的long值
     * @throws IllegalArgumentException 如果输入数组长度不是8字节
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
     * 将long类型转换为8字节的字节数组
     * <p>
     * 采用大端字节序（高位在前）进行转换。
     * </p>
     *
     * @param nToConvert 要转换的long值
     * @return 转换后的8字节数组
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
     * 将float类型安全转换为int类型
     * <p>
     * 如果float值超过Integer.MAX_VALUE，返回Integer.MAX_VALUE。
     * </p>
     *
     * @param f 要转换的float值
     * @return 转换后的int值
     */
    public static int floatToInt(float f) {
        return doubleToInt(f);
    }

    /**
     * 将double类型安全转换为int类型
     * <p>
     * 如果double值超过Integer.MAX_VALUE，返回Integer.MAX_VALUE，避免溢出。
     * </p>
     *
     * @param d 要转换的double值
     * @return 转换后的int值
     */
    public static int doubleToInt(double d) {
        if (d > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) d;
    }
}
