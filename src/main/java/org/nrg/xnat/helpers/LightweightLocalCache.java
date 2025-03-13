package org.nrg.xnat.helpers;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LightweightLocalCache<T> {
    public class LocalCache<T> {
        T value;
        Long lastCheckInMillis;

        public LocalCache(T v) {
            this.value = v;
            this.lastCheckInMillis = Calendar.getInstance().getTimeInMillis();
        }
    }

    public T getValue(String key, Integer timeoutInMillis) {
        if (key != null && localCache.containsKey(key)) {
            LocalCache cache = localCache.get(key);
            if ((cache.lastCheckInMillis + timeoutInMillis) > Calendar.getInstance().getTimeInMillis()) {
                return (T) cache.value;
            }
        }
        return null;
    }

    public T cacheValue(String key, T value) {
        if (key != null && value != null) {
            LocalCache<T> lc = new LocalCache(value);
            localCache.put(key, lc);
        }
        return value;
    }

    private Map<String, LocalCache> localCache = new ConcurrentHashMap<>();
}
