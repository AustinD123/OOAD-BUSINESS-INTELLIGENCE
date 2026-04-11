package com.bi.models;

import com.bi.util.Dataset;
import java.time.LocalDateTime;

public class Query {
    private final int queryId;
    private String queryParameters;
    private final String filterType;
    private final Object resultData;
    private final LocalDateTime executionDate;

    public Query(int queryId, String queryParameters, String filterType, Object resultData, LocalDateTime executionDate) {
        this.queryId = queryId;
        this.queryParameters = queryParameters;
        this.filterType = filterType;
        this.resultData = resultData;
        this.executionDate = executionDate;
    }

    public int getQueryId() {
        return queryId;
    }

    public String getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(String queryParameters) {
        this.queryParameters = queryParameters;
    }

    public String getFilterType() {
        return filterType;
    }

    public Object getResultData() {
        return resultData;
    }

    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    public Dataset execute() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void addFilter() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
