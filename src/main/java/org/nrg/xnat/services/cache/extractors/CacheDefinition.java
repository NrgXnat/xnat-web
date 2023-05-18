package org.nrg.xnat.services.cache.extractors;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to support extracting some kind of data and storing it in a cache.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface CacheDefinition {
    /**
     * Indicates the name of the cache that will contain the data from this extractor.
     *
     * @return The name of the target cache.
     */
    @AliasFor("cacheName")
    String value();

    /**
     * Indicates the type of key to be used for the cache. This defaults to <pre>String</pre>.
     *
     * @return The type of the cache key.
     */
    Class<?> keyType() default String.class;

    /**
     * Indicates the type of value to be stored in the cache. This defaults to <pre>String</pre>.
     *
     * @return The type of the cache value.
     */
    Class<?> valueType() default String.class;
}
