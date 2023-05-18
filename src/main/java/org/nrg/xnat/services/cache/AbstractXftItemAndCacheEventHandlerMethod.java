package org.nrg.xnat.services.cache;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.framework.jcache.DefaultGenericCacheEntryListener;
import org.nrg.framework.jcache.GenericCacheEventListener;
import org.nrg.framework.jcache.JCacheHelper;
import org.nrg.xft.ItemI;
import org.nrg.xft.event.XftItemEventI;
import org.nrg.xft.event.methods.AbstractXftItemEventHandlerMethod;
import org.nrg.xft.event.methods.XftItemEventCriteria;
import org.nrg.xnat.services.cache.extractors.CacheDefinition;
import org.nrg.xnat.services.cache.extractors.DataExtractor;

import javax.cache.Cache;
import javax.inject.Provider;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final Map<String, DataExtractor<?, ?>>               _extractors;
    private final List<GenericCacheEventListener<String, ItemI>> _cacheEventListeners;

    /**
     * Creates the super class using the default <b>CacheEventListenerAdapter</b> implementation for the underlying default functionality.
     */
    protected AbstractXftItemAndCacheEventHandlerMethod(final JCacheHelper cacheHelper, final XftItemEventCriteria first, final XftItemEventCriteria... criteria) {
        this(cacheHelper, Collections.emptyList(), Collections.emptyList(), first, criteria);
    }

    protected AbstractXftItemAndCacheEventHandlerMethod(final JCacheHelper cacheHelper, final List<DataExtractor<?, ?>> extractors, final XftItemEventCriteria first, final XftItemEventCriteria... criteria) {
        this(cacheHelper, extractors, Collections.emptyList(), first, criteria);
    }

    /**
     * Creates the super class using the submitted <b>CacheEventListener</b> instance for the underlying default functionality.
     */
    @SuppressWarnings("unused")
    protected AbstractXftItemAndCacheEventHandlerMethod(final JCacheHelper cacheHelper, final GenericCacheEventListener<String, ItemI> cacheEventListener, final XftItemEventCriteria first, final XftItemEventCriteria... criteria) {
        this(cacheHelper, Collections.emptyList(), Collections.singletonList(cacheEventListener), first, criteria);
    }

    /**
     * Creates the super class using the submitted <b>CacheEventListener</b> instance for the underlying default functionality.
     */
    protected AbstractXftItemAndCacheEventHandlerMethod(final JCacheHelper cacheHelper, final List<DataExtractor<?, ?>> extractors, final List<GenericCacheEventListener<String, ItemI>> cacheEventListeners, final XftItemEventCriteria first, final XftItemEventCriteria... criteria) {
        super(first, criteria);
        _cacheHelper         = cacheHelper;
        _extractors          = extractors.stream().collect(Collectors.toMap(EXTRACTOR_CACHE_NAME, Function.identity()));
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

    protected <K, V> Cache<K, V> getCache(final String cacheName, final Class<K> keyType, final Class<V> valueType) {
        return _cacheHelper.getCache(cacheName, keyType, valueType);
    }

    protected <K, V> Cache<K, V> getCache(final String cacheName) {
        return _cacheHelper.getCache(cacheName);
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

    protected List<Object> evict(final String cacheName, final List<String> cacheIds) {
        return cacheIds.stream().map(cacheId -> evict(cacheName, cacheId)).collect(Collectors.toList());
    }

    protected Object evict(final String cacheName, final String cacheId) {
        log.debug("Evicting cache entry '{}' from cache {}", cacheId, cacheName);
        return getCache(cacheName).getAndRemove(cacheId);
    }

    private void initializeCaches() {
        _extractors.forEach((cacheName, extractor) -> {
            final CacheDefinition annotation = extractor.getClass().getAnnotation(CacheDefinition.class);
            getCache(cacheName, annotation.keyType(), annotation.valueType());
        });
    }

    private void initializeCache(final DataExtractor<?, ?> extractor) {
        // CACHING: Implement this
        // getCache(cacheName, keyAndValueTypes.getKey(), keyAndValueTypes.getValue())
    }

    private void registerCacheEventListener() {
        // CACHING: Implement this
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

    private static final Function<DataExtractor<?, ?>, String> EXTRACTOR_CACHE_NAME = extractor -> {
        final CacheDefinition cacheDefinition = extractor.getClass().getAnnotation(CacheDefinition.class);
        if (cacheDefinition == null) {
            throw new NrgServiceRuntimeException(NrgServiceError.Instantiation, "All extractors must be annotated with @CacheDefinition, but " + extractor.getClass().getName() + " is not");
        }
        return cacheDefinition.value();
    };
}
