package com.bi.query;

import com.bi.exceptions.InvalidQueryException;
import com.bi.interfaces.IQueryService;
import com.bi.repository.DataRepositoryImpl;
import com.bi.util.Dataset;
import com.bi.util.FilterSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * QueryServiceImpl executes in-memory queries over datasets from the repository.
 */
public class QueryServiceImpl implements IQueryService {
    private DataRepositoryImpl repository;
    private FilterSet activeFilterSet = null;
    private Dataset lastQueryResult = null;
    private List<String> queryLog = new ArrayList<>();
    private int queryCounter = 0;

    /**
     * Constructs a query service with a repository reference.
     *
     * @param repository the repository queried by this service
     */
    public QueryServiceImpl(DataRepositoryImpl repository) {
        this.repository = repository;
    }

    /**
     * Combines all repository datasets into one queryable dataset.
     *
     * @return a dataset containing merged records from all datasets
     * @throws InvalidQueryException if repository has no datasets
     */
    @Override
    public Dataset parseQuery() {
        List<Dataset> allDatasets = repository.index();
        if (allDatasets.isEmpty()) {
            throw new InvalidQueryException("No datasets available in repository to query.");
        }

        List<Map<String, Object>> combined = new ArrayList<>();
        for (Dataset dataset : allDatasets) {
            try {
                combined.addAll(safeCastToRecordList(dataset.getData()));
            } catch (InvalidQueryException e) {
                System.out.println("[Query] Warning: Skipped dataset due to unsupported data format: " + dataset.getId());
            }
        }

        Dataset result = new Dataset(
                "QUERY-ALL-" + LocalDateTime.now(),
                combined,
                LocalDateTime.now(),
                "Combined query across " + allDatasets.size() + " datasets");

        System.out.println("[Query] parseQuery(): Combined " + combined.size() + " total records.");
        return result;
    }

    /**
     * Applies the filter definition to be used by the next executeQuery call.
     *
     * @param filterSet the filter definition
     * @throws InvalidQueryException if filter is null
     */
    @Override
    public void applyFilters(FilterSet filterSet) {
        if (filterSet == null) {
            throw new InvalidQueryException("FilterSet cannot be null.");
        }
        this.activeFilterSet = filterSet;
        System.out.println("[Query] Filter applied: " + filterSet);
    }

    /**
     * Executes a query using merged repository data and optional active filters.
     *
     * @param queryParams textual query parameters
     * @return the resulting dataset after applying optional filters
     * @throws InvalidQueryException if params are invalid, data format unsupported, or operator unknown
     */
    @Override
    public Dataset executeQuery(String queryParams) {
        if (queryParams == null || queryParams.isBlank()) {
            throw new InvalidQueryException("Query parameters cannot be null or empty.");
        }

        queryCounter++;
        queryLog.add(queryCounter + ": " + queryParams);
        System.out.println("[Query] Executing query #" + queryCounter + " with params: " + queryParams);

        Dataset baseDataset;
        try {
            baseDataset = parseQuery();
        } catch (InvalidQueryException e) {
            throw e;
        }

        List<Map<String, Object>> records;
        try {
            records = safeCastToRecordList(baseDataset.getData());
        } catch (InvalidQueryException e) {
            throw new InvalidQueryException("Query result data is in an unsupported format.", e);
        }

        List<Map<String, Object>> filteredRecords;
        if (activeFilterSet != null) {
            filteredRecords = applyActiveFilter(records, activeFilterSet);
        } else {
            filteredRecords = new ArrayList<>(records);
        }

        Dataset result = new Dataset(
                "QUERY-" + String.format("%03d", queryCounter),
                filteredRecords,
                LocalDateTime.now(),
                "Query result for: " + queryParams);

        lastQueryResult = result;
        System.out.println("[Query] Query #" + queryCounter + " complete. " + filteredRecords.size() + " records returned.");
        return result;
    }

    /**
     * Returns the most recent query result dataset.
     *
     * @return the latest query result
     * @throws InvalidQueryException if no query has executed yet
     */
    public Dataset getLastQueryResult() {
        if (lastQueryResult == null) {
            throw new InvalidQueryException("No query has been executed yet.");
        }
        return lastQueryResult;
    }

    /**
     * Returns the history log of executed queries.
     *
     * @return query log entries
     */
    public List<String> getQueryLog() {
        return queryLog;
    }

    /**
     * Clears the active filter definition.
     */
    public void clearFilters() {
        activeFilterSet = null;
        System.out.println("[Query] Active filters cleared.");
    }

    /**
     * Casts payload to record list used for query operations.
     *
     * @param rawData raw dataset payload
     * @return record list
     * @throws InvalidQueryException if payload type is unsupported
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> safeCastToRecordList(Object rawData) {
        try {
            return (List<Map<String, Object>>) rawData;
        } catch (ClassCastException e) {
            throw new InvalidQueryException("Unsupported record payload format.", e);
        }
    }

    /**
     * Applies active filter logic against candidate records.
     *
     * @param records candidate records
     * @param filter filter definition
     * @return filtered list
     * @throws InvalidQueryException if operator is unsupported
     */
    private List<Map<String, Object>> applyActiveFilter(List<Map<String, Object>> records, FilterSet filter) {
        String key = filter.getFilterKey();
        String value = filter.getFilterValue();
        String operator = filter.getFilterOperator();

        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> record : records) {
            Object candidate = record.get(key);
            boolean include;

            if ("EQUALS".equals(operator)) {
                include = candidate != null && candidate.toString().equals(value);
            } else if ("CONTAINS".equals(operator)) {
                include = candidate != null && candidate.toString().contains(value);
            } else if ("GREATER_THAN".equals(operator)) {
                include = isGreaterThan(candidate, value);
            } else if ("LESS_THAN".equals(operator)) {
                include = isLessThan(candidate, value);
            } else {
                throw new InvalidQueryException("Unknown filter operator: " + operator);
            }

            if (include) {
                filtered.add(record);
            }
        }

        return filtered;
    }

    /**
     * Compares candidate value against threshold using greater-than logic.
     *
     * @param candidate record value
     * @param threshold threshold text value
     * @return true when candidate parses and is greater than threshold
     */
    private boolean isGreaterThan(Object candidate, String threshold) {
        try {
            return candidate != null && Double.parseDouble(candidate.toString()) > Double.parseDouble(threshold);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Compares candidate value against threshold using less-than logic.
     *
     * @param candidate record value
     * @param threshold threshold text value
     * @return true when candidate parses and is less than threshold
     */
    private boolean isLessThan(Object candidate, String threshold) {
        try {
            return candidate != null && Double.parseDouble(candidate.toString()) < Double.parseDouble(threshold);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
