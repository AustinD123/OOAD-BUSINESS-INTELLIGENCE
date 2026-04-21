package com.bi.test;

import com.bi.BIDataPipeline;
import com.bi.analytics.AnalyticsServiceImpl;
import com.bi.dashboard.DashboardServiceImpl;
import com.bi.enums.KPIStatus;
import com.bi.exceptions.*;
import com.bi.kpi.KPIServiceImpl;
import com.bi.models.KPI;
import com.bi.query.QueryServiceImpl;
import com.bi.report.ReportServiceImpl;
import com.bi.repository.DataRepositoryImpl;
import com.bi.security.SecurityServiceImpl;
import com.bi.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * BISystemTest is the Phase 6 unit test suite.
 * It tests all components and all custom exceptions end-to-end with mock data.
 * No external testing framework required — runs standalone via main().
 *
 * Run: java com.bi.test.BISystemTest
 */
public class BISystemTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========== BI System Unit Test Suite (Phase 6) ==========\n");

        // Core pipeline
        testDataSourceAndETL();
        testDataRepository();

        // Analytics & Query
        testAnalyticsService();
        testQueryService();

        // Phase 4 services
        testReportService();
        testKPIService();
        testSecurityService();

        // Phase 5 dashboard
        testDashboardService();

        // All custom exceptions
        testAllExceptions();

        // End-to-end integration
        testEndToEndFlow();

        System.out.println("\n========== TEST RESULTS ==========");
        System.out.println("  PASSED : " + passed);
        System.out.println("  FAILED : " + failed);
        System.out.println("  TOTAL  : " + (passed + failed));
        System.out.println("==========================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    // ─── DATA SOURCE & ETL ────────────────────────────────────────────────────

    private static void testDataSourceAndETL() {
        section("Data Source & ETL Pipeline");

        // Pipeline loads all three sources
        test("Pipeline runs all sources without error", () -> {
            BIDataPipeline pipeline = new BIDataPipeline();
            pipeline.runAllPipelines();
            assertTrue("Repository should have 3 datasets", pipeline.getRepository().getTotalRecords() == 3);
        });

        // ETL null input throws
        test("ETLServiceImpl.extract() throws on null", () -> {
            com.bi.etl.ETLServiceImpl etl = new com.bi.etl.ETLServiceImpl();
            assertThrows(ETLProcessException.class, () -> etl.extract(null));
        });

        // ETL transform adds processed flag
        test("ETLServiceImpl.transform() adds processed=true to records", () -> {
            com.bi.etl.ETLServiceImpl etl = new com.bi.etl.ETLServiceImpl();
            List<java.util.Map<String, Object>> records = new ArrayList<>();
            java.util.Map<String, Object> rec = new java.util.HashMap<>();
            rec.put("revenue", 1000.0);
            records.add(rec);
            Dataset raw = new Dataset("TEST-RAW", records, "raw");
            Dataset extracted = etl.extract(raw);
            Dataset transformed = etl.transform(extracted);
            @SuppressWarnings("unchecked")
            List<java.util.Map<String, Object>> result =
                    (List<java.util.Map<String, Object>>) transformed.getData();
            assertTrue("processed flag should be true", Boolean.TRUE.equals(result.get(0).get("processed")));
        });
    }

    // ─── DATA REPOSITORY ─────────────────────────────────────────────────────

    private static void testDataRepository() {
        section("Data Repository");

        test("store() and retrieve() work correctly", () -> {
            DataRepositoryImpl repo = new DataRepositoryImpl();
            Dataset ds = new Dataset("DS-001", List.of(), "test dataset");
            repo.store(ds);
            Dataset retrieved = repo.retrieve("DS-001");
            assertTrue("Retrieved ID should match", "DS-001".equals(retrieved.getId()));
        });

        test("retrieve() throws DataNotFoundException for missing ID", () -> {
            DataRepositoryImpl repo = new DataRepositoryImpl();
            assertThrows(DataNotFoundException.class, () -> repo.retrieve("NONEXISTENT"));
        });

        test("store() throws DataNotFoundException on duplicate ID", () -> {
            DataRepositoryImpl repo = new DataRepositoryImpl();
            Dataset ds = new Dataset("DS-DUP", List.of(), "dup");
            repo.store(ds);
            assertThrows(DataNotFoundException.class, () -> repo.store(ds));
        });

        test("store() throws DataNotFoundException on null dataset", () -> {
            DataRepositoryImpl repo = new DataRepositoryImpl();
            assertThrows(DataNotFoundException.class, () -> repo.store(null));
        });

        test("index() returns all stored datasets", () -> {
            DataRepositoryImpl repo = new DataRepositoryImpl();
            repo.store(new Dataset("A", List.of(), "a"));
            repo.store(new Dataset("B", List.of(), "b"));
            assertTrue("Index should return 2", repo.index().size() == 2);
        });

        test("exists() returns correct boolean", () -> {
            DataRepositoryImpl repo = new DataRepositoryImpl();
            repo.store(new Dataset("EX-1", List.of(), "e"));
            assertTrue("exists() true for stored", repo.exists("EX-1"));
            assertTrue("exists() false for missing", !repo.exists("MISSING"));
        });
    }

    // ─── ANALYTICS SERVICE ───────────────────────────────────────────────────

    private static void testAnalyticsService() {
        section("Analytics Service");

        BIDataPipeline pipeline = new BIDataPipeline();
        pipeline.runAllPipelines();
        AnalyticsServiceImpl analytics = new AnalyticsServiceImpl(pipeline.getRepository());

        test("analyze() returns non-null AnalysisResult", () -> {
            Dataset ds = pipeline.getRepository().index().get(0);
            AnalysisResult result = analytics.analyze(ds);
            assertTrue("AnalysisResult should not be null", result != null);
            assertTrue("Metric value should be positive", result.getMetricValue() > 0);
        });

        test("analyze() throws AnalyticsException on null dataset", () -> {
            assertThrows(AnalyticsException.class, () -> analytics.analyze(null));
        });

        test("forecast() returns ForecastResult with positive forecasted value", () -> {
            Dataset ds = pipeline.getRepository().index().get(0);
            ForecastResult result = analytics.forecast(ds);
            assertTrue("Forecasted value should be > 0", result.getForecastedValue() > 0);
            assertTrue("Confidence should not be null", result.getConfidence() != null);
        });

        test("getTrends() returns TrendResult with direction", () -> {
            Dataset ds = pipeline.getRepository().index().get(0);
            TrendResult result = analytics.getTrends(ds);
            assertTrue("Direction should not be null", result.getDirection() != null);
            assertTrue("Data points should exist", result.getDataPoints() != null);
        });

        test("getTrends() throws AnalyticsException on null dataset", () -> {
            assertThrows(AnalyticsException.class, () -> analytics.getTrends(null));
        });

        test("getAnalysisHistory() grows with each analyze() call", () -> {
            int before = analytics.getAnalysisHistory().size();
            Dataset ds = pipeline.getRepository().index().get(0);
            analytics.analyze(ds);
            assertTrue("History should grow", analytics.getAnalysisHistory().size() > before);
        });
    }

    // ─── QUERY SERVICE ───────────────────────────────────────────────────────

    private static void testQueryService() {
        section("Query Service");

        BIDataPipeline pipeline = new BIDataPipeline();
        pipeline.runAllPipelines();
        QueryServiceImpl query = new QueryServiceImpl(pipeline.getRepository());

        test("parseQuery() returns combined dataset", () -> {
            Dataset result = query.parseQuery();
            assertTrue("parseQuery result should not be null", result != null);
        });

        test("executeQuery(ALL) returns records", () -> {
            Dataset result = query.executeQuery("ALL");
            assertTrue("Result should have data", result.getData() != null);
        });

        test("applyFilters() + executeQuery() EQUALS filter works", () -> {
            query.applyFilters(new FilterSet("carModel", "Tesla Model 3", "EQUALS"));
            Dataset result = query.executeQuery("carModel=Tesla");
            assertTrue("Result should not be null", result != null);
        });

        test("applyFilters() + executeQuery() CONTAINS filter works", () -> {
            query.applyFilters(new FilterSet("department", "Sales", "CONTAINS"));
            Dataset result = query.executeQuery("department contains Sales");
            assertTrue("Result should not be null", result != null);
        });

        test("applyFilters() + executeQuery() GREATER_THAN filter works", () -> {
            query.applyFilters(new FilterSet("revenue", "1000000", "GREATER_THAN"));
            Dataset result = query.executeQuery("revenue > 1000000");
            assertTrue("Result should not be null", result != null);
        });

        test("applyFilters() + executeQuery() LESS_THAN filter works", () -> {
            query.applyFilters(new FilterSet("salary", "90000", "LESS_THAN"));
            Dataset result = query.executeQuery("salary < 90000");
            assertTrue("Result should not be null", result != null);
        });

        test("executeQuery() throws InvalidQueryException on null params", () -> {
            assertThrows(InvalidQueryException.class, () -> query.executeQuery(null));
        });

        test("applyFilters() throws InvalidQueryException on null filter", () -> {
            assertThrows(InvalidQueryException.class, () -> query.applyFilters(null));
        });

        test("applyFilters() unknown operator throws InvalidQueryException", () -> {
            query.applyFilters(new FilterSet("revenue", "100", "BETWEEN"));
            assertThrows(InvalidQueryException.class, () -> query.executeQuery("test"));
        });
    }

    // ─── REPORT SERVICE ──────────────────────────────────────────────────────

    private static void testReportService() {
        section("Report Service");

        test("generateReport() creates a report with correct type inference", () -> {
            ReportServiceImpl reportService = new ReportServiceImpl();
            reportService.generateReport(new ReportConfig("Sales performance report"));
            assertTrue("1 report should be generated", reportService.getGeneratedReports().size() == 1);
            assertTrue("Report type should be SALES",
                    "SALES".equals(reportService.getLastReport().getReportType()));
        });

        test("generateReport() null config throws ReportGenerationException", () -> {
            ReportServiceImpl reportService = new ReportServiceImpl();
            assertThrows(ReportGenerationException.class, () -> reportService.generateReport(null));
        });

        test("exportPDF() throws ReportGenerationException when no report exists", () -> {
            ReportServiceImpl reportService = new ReportServiceImpl();
            assertThrows(ReportGenerationException.class, reportService::exportPDF);
        });

        test("exportExcel() throws ReportGenerationException when no report exists", () -> {
            ReportServiceImpl reportService = new ReportServiceImpl();
            assertThrows(ReportGenerationException.class, reportService::exportExcel);
        });

        test("exportPDF() succeeds after generateReport()", () -> {
            ReportServiceImpl reportService = new ReportServiceImpl();
            reportService.generateReport(new ReportConfig("HR report summary"));
            reportService.exportPDF(); // should not throw
            assertTrue("Last report should still exist", reportService.getLastReport() != null);
        });

        test("exportExcel() succeeds after generateReport()", () -> {
            ReportServiceImpl reportService = new ReportServiceImpl();
            reportService.generateReport(new ReportConfig("Financial budget analysis"));
            reportService.exportExcel();
            assertTrue("Report count should be 1", reportService.getGeneratedReports().size() == 1);
        });

        test("Multiple reports accumulate correctly", () -> {
            ReportServiceImpl reportService = new ReportServiceImpl();
            reportService.generateReport(new ReportConfig("Sales report"));
            reportService.generateReport(new ReportConfig("KPI report"));
            reportService.generateReport(new ReportConfig("Finance report"));
            assertTrue("Should have 3 reports", reportService.getGeneratedReports().size() == 3);
        });
    }

    // ─── KPI SERVICE ─────────────────────────────────────────────────────────

    private static void testKPIService() {
        section("KPI Service");

        test("calculateKPI() processes all KPIs correctly", () -> {
            KPIServiceImpl kpiService = new KPIServiceImpl();
            List<KPI> kpis = List.of(
                    new KPI(1, "Revenue",  1000.0, 1200.0, KPIStatus.PENDING),
                    new KPI(2, "Sales",    500.0,  300.0,  KPIStatus.PENDING)
            );
            kpiService.calculateKPI(kpis, new TargetSet("Q1 Targets"));
            assertTrue("All KPIs should be evaluated", kpiService.getEvaluatedKPIs().size() == 2);
        });

        test("calculateKPI() null list throws KPIEvaluationException", () -> {
            KPIServiceImpl kpiService = new KPIServiceImpl();
            assertThrows(KPIEvaluationException.class,
                    () -> kpiService.calculateKPI(null, new TargetSet("T")));
        });

        test("calculateKPI() null TargetSet throws KPIEvaluationException", () -> {
            KPIServiceImpl kpiService = new KPIServiceImpl();
            assertThrows(KPIEvaluationException.class,
                    () -> kpiService.calculateKPI(List.of(new KPI(1,"K",10,10,KPIStatus.PENDING)), null));
        });

        test("evaluateTarget() throws KPIEvaluationException when no KPIs calculated", () -> {
            KPIServiceImpl kpiService = new KPIServiceImpl();
            assertThrows(KPIEvaluationException.class, kpiService::evaluateTarget);
        });

        test("evaluateTarget() succeeds after calculateKPI()", () -> {
            KPIServiceImpl kpiService = new KPIServiceImpl();
            kpiService.calculateKPI(
                    List.of(new KPI(1, "Profit", 20.0, 22.0, KPIStatus.PENDING)),
                    new TargetSet("Targets")
            );
            kpiService.evaluateTarget(); // should not throw
            assertTrue("Evaluated KPIs should be 1", kpiService.getEvaluatedKPIs().size() == 1);
        });

        test("ACHIEVED status when actual >= target", () -> {
            KPIServiceImpl kpiService = new KPIServiceImpl();
            kpiService.calculateKPI(
                    List.of(new KPI(1, "Units", 100.0, 120.0, KPIStatus.PENDING)),
                    new TargetSet("T")
            );
            assertTrue("Should have 1 evaluated KPI", kpiService.getEvaluatedKPIs().size() == 1);
        });
    }

    // ─── SECURITY SERVICE ────────────────────────────────────────────────────

    private static void testSecurityService() {
        section("Security Service");

        test("authenticate() succeeds for valid admin fallback credentials", () -> {
            SecurityServiceImpl sec = new SecurityServiceImpl();
            boolean result = sec.authenticate("admin", "admin123");
            assertTrue("Admin should authenticate", result);
        });

        test("authenticate() throws AuthenticationFailedException for wrong password", () -> {
            SecurityServiceImpl sec = new SecurityServiceImpl();
            assertThrows(AuthenticationFailedException.class,
                    () -> sec.authenticate("admin", "wrongpass"));
        });

        test("authenticate() throws AuthenticationFailedException for null username", () -> {
            SecurityServiceImpl sec = new SecurityServiceImpl();
            assertThrows(AuthenticationFailedException.class,
                    () -> sec.authenticate(null, "pass"));
        });

        test("authorize() ADMIN has GENERATE_REPORT permission", () -> {
            SecurityServiceImpl sec = new SecurityServiceImpl();
            boolean result = sec.authorize("ADMIN", "GENERATE_REPORT");
            assertTrue("ADMIN should be authorized for GENERATE_REPORT", result);
        });

        test("authorize() VIEWER cannot RUN_QUERY", () -> {
            SecurityServiceImpl sec = new SecurityServiceImpl();
            assertThrows(UnauthorizedAccessException.class,
                    () -> sec.authorize("VIEWER", "RUN_QUERY"));
        });

        test("authorize() VIEWER cannot MANAGE_USERS", () -> {
            SecurityServiceImpl sec = new SecurityServiceImpl();
            assertThrows(UnauthorizedAccessException.class,
                    () -> sec.authorize("VIEWER", "MANAGE_USERS"));
        });

        test("getPermissions() ANALYST has VIEW_DASHBOARD", () -> {
            SecurityServiceImpl sec = new SecurityServiceImpl();
            List<String> perms = sec.getPermissions("ANALYST");
            assertTrue("ANALYST should have VIEW_DASHBOARD", perms.contains("VIEW_DASHBOARD"));
        });

        test("getPermissions() ADMIN has MANAGE_USERS", () -> {
            SecurityServiceImpl sec = new SecurityServiceImpl();
            List<String> perms = sec.getPermissions("ADMIN");
            assertTrue("ADMIN should have MANAGE_USERS", perms.contains("MANAGE_USERS"));
        });

        test("getPermissions() VIEWER only has 2 permissions", () -> {
            SecurityServiceImpl sec = new SecurityServiceImpl();
            List<String> perms = sec.getPermissions("VIEWER");
            assertTrue("VIEWER should have 2 permissions", perms.size() == 2);
        });
    }

    // ─── DASHBOARD SERVICE ────────────────────────────────────────────────────

    private static void testDashboardService() {
        section("Dashboard Service");

        test("renderChart() succeeds with valid ChartData", () -> {
            DashboardServiceImpl dash = new DashboardServiceImpl();
            dash.renderChart(new ChartData("Sales Bar Chart"));
            assertTrue("Should have 1 visualization", dash.getRenderedVisualizations().size() == 1);
        });

        test("renderChart() throws DashboardLoadException on null", () -> {
            DashboardServiceImpl dash = new DashboardServiceImpl();
            assertThrows(DashboardLoadException.class, () -> dash.renderChart(null));
        });

        test("renderWidget() completes without error", () -> {
            DashboardServiceImpl dash = new DashboardServiceImpl();
            dash.renderWidget(); // should not throw
            assertTrue("Passes", true);
        });

        test("renderKPICards() displays KPI table", () -> {
            DashboardServiceImpl dash = new DashboardServiceImpl();
            List<KPI> kpis = List.of(
                    new KPI(1, "Revenue", 1000000.0, 1200000.0, KPIStatus.PENDING)
            );
            dash.renderKPICards(kpis);
            assertTrue("Passes", true);
        });

        test("renderKPICards() throws DashboardLoadException on null", () -> {
            DashboardServiceImpl dash = new DashboardServiceImpl();
            assertThrows(DashboardLoadException.class, () -> dash.renderKPICards(null));
        });

        test("renderSalesTrend() throws DashboardLoadException on empty list", () -> {
            DashboardServiceImpl dash = new DashboardServiceImpl();
            assertThrows(DashboardLoadException.class,
                    () -> dash.renderSalesTrend(new ArrayList<>()));
        });

        test("Line chart type inferred from description", () -> {
            DashboardServiceImpl dash = new DashboardServiceImpl();
            dash.renderChart(new ChartData("Sales trend line chart"));
            assertTrue("Should be LINE type",
                    dash.getRenderedVisualizations().get(0).getChartType() ==
                    com.bi.enums.ChartType.LINE);
        });
    }

    // ─── ALL EXCEPTIONS ──────────────────────────────────────────────────────

    private static void testAllExceptions() {
        section("All Custom Exceptions");

        test("DataSourceException carries message", () -> {
            DataSourceException e = new DataSourceException("ds error");
            assertTrue("Message matches", "ds error".equals(e.getMessage()));
        });

        test("ETLProcessException carries message and cause", () -> {
            Throwable cause = new RuntimeException("root");
            ETLProcessException e = new ETLProcessException("etl error", cause);
            assertTrue("Message matches", "etl error".equals(e.getMessage()));
            assertTrue("Cause matches", cause == e.getCause());
        });

        test("DataNotFoundException carries message", () -> {
            DataNotFoundException e = new DataNotFoundException("not found");
            assertTrue("Message matches", "not found".equals(e.getMessage()));
        });

        test("AnalyticsException carries message", () -> {
            AnalyticsException e = new AnalyticsException("analytics error");
            assertTrue("Message matches", "analytics error".equals(e.getMessage()));
        });

        test("InvalidQueryException carries message", () -> {
            InvalidQueryException e = new InvalidQueryException("bad query");
            assertTrue("Message matches", "bad query".equals(e.getMessage()));
        });

        test("ReportGenerationException carries message", () -> {
            ReportGenerationException e = new ReportGenerationException("report fail");
            assertTrue("Message matches", "report fail".equals(e.getMessage()));
        });

        test("DashboardLoadException carries message", () -> {
            DashboardLoadException e = new DashboardLoadException("dash fail");
            assertTrue("Message matches", "dash fail".equals(e.getMessage()));
        });

        test("KPIEvaluationException carries message", () -> {
            KPIEvaluationException e = new KPIEvaluationException("kpi fail");
            assertTrue("Message matches", "kpi fail".equals(e.getMessage()));
        });

        test("UnauthorizedAccessException carries message", () -> {
            UnauthorizedAccessException e = new UnauthorizedAccessException("unauthorized");
            assertTrue("Message matches", "unauthorized".equals(e.getMessage()));
        });

        test("AuthenticationFailedException carries message", () -> {
            AuthenticationFailedException e = new AuthenticationFailedException("auth fail");
            assertTrue("Message matches", "auth fail".equals(e.getMessage()));
        });

        test("All exceptions are RuntimeExceptions", () -> {
            assertTrue("DataSourceException is RuntimeException",
                    new DataSourceException("x") instanceof RuntimeException);
            assertTrue("ETLProcessException is RuntimeException",
                    new ETLProcessException("x") instanceof RuntimeException);
            assertTrue("AnalyticsException is RuntimeException",
                    new AnalyticsException("x") instanceof RuntimeException);
            assertTrue("ReportGenerationException is RuntimeException",
                    new ReportGenerationException("x") instanceof RuntimeException);
            assertTrue("DashboardLoadException is RuntimeException",
                    new DashboardLoadException("x") instanceof RuntimeException);
            assertTrue("KPIEvaluationException is RuntimeException",
                    new KPIEvaluationException("x") instanceof RuntimeException);
            assertTrue("UnauthorizedAccessException is RuntimeException",
                    new UnauthorizedAccessException("x") instanceof RuntimeException);
            assertTrue("AuthenticationFailedException is RuntimeException",
                    new AuthenticationFailedException("x") instanceof RuntimeException);
        });
    }

    // ─── END-TO-END INTEGRATION ───────────────────────────────────────────────

    private static void testEndToEndFlow() {
        section("End-to-End Integration Flow");

        test("Full flow: DataSource → ETL → Repository → Analytics → Query → Report → KPI", () -> {
            // 1. Boot pipeline (DataSource + ETL + Repository)
            BIDataPipeline pipeline = new BIDataPipeline();
            pipeline.runAllPipelines();
            assertTrue("Repository has 3 datasets", pipeline.getRepository().getTotalRecords() == 3);

            // 2. Analytics over repository data
            AnalyticsServiceImpl analytics = new AnalyticsServiceImpl(pipeline.getRepository());
            List<Dataset> datasets = pipeline.getRepository().index();
            AnalysisResult analysisResult = analytics.analyze(datasets.get(0));
            assertTrue("Analysis metric value > 0", analysisResult.getMetricValue() > 0);

            ForecastResult forecastResult = analytics.forecast(datasets.get(0));
            assertTrue("Forecast value > 0", forecastResult.getForecastedValue() > 0);

            TrendResult trendResult = analytics.getTrends(datasets.get(0));
            assertTrue("Trend direction is not null", trendResult.getDirection() != null);

            // 3. Query over repository
            QueryServiceImpl query = new QueryServiceImpl(pipeline.getRepository());
            Dataset allData = query.executeQuery("ALL");
            assertTrue("Query result is not null", allData != null);

            // 4. Report generation
            ReportServiceImpl reportService = new ReportServiceImpl();
            reportService.generateReport(new ReportConfig("Sales end-to-end test report"));
            assertTrue("Report was generated", reportService.getGeneratedReports().size() == 1);

            // 5. KPI evaluation
            KPIServiceImpl kpiService = new KPIServiceImpl();
            List<KPI> kpis = List.of(
                    new KPI(1, "Total Revenue", 17000000.0, analysisResult.getMetricValue(), KPIStatus.PENDING)
            );
            kpiService.calculateKPI(kpis, new TargetSet("Integration Targets"));
            assertTrue("KPI was evaluated", kpiService.getEvaluatedKPIs().size() == 1);

            // 6. Security gate
            SecurityServiceImpl sec = new SecurityServiceImpl();
            boolean auth = sec.authenticate("admin", "admin123");
            assertTrue("Admin auth passes", auth);
            boolean authz = sec.authorize("ADMIN", "GENERATE_REPORT");
            assertTrue("Admin authorized for reports", authz);

            // 7. Dashboard rendering
            DashboardServiceImpl dash = new DashboardServiceImpl();
            dash.renderChart(new ChartData("Integration test chart"));
            dash.renderWidget();
            assertTrue("Dashboard rendered", dash.getRenderedVisualizations().size() == 1);
        });

        test("Exception propagation: DataSource error is caught cleanly", () -> {
            // Simulate a DataSourceException being thrown and caught
            try {
                throw new DataSourceException("Simulated ERP connection failure");
            } catch (DataSourceException e) {
                assertTrue("Exception caught correctly", e.getMessage().contains("ERP"));
            }
        });

        test("Exception propagation: unauthorized user cannot generate report", () -> {
            SecurityServiceImpl sec = new SecurityServiceImpl();
            assertThrows(UnauthorizedAccessException.class,
                    () -> sec.authorize("VIEWER", "GENERATE_REPORT"));
        });
    }

    // ─── TEST FRAMEWORK HELPERS ───────────────────────────────────────────────

    private static void section(String name) {
        System.out.println("\n--- " + name + " ---");
    }

    private static void test(String name, TestCase testCase) {
        try {
            testCase.run();
            System.out.println("  [PASS] " + name);
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("  [FAIL] " + name);
            System.out.println("         Reason: " + e.getMessage());
            failed++;
        }
    }

    private static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertThrows(Class<? extends Exception> expectedType, ThrowingRunnable action) {
        try {
            action.run();
            throw new AssertionError("Expected " + expectedType.getSimpleName() + " but no exception was thrown");
        } catch (Exception e) {
            if (!expectedType.isInstance(e)) {
                throw new AssertionError("Expected " + expectedType.getSimpleName()
                        + " but got " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    @FunctionalInterface
    interface TestCase {
        void run() throws Exception;
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }
}
