package org.gms.util;

import org.gms.exception.BizException;
import org.gms.exception.BizExceptionEnum;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 参数校验工具类
 * <p>
 * 提供常用的参数校验方法，在参数不符合要求时抛出异常。
 * 支持null检查、空值检查、布尔值检查等，支持自定义错误消息。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public class RequireUtil {

    /**
     * 要求对象必须为null（无自定义消息）
     *
     * @param obj 要检查的对象
     * @throws IllegalArgumentException 如果对象不为null
     */
    public static void requireNull(Object obj) {
        requireNull(obj, null);
    }

    /**
     * 要求对象必须为null
     *
     * @param obj 要检查的对象
     * @param msg 自定义错误消息
     * @throws IllegalArgumentException 如果对象不为null且msg为null
     * @throws BizException             如果对象不为null且msg不为null
     */
    public static void requireNull(Object obj, String msg) {
        if (obj == null) {
            return;
        }
        if (msg == null) {
            throw new IllegalArgumentException();
        } else {
            throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
        }
    }

    /**
     * 要求对象不能为null（无自定义消息）
     *
     * @param obj 要检查的对象
     * @throws IllegalArgumentException 如果对象为null
     */
    public static void requireNotNull(Object obj) {
        requireNull(obj, null);
    }

    /**
     * 要求对象不能为null
     *
     * @param obj 要检查的对象
     * @param msg 自定义错误消息
     * @throws IllegalArgumentException 如果对象为null且msg为null
     * @throws BizException             如果对象为null且msg不为null
     */
    public static void requireNotNull(Object obj, String msg) {
        if (obj != null) {
            return;
        }
        if (msg == null) {
            throw new IllegalArgumentException();
        } else {
            throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
        }
    }

    /**
     * 要求对象不能为空（无自定义消息）
     *
     * @param obj 要检查的对象
     * @throws IllegalArgumentException 如果对象为空
     */
    public static void requireNotEmpty(Object obj) {
        requireNotEmpty(obj, null);
    }

    /**
     * 要求对象不能为空
     * <p>
     * 支持String、Iterable、数组、Map、Iterator等类型的空值检查。
     * 字符串会先trim再判断是否为空。
     * </p>
     *
     * @param obj 要检查的对象
     * @param msg 自定义错误消息
     * @throws IllegalArgumentException 如果对象为空且msg为null
     * @throws BizException             如果对象为空且msg不为null
     */
    public static void requireNotEmpty(Object obj, String msg) {
        if (!isEmpty(obj)) {
            return;
        }

        if (msg == null) {
            throw new IllegalArgumentException();
        } else {
            throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
        }
    }

    /**
     * 要求布尔值必须为true
     *
     * @param b   要检查的布尔值
     * @param msg 自定义错误消息
     * @throws BizException 如果布尔值为false
     */
    public static void requireTrue(boolean b, String msg) {
        if (!b) throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
    }

    /**
     * 要求布尔值必须为false
     *
     * @param b   要检查的布尔值
     * @param msg 自定义错误消息
     * @throws BizException 如果布尔值为true
     */
    public static void requireFalse(boolean b, String msg) {
        if (b) throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
    }

    /**
     * 判断对象是否为空
     * <p>
     * 支持以下类型的空值判断：
     * <ul>
     *   <li>null - 返回true</li>
     *   <li>String - trim后为空返回true</li>
     *   <li>Iterable - 没有元素返回true</li>
     *   <li>数组 - 长度为0返回true</li>
     *   <li>Map - 为空返回true</li>
     *   <li>Iterator - 没有下一个元素返回true</li>
     * </ul>
     * </p>
     *
     * @param obj 要判断的对象
     * @return 如果对象为空返回true，否则返回false
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
     * 判断数值是否为零
     *
     * @param obj 要判断的数值
     * @return 如果数值为null或不为零返回false，为零返回true
     */
    public static boolean isZero(Number obj) {
        if (obj == null) {
            return false;
        }
        return obj.doubleValue() == 0;
    }

    /**
     * 如果对象为空则执行指定操作
     *
     * @param obj      要检查的对象
     * @param runnable 对象为空时要执行的操作
     */
    public static void requireNotEmptyOrElse(Object obj, Runnable runnable) {
        if (!isEmpty(obj)) {
            return;
        }
        runnable.run();
    }

    /**
     * 如果对象不为空则执行指定操作
     *
     * @param obj      要检查的对象
     * @param runnable 对象不为空时要执行的操作
     */
    public static void requireNotEmptyAndThen(Object obj, Runnable runnable) {
        if (isEmpty(obj)) {
            return;
        }
        runnable.run();
    }

    /**
     * 如果两个对象都不为空则执行指定的消费操作
     *
     * @param t        第一个对象
     * @param r        第二个对象
     * @param consumer 消费操作，接收两个参数
     * @param <T>      第一个对象的类型
     * @param <R>      第二个对象的类型
     */
    public static <T, R> void requireNotEmptyAndThen(T t, R r, BiConsumer<T, R> consumer) {
        if (isEmpty(t) || isEmpty(r)) {
            return;
        }
        consumer.accept(t, r);
    }
}
