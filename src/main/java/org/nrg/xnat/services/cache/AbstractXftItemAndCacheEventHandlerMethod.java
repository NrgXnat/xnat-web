package org.nrg.xnat.services.cache;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.nrg.framework.jcache.DefaultGenericCacheEntryListener;
import org.nrg.framework.jcache.GenericCacheEventListener;
import org.nrg.framework.jcache.JCacheHelper;
import org.nrg.xft.ItemI;
import org.nrg.xft.event.XftItemEventI;
import org.nrg.xft.event.methods.AbstractXftItemEventHandlerMethod;
import org.nrg.xft.event.methods.XftItemEventCriteria;

import javax.annotation.Nullable;
import javax.cache.Cache;
import javax.inject.Provider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

/**
 * Provides both {@link AbstractXftItemEventHandlerMethod} implementation and the functionality for managing multiple
 * caches under a single namespace.
 */
@SuppressWarnings("WeakerAccess")
@Getter(PROTECTED)
@Accessors(prefix = "_")
@Slf4j
public abstract class AbstractXftItemAndCacheEventHandlerMethod extends AbstractXftItemEventHandlerMethod {
    private final JCacheHelper                                   _cacheHelper;
    private final Map<String, Pair<Class<?>, Class<?>>>          _cacheMap;
    private final List<GenericCacheEventListener<String, ItemI>> _cacheEventListeners;

    /**
     * Creates the super class using the default <b>CacheEventListenerAdapter</b> implementation for the underlying default functionality.
     */
    protected AbstractXftItemAndCacheEventHandlerMethod(final JCacheHelper cacheHelper, final XftItemEventCriteria first, final XftItemEventCriteria... criteria) {
        this(cacheHelper, Collections.emptyMap(), Collections.emptyList(), first, criteria);
    }

    protected AbstractXftItemAndCacheEventHandlerMethod(final JCacheHelper cacheHelper, final Map<String, Pair<Class<?>, Class<?>>> cacheMap, final XftItemEventCriteria first, final XftItemEventCriteria... criteria) {
        this(cacheHelper, cacheMap, Collections.emptyList(), first, criteria);
    }

    /**
     * Creates the super class using the submitted <b>CacheEventListener</b> instance for the underlying default functionality.
     */
    @SuppressWarnings("unused")
    protected AbstractXftItemAndCacheEventHandlerMethod(final JCacheHelper cacheHelper, final GenericCacheEventListener<String, ItemI> cacheEventListener, final XftItemEventCriteria first, final XftItemEventCriteria... criteria) {
        this(cacheHelper, Collections.emptyMap(), Collections.singletonList(cacheEventListener), first, criteria);
    }

    /**
     * Creates the super class using the submitted <b>CacheEventListener</b> instance for the underlying default functionality.
     */
    protected AbstractXftItemAndCacheEventHandlerMethod(final JCacheHelper cacheHelper, final Map<String, Pair<Class<?>, Class<?>>> cacheMap, final List<GenericCacheEventListener<String, ItemI>> cacheEventListeners, final XftItemEventCriteria first, final XftItemEventCriteria... criteria) {
        super(first, criteria);
        _cacheHelper         = cacheHelper;
        _cacheMap            = cacheMap;
        _cacheEventListeners = ObjectUtils.defaultIfNull(cacheEventListeners, Collections.singletonList(new DefaultGenericCacheEntryListener<>()));
        initializeCaches();
        registerCacheEventListener();
        log.debug("XFT item event handler method and cache event listener created with {} cache event listener instances, {} criteria specified", getCacheEventListeners().size(), criteria.length + 1);
    }

    /**
     * Defines the top-level name for the cache implementation. Note that this is used as a prefix for caches controlled
     * by the implementation rather than the full name of a particular cache.
     *
     * @return The top-level cache name.
     */
    abstract public String getCacheName();

