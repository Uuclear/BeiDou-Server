package org.gms.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU（最近最少使用）缓存实现类
 * <p>
 * 基于LinkedHashMap实现的LRU缓存，当缓存容量超过指定大小时，
 * 会自动删除最近最少使用的条目。访问顺序模式（accessOrder=true）
 * 确保每次访问（get/put）都会将条目移到链表末尾。
 * </p>
 *
 * @param <K> 缓存键类型
 * @param <V> 缓存值类型
 * @author GMS Team
 * @since 1.0.0
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {

    /**
     * 缓存的最大容量
     */
    private final int capacity;

    /**
     * 默认构造函数，创建容量为1024的LRU缓存
     */
    public LRUCache() {
        this(1024);
    }

    /**
     * 构造函数，创建指定容量的LRU缓存
     *
     * @param capacity 缓存的最大容量
     */
    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    /**
     * 判断是否需要移除最老的条目
     * <p>
     * 当Map中的数据量超过指定缓存容量时，返回true，
     * LinkedHashMap会自动删除最老（最近最少使用）的数据。
     * </p>
     *
     * @param eldest 最老的条目
     * @return 如果需要移除则返回true，否则返回false
     */
    @Override
    public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
