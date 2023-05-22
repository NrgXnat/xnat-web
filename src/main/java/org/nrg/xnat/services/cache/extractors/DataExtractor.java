package org.nrg.xnat.services.cache.extractors;

public interface DataExtractor<K, V> {
    String PARAM_DATA_TYPE     = "dataType";
    String PARAM_EXPERIMENT_ID = "experimentId";
    String PARAM_PROJECT_ID    = "projectId";
    String PARAM_PROJECT_IDS   = "projectIds";
    String PARAM_SUBJECT_ID    = "subjectId";
    String PARAM_USERNAME      = "username";
    String PARAM_USER_IDS      = "userIds";

    String getCacheGroup();

    String getCacheName();

    Class<K> getKeyType();

    Class<V> getValueType();

    /**
     * If the {@link #getValueType() value type} for this extractor is <em>not</em> a map, this method is ignored. If
     * the value <em>is</em> a map, the value returned here indicates that the map should be partitioned and stored by
     * its individual keys rather than as a whole map. Due to type erasure, the cache creation process can't easily
     * determine the value type of the map, so it should be supplied here. In order for a partitioned map to work
     * properly, the cache <em>and</em> map keys <em>must</em> be strings.
     * <p>
     * The main use for a partitioned map is when the value type is a collection of some sort, like a list or set.
     * <p>
     * The default implementation of this method returns <pre>null</pre>.
     *
     * @return Returns the map's value type if the map should be partitioned.
     */
    <T> Class<T> getPartitionValueType();

    /**
     * Indicates whether the
     * @return
     */
    boolean isPartitionedMap();

    V extract(Object... parameters);
}
