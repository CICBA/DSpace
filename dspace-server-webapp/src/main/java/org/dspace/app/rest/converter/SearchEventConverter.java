/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.model.PageRest;
import org.dspace.app.rest.model.SearchEventRest;
import org.dspace.app.rest.model.SearchResultsRest;
import org.dspace.app.rest.utils.ScopeResolver;
import org.dspace.app.util.service.DSpaceObjectUtils;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.discovery.IndexableObject;
import org.dspace.usage.UsageEvent;
import org.dspace.usage.UsageSearchEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchEventConverter {
    /* Log4j logger */
    private static final Logger log = LogManager.getLogger(SearchEventConverter.class);

    @Autowired
    private ScopeResolver scopeResolver;

    @Autowired
    private DSpaceObjectUtils dSpaceObjectUtils;

    private final Integer[] allowedClickedObjectTypes =
            new Integer[]{Constants.COMMUNITY, Constants.COLLECTION, Constants.ITEM};

    public UsageSearchEvent convert(Context context, HttpServletRequest request, SearchEventRest searchEventRest) {
        UsageSearchEvent usageSearchEvent = new UsageSearchEvent(UsageEvent.Action.SEARCH, request, context,
                                                                             null);
        usageSearchEvent.setQuery(searchEventRest.getQuery());
        usageSearchEvent.setDsoType(searchEventRest.getDsoType());
        if (searchEventRest.getClickedObject() != null) {
            try {
                DSpaceObject clickedObject =
                        dSpaceObjectUtils.findDSpaceObject(context, searchEventRest.getClickedObject());
                if (clickedObject != null &&
                        Arrays.asList(allowedClickedObjectTypes).contains(clickedObject.getType())) {
                    usageSearchEvent.setObject(clickedObject);
                } else {
                    throw new IllegalArgumentException("UUID " + searchEventRest.getClickedObject() +
                            " was expected to resolve to a Community, Collection or Item, but didn't resolve to any");
                }
            } catch (SQLException e) {
                log.warn("Unable to retrieve DSpace Object with ID " + searchEventRest.getClickedObject() +
                        " from the database", e);
            }
        }
        if (searchEventRest.getScope() != null) {
            IndexableObject scopeObject =
                    scopeResolver.resolveScope(context, String.valueOf(searchEventRest.getScope()));
            if (scopeObject != null && scopeObject.getIndexedObject() instanceof DSpaceObject) {
                usageSearchEvent.setScope((DSpaceObject) scopeObject.getIndexedObject());
            }
        }
        usageSearchEvent.setConfiguration(searchEventRest.getConfiguration());
        if (searchEventRest.getAppliedFilters() != null) {
            usageSearchEvent.setAppliedFilters(convertAppliedFilters(searchEventRest.getAppliedFilters()));
        }
        usageSearchEvent.setSort(convertSort(searchEventRest.getSort()));
        usageSearchEvent.setPage(convertPage(searchEventRest.getPage()));

        return usageSearchEvent;

    }

    private UsageSearchEvent.Page convertPage(PageRest page) {
        return new UsageSearchEvent.Page(page.getSize(), page.getTotalElements(), page.getTotalPages(),
                                             page.getNumber());
    }

    private UsageSearchEvent.Sort convertSort(SearchResultsRest.Sorting sort) {
        return new UsageSearchEvent.Sort(sort.getBy(), sort.getOrder());
    }

    private List<UsageSearchEvent.AppliedFilter> convertAppliedFilters(
        List<SearchResultsRest.AppliedFilter> appliedFilters) {
        List<UsageSearchEvent.AppliedFilter> listToReturn = new LinkedList<>();
        for (SearchResultsRest.AppliedFilter appliedFilter : appliedFilters) {
            UsageSearchEvent.AppliedFilter convertedAppliedFilter = new UsageSearchEvent.AppliedFilter(
                appliedFilter.getFilter(), appliedFilter.getOperator(), appliedFilter.getValue(),
                appliedFilter.getLabel());
            listToReturn.add(convertedAppliedFilter);
        }
        return listToReturn;
    }
}
