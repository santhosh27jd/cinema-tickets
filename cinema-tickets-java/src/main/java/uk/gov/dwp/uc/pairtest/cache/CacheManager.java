package uk.gov.dwp.uc.pairtest.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache Manager is a custom cache class implemented for testing
 * @param <K>
 * @param <T>
 */
public class CacheManager<K,T> {

    /**
     * cache is a HashMap which is thread safe
     */
    private ConcurrentHashMap<K, T> cache;

    /**
     * Constructor CacheManager
     */
    public CacheManager(){
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void put(K key, T value) {
        cache.put(key, value);
    }

    /**
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public T get(K key) {
        return cache.get(key);
    }

    /**
     *
     * @param key
     */
    public void remove(String key) {
        cache.remove(key);
    }

    /**
     *
     * @return
     */
    public int size() {
        return cache.size();
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     *
     * @param key
     * @return
     */
    public boolean contains(K key){
        return cache.containsKey(key);
    }

    /**
     *  Remove all data
     */
    public void clear(){
        cache.clear();
    }

}
