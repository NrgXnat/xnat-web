package org.nrg.xnat.tracking.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.framework.services.SerializerService;
import org.nrg.xnat.tracking.daos.EventTrackingEntryDao;
import org.nrg.xnat.tracking.entities.EventTracking;
import org.nrg.xnat.tracking.exceptions.SummarizerException;
import org.nrg.xnat.tracking.model.EventLogSummary;
import org.nrg.xnat.tracking.model.EventLog;
import org.nrg.xnat.tracking.model.EventLogSubType;
import org.nrg.xnat.tracking.model.TrackableEvent;
import org.nrg.xnat.tracking.services.EventTrackingHibernateService;
import org.nrg.xnat.tracking.services.EventLogSummarizer;
import org.reflections.Reflections;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class EventTrackingHibernateServiceImpl extends AbstractHibernateEntityService<EventTracking, EventTrackingEntryDao> implements EventTrackingHibernateService {
    private final ObjectMapper objectMapper;
    private final Map<String, EventLogSummarizer> summarizerMap;

    public EventTrackingHibernateServiceImpl(final SerializerService serializerService,
                                             final Map<String, EventLogSummarizer> summarizerMap) {
        this.objectMapper = addEventLogSupportToObjectMapper(serializerService.getObjectMapper());
        this.summarizerMap = summarizerMap;
    }

    private ObjectMapper addEventLogSupportToObjectMapper(final ObjectMapper objectMapper) {
        final Reflections reflections = new Reflections(EventLog.class.getPackage().getName());
        final Set<Class<?>> subtypes = reflections.getTypesAnnotatedWith(EventLogSubType.class);
        for (Class<?> subType : subtypes) {
            final EventLogSubType annotation = subType.getAnnotation(EventLogSubType.class);
            if (annotation != null) {
                final String typeName = annotation.value();
                objectMapper.registerSubtypes(new NamedType(subType, typeName));
            }
        }

        return objectMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void create(final TrackableEvent eventData) throws JsonProcessingException {
        final EventTracking.EventTrackingBuilder builder = EventTracking.builder()
                .key(eventData.getTrackingId())
                .userId(eventData.getUserId())
                .eventTime(eventData.getEventTime())
                .eventLog(objectMapper.writeValueAsString(eventData.getEventLog()));
        if (eventData.isCompleted()) {
            builder.succeeded(eventData.isSuccess())
                    .finalMessage(eventData.getMessage());
        }
        create(builder.build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public EventLogSummary getSummaryForKey(final String key, final Integer userId)
            throws NotFoundException, JsonProcessingException, SummarizerException {
        final List<EventTracking> eventTrackingList = getDao().findByKeyAndUserId(key, userId);
        if (eventTrackingList.isEmpty()) {
            throw new NotFoundException("No event listener data with key " + key + " accessible to user");
        }
        return buildSummaryFromEventList(eventTrackingList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteEntriesOlderThan(final Date expiration) {
        getDao().deleteEntriesLastUpdatedBefore(expiration);
    }

    private EventLogSummary buildSummaryFromEventList(final List<EventTracking> eventTrackingList)
            throws JsonProcessingException, SummarizerException {
        final EventLog examplePayload = objectMapper.readValue(eventTrackingList.get(0).getEventLog(),
                EventLog.class);
        final String payloadClass = examplePayload.getClass().getName();
        final EventLogSummarizer summarizer = summarizerMap.get(payloadClass);
        if (summarizer == null) {
            throw new RuntimeException("No summarizer for payload type: " + payloadClass);
        }
        return summarizer.summarize(eventTrackingList, objectMapper);
    }
}
