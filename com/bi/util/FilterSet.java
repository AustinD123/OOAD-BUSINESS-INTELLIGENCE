package com.bi.util;

public class FilterSet {
    private String filterKey;
    private String filterValue;
    private String filterOperator;

    /**
     * Creates an empty filter definition.
     */
    public FilterSet() {
    }

    /**
     * Creates a filter definition with all filter parts.
     *
     * @param filterKey the record field to filter on
     * @param filterValue the value used in comparison
     * @param filterOperator the filter operator
     */
    public FilterSet(String filterKey, String filterValue, String filterOperator) {
        this.filterKey = filterKey;
        this.filterValue = filterValue;
        this.filterOperator = filterOperator;
    }

    /**
     * Returns the field name used for filtering.
     *
     * @return the filter key
     */
    public String getFilterKey() {
        return filterKey;
    }

    /**
     * Sets the field name used for filtering.
     *
     * @param filterKey the filter key
     */
    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    /**
     * Returns the comparison value for filtering.
     *
     * @return the filter value
     */
    public String getFilterValue() {
        return filterValue;
    }

    /**
     * Sets the comparison value for filtering.
     *
     * @param filterValue the filter value
     */
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    /**
     * Returns the filter operator.
     *
     * @return the operator string
     */
    public String getFilterOperator() {
        return filterOperator;
    }

    /**
     * Sets the filter operator.
     *
     * @param filterOperator the operator string
     */
    public void setFilterOperator(String filterOperator) {
        this.filterOperator = filterOperator;
    }

    /**
     * Returns a concise string for filter display.
     *
     * @return key operator value format
     */
    @Override
    public String toString() {
        return filterKey + " " + filterOperator + " " + filterValue;
    }
}
