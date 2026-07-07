package org.gms.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于 {@link LinkedHashMap} 的 LRU（最近最少使用）缓存实现。
 * <p>
 * 当条目数超过容量时，自动淘汰最久未访问的条目。
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    /**
     * 使用默认容量 1024 构造 LRU 缓存。
     */
    public LRUCache() {
        this(1024);
    }

    /**
     * 使用指定容量构造 LRU 缓存。
     *
     * @param capacity 最大缓存条目数
     */
    public LRUCache(int capacity) {
        // 设置最大容量，扩容因子，排序规则
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    /**
     * 当缓存大小超过容量时移除最老的条目。
     *
     * @param eldest 最久未访问的条目
     * @return 若应移除最老条目则返回 {@code true}
     */
    @Override
    public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // 当Map中数据量大于指定缓存个数的时候，返回true，自动删除最老的数据
        return size() > capacity;
    }
}
