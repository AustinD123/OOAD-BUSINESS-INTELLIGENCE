package com.bi.interfaces;

import com.bi.util.Dataset;
import com.bi.util.FilterSet;

public interface IQueryService {
    Dataset executeQuery(String queryParams);

    void applyFilters(FilterSet filterSet);

    Dataset parseQuery();
}
