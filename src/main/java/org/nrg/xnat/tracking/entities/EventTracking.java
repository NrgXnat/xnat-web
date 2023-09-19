package org.nrg.xnat.tracking.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import javax.annotation.Nullable;
import javax.persistence.*;

@Slf4j
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(indexes = @Index(columnList = "key, userId"))
@NamedQueries({
        @NamedQuery(name = EventTracking.FIND_BY_KEY_AND_USER,
                query = "SELECT e FROM EventTracking e WHERE e.key = :key AND e.userId = :userId ORDER BY e.eventTime ASC"),
        @NamedQuery(name = EventTracking.DELETE_OLD_ENTRIES,
                query = "DELETE FROM EventTracking e WHERE e.timestamp < :expiration")
})
public class EventTracking extends AbstractHibernateEntity {
    public static final String FIND_BY_KEY_AND_USER = "findByKeyAndUserId";
    public static final String DELETE_OLD_ENTRIES = "deleteEntriesLastUpdatedBefore";

    private String key;
    private Integer userId;
    private Long eventTime;
    @Nullable private String eventLog; // Use a payload string so we can serialize multiple subtypes of EventLog
    @Nullable private Boolean succeeded;
    @Nullable private String finalMessage;

    @Column(columnDefinition = "TEXT")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Nullable
    @Column(columnDefinition = "TEXT")
    public String getEventLog() {
        return eventLog;
    }

    public void setEventLog(@Nullable String payload) {
        this.eventLog = payload;
    }

    @Nullable
    public Boolean getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(@Nullable Boolean succeeded) {
        this.succeeded = succeeded;
    }

    @Nullable
    @Column(columnDefinition = "TEXT")
    public String getFinalMessage() {
        return finalMessage;
    }

    public void setFinalMessage(@Nullable String finalMessage) {
        this.finalMessage = finalMessage;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public boolean hasCompleted() {
        return succeeded != null;
    }
}
