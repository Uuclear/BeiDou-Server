package org.gms.util;

import java.util.Random;

/**
 * 全局随机数工具类，封装 {@link Random} 的常用操作。
 * <p>
 * 服务端游戏逻辑（掉落、伤害浮动等）统一通过本类获取随机值，保证使用同一随机源。
 */
public class Randomizer {

    private final static Random rand = new Random();

    /**
     * 返回下一个伪随机 {@code int} 值（全范围）。
     *
     * @return 随机整数
     */
    public static int nextInt() {
        return rand.nextInt();
    }

    /**
     * 返回 {@code [0, arg0)} 范围内的伪随机 {@code int} 值。
     *
     * @param arg0 上界（不含）
     * @return 随机整数
     */
    public static int nextInt(final int arg0) {
        return rand.nextInt(arg0);
    }

    /**
     * 用随机字节填充指定数组。
     *
     * @param bytes 待填充的字节数组
     */
    public static void nextBytes(final byte[] bytes) {
        rand.nextBytes(bytes);
    }

    /**
     * 返回伪随机 {@code boolean} 值。
     *
     * @return 随机布尔值
     */
    public static boolean nextBoolean() {
        return rand.nextBoolean();
    }

    /**
     * 返回 {@code [0.0, 1.0)} 范围内的伪随机 {@code double} 值。
     *
     * @return 随机双精度值
     */
    public static double nextDouble() {
        return rand.nextDouble();
    }

    /**
     * 返回 {@code [0.0, 1.0)} 范围内的伪随机 {@code float} 值。
     *
     * @return 随机单精度值
     */
    public static float nextFloat() {
        return rand.nextFloat();
    }

    /**
     * 返回下一个伪随机 {@code long} 值。
     *
     * @return 随机长整型值
     */
    public static long nextLong() {
        return rand.nextLong();
    }

    /**
     * 返回 {@code [lbound, ubound]} 闭区间内的伪随机整数（含两端）。
     *
     * @param lbound 下界（含）
     * @param ubound 上界（含）
     * @return 区间内的随机整数
     */
    public static int rand(final int lbound, final int ubound) {
        return (int) ((rand.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
}
