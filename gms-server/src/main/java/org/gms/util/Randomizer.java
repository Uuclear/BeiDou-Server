package org.gms.util;

import java.util.Random;

/**
 * 随机数生成工具类
 * <p>
 * 封装Java的Random类，提供统一的随机数生成接口。
 * 所有方法都是静态方法，使用单例的Random实例。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public class Randomizer {

    /**
     * 单例随机数生成器实例
     */
    private final static Random rand = new Random();

    /**
     * 生成一个随机int值
     *
     * @return 随机int值
     */
    public static int nextInt() {
        return rand.nextInt();
    }

    /**
     * 生成一个[0, arg0)范围内的随机int值
     *
     * @param arg0 上界（不包含）
     * @return 随机int值
     */
    public static int nextInt(final int arg0) {
        return rand.nextInt(arg0);
    }

    /**
     * 生成随机字节并填充到指定数组
     *
     * @param bytes 要填充随机字节的数组
     */
    public static void nextBytes(final byte[] bytes) {
        rand.nextBytes(bytes);
    }

    /**
     * 生成一个随机boolean值
     *
     * @return 随机boolean值
     */
    public static boolean nextBoolean() {
        return rand.nextBoolean();
    }

    /**
     * 生成一个[0.0, 1.0)范围内的随机double值
     *
     * @return 随机double值
     */
    public static double nextDouble() {
        return rand.nextDouble();
    }

    /**
     * 生成一个[0.0, 1.0)范围内的随机float值
     *
     * @return 随机float值
     */
    public static float nextFloat() {
        return rand.nextFloat();
    }

    /**
     * 生成一个随机long值
     *
     * @return 随机long值
     */
    public static long nextLong() {
        return rand.nextLong();
    }

    /**
     * 生成一个[lbound, ubound]范围内的随机int值（包含上下界）
     *
     * @param lbound 下界（包含）
     * @param ubound 上界（包含）
     * @return 指定范围内的随机int值
     */
    public static int rand(final int lbound, final int ubound) {
        return (int) ((rand.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
}