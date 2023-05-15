package org.nrg.xnat.eventservice.daos;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.ajax.Filter;
import org.nrg.framework.ajax.hibernate.HibernateFilter;
import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.nrg.framework.orm.hibernate.QueryBuilder;
import org.nrg.xnat.eventservice.entities.SubscriptionDeliveryEntity;
import org.nrg.xnat.eventservice.entities.SubscriptionDeliverySummaryEntity;
import org.nrg.xnat.eventservice.entities.TimedEventStatusEntity;
import org.nrg.xnat.eventservice.services.SubscriptionDeliveryEntityPaginatedRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.nrg.framework.generics.GenericUtils.convertToTypedList;

@Repository
public class SubscriptionDeliveryEntityDao extends AbstractHibernateDAO<SubscriptionDeliveryEntity> {
    public List<SubscriptionDeliverySummaryEntity> getSummaryDeliveries(final String projectId) {
        final boolean hasProjectId = StringUtils.isNotBlank(projectId);
        final Query   query        = getSession().createQuery(hasProjectId ? QUERY_SUMMARY_DELIVERIES_BY_PROJECT : QUERY_SUMMARY_DELIVERIES);
        query.setParameter("statusToExclude", TimedEventStatusEntity.Status.OBJECT_FILTER_MISMATCH_HALT.ordinal());
        if (hasProjectId) {
            query.setParameter("projectId", projectId);
        }
        return convertToTypedList(query.getResultList(), SubscriptionDeliverySummaryEntity.class);
    }

    public List<SubscriptionDeliveryEntity> get(final String projectId, final Long subscriptionId, final TimedEventStatusEntity.Status statusToExclude, SubscriptionDeliveryEntityPaginatedRequest paginatedRequest) {
        paginatedRequest = paginatedRequest != null ? paginatedRequest : new SubscriptionDeliveryEntityPaginatedRequest();

        final Map<String, Filter> newFilters     = new HashMap<>();
        final Map<String, Filter> requestFilters = paginatedRequest.getFiltersMap();

        // Method parameter filters
        if (StringUtils.isNotBlank(projectId)) {
            newFilters.put("projectId", HibernateFilter.builder().operator(HibernateFilter.Operator.EQ).value(projectId).build());
        }
        if (subscriptionId != null) {
            newFilters.put("subscriptionId", HibernateFilter.builder().operator(HibernateFilter.Operator.EQ).value(subscriptionId).build());
        }
        if (statusToExclude != null) {
            newFilters.put("status", HibernateFilter.builder().operator(HibernateFilter.Operator.NE).value(statusToExclude).build());
        }

        // Request filters
        if (paginatedRequest.hasFilters()) {

            // Method projectId parameter supersedes request project filter
            if (projectId == null && requestFilters.containsKey("project")) {
                newFilters.put("projectId", requestFilters.get("project"));
            }
            if (paginatedRequest.getFiltersMap().containsKey("subscription")) {
                newFilters.put("description", requestFilters.get("subscription"));
            }
            if (requestFilters.containsKey("eventtype")) {
                newFilters.put("eventType", requestFilters.get("eventtype"));
            }
            if (requestFilters.containsKey("user")) {
                newFilters.put("actionUserLogin", requestFilters.get("user"));
            }
            if (requestFilters.containsKey("status")) {
                newFilters.put("statusMessage", requestFilters.get("status"));
            }
        }
        paginatedRequest.setFiltersMap(newFilters);

        //Sort column
        if (paginatedRequest.getSortColumn() != null && !paginatedRequest.getSortColumn().isEmpty()) {
            String sortColumn = paginatedRequest.getSortColumn();
            if (sortColumn.contentEquals("user")) {
                sortColumn = "actionUserLogin";
            } else if (sortColumn.contentEquals("status")) {
                sortColumn = "statusMessage";
            } else if (sortColumn.contentEquals("project")) {
                sortColumn = "projectId";
            } else if (sortColumn.contentEquals("eventtype")) {
                sortColumn = "eventType";
            }
            paginatedRequest.setSortColumn(sortColumn);
        }

        return findPaginated(paginatedRequest);
    }

    public List<SubscriptionDeliveryEntity> excludeByProperty(final String property, final Object value) {
        QueryBuilder<SubscriptionDeliveryEntity> builder = newQueryBuilder();
        builder.where(value == null ? builder.isNotNull(property) : builder.ne(property, value));
        return builder.getResults();
    }

    public long count(final String projectId, final Long subscriptionId, final TimedEventStatusEntity.Status statusToExclude) {
        QueryBuilder<SubscriptionDeliveryEntity> builder = newQueryBuilder();

        final List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotBlank(projectId)) {
            predicates.add(builder.eq("projectId", projectId));
        }
        if (subscriptionId != null) {
            builder.join("subscription", "sub");
            predicates.add(builder.eq("sub.id", subscriptionId));
        }
        if (statusToExclude != null) {
            predicates.add(builder.ne("status", statusToExclude));
        }
        return builder.count(predicates);
    }

    private static final String BASE_QUERY_SUMMARY_DELIVERIES          = "SELECT NEW org.nrg.xnat.eventservice.entities.SubscriptionDeliverySummaryEntity(D.id, D.eventType, D.subscription.id, D.subscription.name, D.actionUserLogin, D.projectId, D.triggeringEventEntity.objectLabel, D.status, D.errorState, D.statusTimestamp) FROM SubscriptionDeliveryEntity as D WHERE D.status != :statusToExclude";
    private static final String BASE_QUERY_SUMMARY_DELIVERIES_ORDER_BY = " ORDER BY D.id ASC";
    private static final String QUERY_SUMMARY_DELIVERIES               = BASE_QUERY_SUMMARY_DELIVERIES + BASE_QUERY_SUMMARY_DELIVERIES_ORDER_BY;
    private static final String QUERY_SUMMARY_DELIVERIES_BY_PROJECT    = BASE_QUERY_SUMMARY_DELIVERIES + " AND D.projectId = :projectId" + BASE_QUERY_SUMMARY_DELIVERIES_ORDER_BY;
}
