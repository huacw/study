package net.sea.study.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author huacw
 * @date 2019/12/27
 */
public abstract class GuavaCache<K, V> {
    protected LoadingCache<K, V> cache;

    /**
     * 默认的构造函数
     */
    public GuavaCache() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K k) throws Exception {
                        return loadData(k);
                    }
                });
    }

    /**
     * 超时缓存：数据写入缓存超过一定时间自动刷新
     *
     * @param duration
     * @param timeUtil
     */
    public GuavaCache(long duration, TimeUnit timeUtil) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(duration, timeUtil)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K k) throws Exception {
                        return loadData(k);
                    }
                });
    }

    /**
     * 限容缓存：缓存数据个数不能超过maxSize
     *
     * @param maxSize
     */
    public GuavaCache(long maxSize) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K k) throws Exception {
                        return loadData(k);
                    }
                });
    }

    /**
     * 权重缓存：缓存数据权重和不能超过maxWeight
     *
     * @param maxWeight
     * @param weigher：权重函数类，需要实现计算元素权重的函数
     */
    public GuavaCache(long maxWeight, Weigher<K, V> weigher) {
        cache = CacheBuilder.newBuilder()
                .maximumWeight(maxWeight)
                .weigher(weigher)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K k) throws Exception {
                        return loadData(k);
                    }
                });
    }


    /**
     * 缓存数据加载方法
     *
     * @param k
     * @return
     * @author coshaho
     */
    protected abstract V loadData(K k);

    /**
     * 从缓存获取数据
     *
     * @param param
     * @return
     * @author coshaho
     */
    public V getCache(K param) {
        return cache.getUnchecked(param);
    }

    /**
     * 清除缓存数据，缓存清除后，数据会重新调用load方法获取
     *
     * @param k
     * @author coshaho
     */
    public void refresh(K k) {
        cache.refresh(k);
    }

    /**
     * 主动设置缓存数据
     *
     * @param k
     * @param v
     * @author coshaho
     */
    public void put(K k, V v) {
        cache.put(k, v);
    }

    public long getSize() {
        return cache.size();
    }

    public ConcurrentMap<K, V> getAllValues() {
        return cache.asMap();
    }
}
