package org.gms.util;

import org.gms.exception.BizException;
import org.gms.exception.BizExceptionEnum;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 参数校验工具类，在条件不满足时抛出 {@link IllegalArgumentException} 或 {@link BizException}。
 * <p>
 * 用于 API 入参、业务逻辑前置条件的统一断言式校验。
 */
public class RequireUtil {
    /**
     * 要求对象必须为 {@code null}，否则抛出异常。
     *
     * @param obj 待校验对象
     */
    public static void requireNull(Object obj) {
        requireNull(obj, null);
    }

    /**
     * 要求对象必须为 {@code null}，否则抛出带自定义消息的异常。
     *
     * @param obj 待校验对象
     * @param msg 错误消息，为 {@code null} 时抛出 {@link IllegalArgumentException}
     */
    public static void requireNull(Object obj, String msg) {
        if (obj == null) {
            return;
        }
        // 有无错误信息
        if (msg == null) {
            throw new IllegalArgumentException();
        } else {
            throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
        }
    }

    /**
     * 要求对象必须非 {@code null}，否则抛出异常。
     *
     * @param obj 待校验对象
     */
    public static void requireNotNull(Object obj) {
        requireNull(obj, null);
    }

    /**
     * 要求对象必须非 {@code null}，否则抛出带自定义消息的异常。
     *
     * @param obj 待校验对象
     * @param msg 错误消息，为 {@code null} 时抛出 {@link IllegalArgumentException}
     */
    public static void requireNotNull(Object obj, String msg) {
        if (obj != null) {
            return;
        }
        // 有无错误信息
        if (msg == null) {
            throw new IllegalArgumentException();
        } else {
            throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
        }
    }

    /**
     * 要求对象非空（非 null 且非空字符串/集合/数组/Map），否则抛出异常。
     *
     * @param obj 待校验对象
     */
    public static void requireNotEmpty(Object obj) {
        requireNotEmpty(obj, null);
    }

    /**
     * 要求对象非空，否则抛出带自定义消息的异常。
     *
     * @param obj 待校验对象
     * @param msg 错误消息
     */
    public static void requireNotEmpty(Object obj, String msg) {
        if (!isEmpty(obj)) {
            return;
        }

        // 有无错误信息
        if (msg == null) {
            throw new IllegalArgumentException();
        } else {
            throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
        }
    }

    /**
     * 要求条件为 {@code true}，否则抛出业务异常。
     *
     * @param b   待校验条件
     * @param msg 错误消息
     */
    public static void requireTrue(boolean b, String msg) {
        if (!b) throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
    }

    /**
     * 要求条件为 {@code false}，否则抛出业务异常。
     *
     * @param b   待校验条件
     * @param msg 错误消息
     */
    public static void requireFalse(boolean b, String msg) {
        if (b) throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
    }

    /**
     * 判断对象是否为空（{@code null}、空字符串、空集合、空数组、空 Map 或无元素的迭代器）。
     *
     * @param obj 待判断对象
     * @return 若为空则返回 {@code true}
     */
    public static boolean isEmpty(Object obj) {
        boolean empty = false;
        if (obj == null) {
            empty = true;
        } else if (obj instanceof String str) {
            empty = str.trim().isEmpty();
        } else if (obj instanceof Iterable<?> iter) {
            empty = !iter.iterator().hasNext();
        } else if (obj.getClass().isArray()) {
            empty = Array.getLength(obj) == 0;
        } else if (obj instanceof Map<?, ?> map) {
            empty = map.isEmpty();
        } else if (obj instanceof Iterator<?> iter) {
            empty = !iter.hasNext();
        }
        return empty;
    }

    /**
     * 判断数值是否为 {@code 0}；对象为 {@code null} 时返回 {@code false}。
     *
     * @param obj 数值对象
     * @return 若值为 0 则返回 {@code true}
     */
    public static boolean isZero(Number obj) {
        if (obj == null) {
            return false;
        }
        return obj.doubleValue() == 0;
    }

    /**
     * 若对象为空则执行回调，否则直接返回。
     *
     * @param obj      待校验对象
     * @param runnable 对象为空时执行的操作
     */
    public static void requireNotEmptyOrElse(Object obj, Runnable runnable) {
        if (!isEmpty(obj)) {
            return;
        }
        runnable.run();
    }

    /**
     * 若对象非空则执行回调，否则直接返回。
     *
     * @param obj      待校验对象
     * @param runnable 对象非空时执行的操作
     */
    public static void requireNotEmptyAndThen(Object obj, Runnable runnable) {
        if (isEmpty(obj)) {
            return;
        }
        runnable.run();
    }

    /**
     * 若两个参数均非空则执行双参数消费者，否则直接返回。
     *
     * @param t        第一个参数
     * @param r        第二个参数
     * @param consumer 双参数回调
     * @param <T>      第一个参数类型
     * @param <R>      第二个参数类型
     */
    public static <T, R> void requireNotEmptyAndThen(T t, R r, BiConsumer<T, R> consumer) {
        if (isEmpty(t) || isEmpty(r)) {
            return;
        }
        consumer.accept(t, r);
    }
}
