package org.gms.scripting;

import net.jcip.annotations.ThreadSafe;

import javax.script.Invocable;
import javax.script.ScriptException;

/**
 * {@link Invocable} 的线程安全包装器，对所有方法加同步锁。
 * <p>
 * GraalVM 要求已求值的脚本引擎不能并发访问；事件脚本等场景下多个线程可能同时回调 JS，
 * 因此通过本类将调用串行化，避免并发调用导致引擎状态异常。
 * </p>
 */
@ThreadSafe
public class SynchronizedInvocable implements Invocable {
    private final Invocable invocable;

    private SynchronizedInvocable(Invocable invocable) {
        this.invocable = invocable;
    }

    /**
     * 将普通 {@link Invocable} 包装为线程安全版本。
     *
     * @param invocable 原始可调用脚本接口
     * @return 同步包装后的 {@link Invocable}
     */
    public static Invocable of(Invocable invocable) {
        return new SynchronizedInvocable(invocable);
    }

    /** {@inheritDoc} 以同步方式调用脚本对象方法。 */
    @Override
    public synchronized Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        return invocable.invokeMethod(thiz, name, args);
    }

    /** {@inheritDoc} 以同步方式调用脚本全局函数。 */
    @Override
    public synchronized Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        return invocable.invokeFunction(name, args);
    }

    /** {@inheritDoc} 以同步方式获取脚本实现的 Java 接口代理。 */
    @Override
    public synchronized <T> T getInterface(Class<T> clasz) {
        return invocable.getInterface(clasz);
    }

    /** {@inheritDoc} 以同步方式获取指定脚本对象实现的 Java 接口代理。 */
    @Override
    public synchronized <T> T getInterface(Object thiz, Class<T> clasz) {
        return invocable.getInterface(thiz, clasz);
    }
}