    /**
     * Returns the timestamp indicating when the specified cache entry was last updated. If the entry was only
     * inserted and not updated, the insert time is returned.
     *
     * @param cacheId The ID of the cache entry to check.
     *
     * @return The date and time of the latest update to the specified cache entry.
     */
    public Date getCacheEntryLastUpdateTime(final String cacheId) {
        if (!has(cacheId)) {
            log.trace("Trying to check the last update time for cache entry '{}', but that is not in the cache.", cacheId);
            return null;
        }
        final long lastUpdateTime = getLatestOfCreationAndUpdateTime(cacheId);
        log.trace("Checked last update time for cache entry '{}' and found: {}", cacheId, lastUpdateTime);
        return new Date(lastUpdateTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    abstract protected boolean handleEventImpl(final XftItemEventI event);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    protected static String createCacheIdFromElements(final String... elements) {
        return StringUtils.join(elements, ":");
    }

    protected <K, V> Cache<K, V> getCache(final String cacheName, final Class<K> keyType, final Class<V> valueType) {
        return _cacheHelper.getCache(cacheName, keyType, valueType);
    }

    protected long getLatestOfCreationAndUpdateTime(final String cacheId) {
        return 1L; // STASHED: getEhCache().get(cacheId).getLatestOfCreationAndUpdateTime();
    }

    protected List<String> getEhCacheKeys() {
        return Arrays.asList("foo", "bar"); // STASHED: GenericUtils.convertToTypedList(getEhCache().getKeys(), String.class);
    }

    protected void cacheObject(final String cacheId, final Object object) {
        if (object == null) {
            log.warn("I was asked to cache an object with ID '{}' but the object was null.", cacheId);
            return;
        }
        log.trace("Request to cache entry '{}', evaluating", cacheId);
        if (has(cacheId)) {
            log.trace("Cache entry '{}' exists and force update not specified, evaluating for change", cacheId);
            final Object existing = getCachedObject(cacheId, Object.class);
            if (object.equals(existing)) {
                log.trace("Existing and updated cached objects for entry '{}' are identical, returning without updating", cacheId);
                return;
            }
            log.trace("Cache entry '{}' already exists but differs from updated cache item:\n\nExisting:\n{}\nUpdated:\n{}", cacheId, existing, object);
        }
        forceCacheObject(cacheId, object);
    }

    protected void forceCacheObject(final String cacheId, final Object object) {
        final Object candidate = object instanceof Provider ? ((Provider<?>) object).get() : object;
        final Object target;
        if (candidate instanceof Map) {
            //noinspection rawtypes,unchecked
            target = checkMapForNullKey(cacheId, (Map) candidate);
        } else if (candidate instanceof Multimap) {
            //noinspection rawtypes,unchecked
            target = checkMultimapForNullKey(cacheId, (Multimap) candidate).asMap();
        } else {
            target = candidate;
        }
        log.trace("Storing cache entry '{}' with object of type: {}", cacheId, target.getClass().getName());
        getCache().put(cacheId, (ItemI) target); // STASHED: getCache().put(cacheId, target);
    }

    protected <T> T getCachedObject(final String cacheId, final Class<? extends T> type) {
        try {
            return (T) getCache().get(cacheId); // STASHED: return getCache().get(cacheId, type);
        } catch (IllegalStateException e) {
            log.error("Got an IllegalStateException trying to retrieve cache ID '{}' as an object of type {}", cacheId, type.getName(), e);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> getCachedList(final String cacheId) {
        final List<T> elements = getCachedObject(cacheId, List.class);
        if (elements != null) {
            log.trace("Found cached list containing {} items for cache ID '{}'", elements.size(), cacheId);
            return ImmutableList.copyOf(elements);
        }
        log.trace("Got a request for cached list '{}', but when I retrieved the entry it was null.", cacheId);
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected <T> Set<T> getCachedSet(final String cacheId) {
        final Set<T> elements = getCachedObject(cacheId, Set.class);
        if (elements != null) {
            log.trace("Found cached set containing {} items for cache ID '{}'", elements.size(), cacheId);
            return ImmutableSet.copyOf(elements);
        }
        log.trace("Got a request for cached set '{}', but when I retrieved the entry it was null.", cacheId);
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected <K, V> Map<K, V> getCachedMap(final String cacheId) {
        final Map<K, V> map = getCachedObject(cacheId, Map.class);
        if (map != null) {
            log.trace("Found cached map containing {} items for cache ID '{}'", map.size(), cacheId);
            return ImmutableMap.copyOf(map);
        }
        log.trace("Got a request for cached map '{}', but when I retrieved the entry it was null.", cacheId);
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected <K, V> ListMultimap<K, V> getCachedListMultimap(final String cacheId) {
        final ArrayListMultimap<K, V> map = getCachedObject(cacheId, ArrayListMultimap.class);
        if (map != null) {
            log.trace("Found cached map containing {} items for cache ID '{}'", map.size(), cacheId);
            return map;
        }
        log.trace("Got a request for cached map '{}', but when I retrieved the entry it was null.", cacheId);
        return null;
    }

    /**
     * As the name implies, this clears the contents of the cache.
     */
    protected void clearCache() {
        log.info("Clearing the contents of the {} cache.", getCacheName());
        getCache().clear();
    }

    protected <K, V> Map<K, V> buildImmutableMap(final List<Map<K, V>> maps) {
        // The keys set keeps track of keys that have already been added
        // so that we don't add them again.
        final Set<K> keys = new HashSet<>();

        final ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        for (final Map<K, V> map : maps) {
            builder.putAll(Maps.filterKeys(map, entry -> !keys.contains(entry)));
            keys.addAll(map.keySet());
        }
        final ImmutableMap<K, V> map = builder.build();
        log.debug("Created immutable map combining {} maps, resulting in {} total entries", maps.size(), map.size());
        return map;
    }

    @SuppressWarnings("unchecked")
    protected <T> Set<T> buildImmutableSet(final Collection<T>... collections) {
        final ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        for (final Collection<T> list : collections) {
            builder.addAll(list);
        }
        return builder.build();
    }

    protected void evict(final List<String> cacheIds) {
        for (final String cacheId : cacheIds) {
            evict(cacheId);
        }
    }

    protected void evict(final String cacheId) {
        log.debug("Evicting cache entry '{}'", cacheId);
        // STASHED: getCache().evict(cacheId);
    }

    /**
     * Indicates whether the specified project ID or alias is already cached.
     *
     * @param cacheId The ID or alias of the project to check.
     *
     * @return Returns true if the ID or alias is mapped to a project cache entry, false otherwise.
     */
    private boolean has(final String cacheId) {
        return getCache().get(cacheId) != null;
    }

    private void initializeCaches() {
        _cacheMap.forEach((cacheName, keyAndValueTypes) -> getCache(cacheName, keyAndValueTypes.getKey(), keyAndValueTypes.getValue()));
    }

    private void registerCacheEventListener() {
        // STASHED:
        /*
        final Object nativeCache = getCache().getNativeCache();
        if (nativeCache instanceof net.sf.ehcache.Cache) {
            ((net.sf.ehcache.Cache) nativeCache).getCacheEventNotificationService().registerListener(this);
            log.debug("Registered user project cache as net.sf.ehcache.Cache listener");
        } else {
            log.warn("I don't know how to handle the native cache type {}", nativeCache.getClass().getName());
        }
        */
    }

    private static <K, V> Multimap<K, V> checkMultimapForNullKey(final String cacheId, final Multimap<K, V> map) {
        try {
            if (map.containsKey(null)) {
                log.warn("I was asked to cache a multimap with the ID {}, but this multimap has a null key. This could indicate a corrupt index hash in the database. Removing to allow execution to proceed. The value(s) stored under the null key are: {}", cacheId, map.removeAll(null));
            }
            return map;
        } catch (UnsupportedOperationException e) {
            log.warn("I was asked to cache a multimap with the ID {}, but this multimap has a null key. This could indicate a corrupt index hash in the database. The multimap is immutable, so I'm creating a copy without the null key to allow execution to proceed. The value(s) stored under the null key are: {}", cacheId, map.get(null));
            return ImmutableMultimap.copyOf(Multimaps.filterKeys(map, Objects::nonNull));
        }
    }

    private static <K, V> Map<K, V> checkMapForNullKey(final String cacheId, final Map<K, V> map) {
        try {
            if (map.containsKey(null)) {
                log.warn("I was asked to cache a map with the ID {}, but this map has a null key. This could indicate a corrupt index hash in the database. Removing to allow execution to proceed. The value stored under the null key is: {}", cacheId, map.remove(null));
            }
            return map;
        } catch (UnsupportedOperationException e) {
            log.warn("I was asked to cache a map with the ID {}, but this map has a null key. This could indicate a corrupt index hash in the database. The map is immutable, so I'm creating a copy without the null key to allow execution to proceed. The value stored under the null key is: {}", cacheId, map.get(null));
            return ImmutableMap.copyOf(Maps.filterKeys(map, Objects::nonNull));
        }
    }
}
