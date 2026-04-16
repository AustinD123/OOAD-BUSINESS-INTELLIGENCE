package com.bi;

import com.bi.analytics.AnalyticsServiceImpl;
import com.bi.exceptions.AnalyticsException;
import com.bi.exceptions.InvalidQueryException;
import com.bi.query.QueryServiceImpl;
import com.bi.util.Dataset;
import com.bi.util.FilterSet;
import java.util.List;
import java.util.Map;

/**
 * Phase3Runner demonstrates analytics and query services over repository data.
 */
public class Phase3Runner {
    private BIDataPipeline pipeline;
    private AnalyticsServiceImpl analyticsService;
    private QueryServiceImpl queryService;

    /**
     * Constructs and wires Phase 3 services after loading repository data.
     */
    public Phase3Runner() {
        this.pipeline = new BIDataPipeline();
        this.pipeline.runAllPipelines();
        this.analyticsService = new AnalyticsServiceImpl(this.pipeline.getRepository());
        this.queryService = new QueryServiceImpl(this.pipeline.getRepository());
    }

    /**
     * Runs analysis, forecast, and trend computation for every dataset in repository.
     */
    public void demonstrateAnalytics() {
        List<Dataset> all = pipeline.getRepository().index();
        for (Dataset dataset : all) {
            try {
                System.out.println("Analyzing dataset: " + dataset.getId());
                System.out.println("  Analysis: " + analyticsService.analyze(dataset));
                System.out.println("  Forecast: " + analyticsService.forecast(dataset));
                System.out.println("  Trend: " + analyticsService.getTrends(dataset));
            } catch (AnalyticsException e) {
                System.err.println("[Phase3Runner] Analytics error for dataset " + dataset.getId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Executes parse and filtered query flows with all supported filter operators.
     */
    public void demonstrateQuery() {
        try {
            Dataset parsed = queryService.parseQuery();
            System.out.println("Parsed total records: " + countRecords(parsed));
        } catch (InvalidQueryException e) {
            System.err.println("[Phase3Runner] parseQuery error: " + e.getMessage());
        }

        try {
            Dataset allResult = queryService.executeQuery("ALL");
            System.out.println("Query ALL result size: " + countRecords(allResult));
        } catch (InvalidQueryException e) {
            System.err.println("[Phase3Runner] executeQuery ALL error: " + e.getMessage());
        }

        try {
            FilterSet equalsFilter = new FilterSet("carModel", "Sedan", "EQUALS");
            queryService.applyFilters(equalsFilter);
            Dataset equalsResult = queryService.executeQuery("Filter by carModel = Sedan");
            System.out.println("EQUALS filter result size: " + countRecords(equalsResult));
        } catch (InvalidQueryException e) {
            System.err.println("[Phase3Runner] EQUALS filter error: " + e.getMessage());
        }

        try {
            FilterSet containsFilter = new FilterSet("department", "Sales", "CONTAINS");
            queryService.applyFilters(containsFilter);
            Dataset containsResult = queryService.executeQuery("Filter department contains Sales");
            System.out.println("CONTAINS filter result size: " + countRecords(containsResult));
        } catch (InvalidQueryException e) {
            System.err.println("[Phase3Runner] CONTAINS filter error: " + e.getMessage());
        }

        try {
            FilterSet greaterThanFilter = new FilterSet("revenue", "50000", "GREATER_THAN");
            queryService.applyFilters(greaterThanFilter);
            Dataset greaterThanResult = queryService.executeQuery("Filter revenue > 50000");
            System.out.println("GREATER_THAN filter result size: " + countRecords(greaterThanResult));
        } catch (InvalidQueryException e) {
            System.err.println("[Phase3Runner] GREATER_THAN filter error: " + e.getMessage());
        }

        try {
            FilterSet lessThanFilter = new FilterSet("salary", "70000", "LESS_THAN");
            queryService.applyFilters(lessThanFilter);
            Dataset lessThanResult = queryService.executeQuery("Filter salary < 70000");
            System.out.println("LESS_THAN filter result size: " + countRecords(lessThanResult));
        } catch (InvalidQueryException e) {
            System.err.println("[Phase3Runner] LESS_THAN filter error: " + e.getMessage());
        }
    }

    /**
     * Entry point for running full Phase 3 demonstrations.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Phase3Runner runner = new Phase3Runner();
        System.out.println("=== Phase 3: Analytics Demo ===");
        runner.demonstrateAnalytics();
        System.out.println("=== Phase 3: Query Demo ===");
        runner.demonstrateQuery();
        System.out.println("=== Phase 3 Complete ===");
    }

    /**
     * Counts records in a dataset payload when payload format is a record list.
     *
     * @param dataset the dataset whose records are counted
     * @return the number of records, or 0 when format is unsupported
     */
    @SuppressWarnings("unchecked")
    private int countRecords(Dataset dataset) {
        if (dataset == null || dataset.getData() == null) {
            return 0;
        }
        try {
            return ((List<Map<String, Object>>) dataset.getData()).size();
        } catch (ClassCastException e) {
            return 0;
        }
    }
}
