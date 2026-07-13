package org.gms.scripting;

import net.jcip.annotations.ThreadSafe;

import javax.script.Invocable;
import javax.script.ScriptException;

/**
 * Invocable接口的线程安全包装器。
 * 通过同步所有方法实现线程安全，解决GraalVM JavaScript引擎不允许并发访问脚本的限制。
 * 在多线程环境下安全调用脚本函数时使用此包装类。
 */
@ThreadSafe
public class SynchronizedInvocable implements Invocable {
    /**
     * 被包装的原始Invocable对象
     */
    private final Invocable invocable;

    /**
     * 私有构造函数，通过of()工厂方法创建实例
     *
     * @param invocable 要包装的Invocable对象
     */
    private SynchronizedInvocable(Invocable invocable) {
        this.invocable = invocable;
    }

    /**
     * 创建线程安全的Invocable包装器
     *
     * @param invocable 原始Invocable对象
     * @return 线程安全的Invocable包装器
     */
    public static Invocable of(Invocable invocable) {
        return new SynchronizedInvocable(invocable);
    }

    /**
     * 同步调用脚本对象的方法
     *
     * @param thiz 脚本中的对象
     * @param name 方法名
     * @param args 方法参数
     * @return 方法返回值
     * @throws ScriptException 脚本执行出错时抛出
     * @throws NoSuchMethodException 方法不存在时抛出
     */
    @Override
    public synchronized Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        return invocable.invokeMethod(thiz, name, args);
    }

    /**
     * 同步调用脚本中的顶层函数
     *
     * @param name 函数名
     * @param args 函数参数
     * @return 函数返回值
     * @throws ScriptException 脚本执行出错时抛出
     * @throws NoSuchMethodException 函数不存在时抛出
     */
    @Override
    public synchronized Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        return invocable.invokeFunction(name, args);
    }

    /**
     * 同步获取脚本实现的接口实例
     *
     * @param clasz 接口类型
     * @param <T> 接口类型参数
     * @return 实现该接口的对象
     */
    @Override
    public synchronized <T> T getInterface(Class<T> clasz) {
        return invocable.getInterface(clasz);
    }

    /**
     * 同步获取脚本对象实现的接口实例
     *
     * @param thiz 脚本中的对象
     * @param clasz 接口类型
     * @param <T> 接口类型参数
     * @return 实现该接口的对象
     */
    @Override
    public synchronized <T> T getInterface(Object thiz, Class<T> clasz) {
        return invocable.getInterface(thiz, clasz);
    }
}
