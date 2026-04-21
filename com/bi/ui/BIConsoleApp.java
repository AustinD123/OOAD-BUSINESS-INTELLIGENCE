package com.bi.ui;

import com.bi.BIDataPipeline;
import com.bi.analytics.AnalyticsServiceImpl;
import com.bi.dashboard.DashboardServiceImpl;
import com.bi.enums.KPIStatus;
import com.bi.exceptions.AuthenticationFailedException;
import com.bi.exceptions.DashboardLoadException;
import com.bi.exceptions.InvalidQueryException;
import com.bi.exceptions.KPIEvaluationException;
import com.bi.exceptions.ReportGenerationException;
import com.bi.exceptions.UnauthorizedAccessException;
import com.bi.kpi.KPIServiceImpl;
import com.bi.models.KPI;
import com.bi.query.QueryServiceImpl;
import com.bi.report.ReportServiceImpl;
import com.bi.security.SecurityServiceImpl;
import com.bi.util.AnalysisResult;
import com.bi.util.ChartData;
import com.bi.util.Dataset;
import com.bi.util.FilterSet;
import com.bi.util.ReportConfig;
import com.bi.util.TargetSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * BIConsoleApp is the Phase 5 faux GUI — a console-menu driven UI that simulates
 * all 4 wireframe screens: Login, BI Dashboard, Report Generation, KPI Monitoring.
 *
 * Run: java com.bi.ui.BIConsoleApp
 * Default credentials: admin / admin123
 */
public class BIConsoleApp {

    private static final String LINE  = "=".repeat(70);
    private static final String DLINE = "-".repeat(70);

    private final Scanner scanner = new Scanner(System.in);

    // Services
    private final SecurityServiceImpl  securityService  = new SecurityServiceImpl();
    private final KPIServiceImpl       kpiService       = new KPIServiceImpl();
    private final ReportServiceImpl    reportService    = new ReportServiceImpl();
    private final DashboardServiceImpl dashboardService = new DashboardServiceImpl();

    // Wired after login
    private BIDataPipeline       pipeline;
    private AnalyticsServiceImpl analyticsService;
    private QueryServiceImpl     queryService;

    // Session state
    private String  loggedInUser = null;
    private boolean running      = true;

    // ─── ENTRY POINT ────────────────────────────────────────────────────────

    public static void main(String[] args) {
        new BIConsoleApp().start();
    }

    private void start() {
        printHeader("BUSINESS INTELLIGENCE SYSTEM", "Team OREO — Sub-system #17");
        showLoginScreen();
    }

    // ─── SCREEN 1: LOGIN ────────────────────────────────────────────────────

    private void showLoginScreen() {
        while (loggedInUser == null) {
            print(LINE);
            print("  BUSINESS INTELLIGENCE");
            print("  [COMPANY LOGO]");
            print(DLINE);
            String username = prompt("  Username");
            String password = promptPassword("  Password");
            print(DLINE);

            try {
                securityService.authenticate(username, password);
                loggedInUser = username;
                print("  Login successful. Welcome, " + username + "!");
                print(LINE);

                // Boot the data pipeline after login
                bootPipeline();
                showMainMenu();

            } catch (AuthenticationFailedException e) {
                print("  [ERROR] " + e.getMessage());
                print("  Please try again.");
            }
        }
    }

    // ─── PIPELINE BOOT ──────────────────────────────────────────────────────

    private void bootPipeline() {
        print("\n  Loading BI data pipeline...");
        pipeline         = new BIDataPipeline();
        pipeline.runAllPipelines();
        analyticsService = new AnalyticsServiceImpl(pipeline.getRepository());
        queryService     = new QueryServiceImpl(pipeline.getRepository());
        print("  Pipeline ready.\n");
    }

    // ─── SCREEN 2: MAIN MENU / DASHBOARD ────────────────────────────────────

    private void showMainMenu() {
        while (running) {
            print(LINE);
            print("  BI MODULE - DASHBOARD                          [User: " + loggedInUser + "]");
            print(DLINE);
            print("  Sidebar:");
            print("    [1] Dashboard");
            print("    [2] Reports");
            print("    [3] KPIs");
            print("    [4] Query");
            print("    [5] Logout");
            print(DLINE);

            String choice = prompt("  Select option");

            switch (choice.trim()) {
                case "1" -> showDashboard();
                case "2" -> {
                    try {
                        securityService.authorize(loggedInUser.toUpperCase(), "GENERATE_REPORT");
                        showReportGeneration();
                    } catch (UnauthorizedAccessException e) {
                        print("  [ACCESS DENIED] " + e.getMessage());
                    }
                }
                case "3" -> showKPIMonitoring();
                case "4" -> showQueryScreen();
                case "5" -> {
                    print("  Logging out. Goodbye, " + loggedInUser + "!");
                    loggedInUser = null;
                    running = false;
                }
                default  -> print("  Invalid option. Please enter 1-5.");
            }
        }
    }

    private void showDashboard() {
        print(LINE);
        print("  BI MODULE - DASHBOARD");
        print(DLINE);

        // KPI Cards
        List<KPI> kpis = buildSampleKPIs();
        try {
            dashboardService.renderKPICards(kpis);
        } catch (DashboardLoadException e) {
            print("  [ERROR] Dashboard load failed: " + e.getMessage());
            return;
        }

        // Sales Trend Chart (from analytics)
        print("\n  Sales Trend Chart:");
        List<AnalysisResult> analysisResults = new ArrayList<>();
        List<Dataset> allDatasets = pipeline.getRepository().index();
        for (Dataset ds : allDatasets) {
            try {
                analysisResults.add(analyticsService.analyze(ds));
            } catch (Exception ignored) {}
        }

        if (!analysisResults.isEmpty()) {
            try {
                dashboardService.renderSalesTrend(analysisResults);
            } catch (DashboardLoadException e) {
                print("  [ERROR] " + e.getMessage());
            }
        } else {
            print("  [No trend data available]");
        }

        // Alerts Panel
        print("\n  Alerts Panel:");
        print("    - Target not met: Monthly Sales");
        print("    - Inventory Low");

        // Render generic widget
        dashboardService.renderWidget();

        prompt("\n  Press Enter to return to menu");
    }

    // ─── SCREEN 3: REPORT GENERATION ────────────────────────────────────────

    private void showReportGeneration() {
        print(LINE);
        print("  BI MODULE - REPORTS                                      [BACK=0]");
        print(DLINE);

        String fromDate = prompt("  Select Date Range - From (e.g. 2026-01-01)");
        String toDate   = prompt("  Select Date Range - To   (e.g. 2026-03-31)");

        print("  Select Department:");
        print("    [1] Sales  [2] HR  [3] Finance  [4] All");
        String deptChoice = prompt("  Department");
        String department = switch (deptChoice.trim()) {
            case "1" -> "Sales";
            case "2" -> "HR";
            case "3" -> "Finance";
            default  -> "All";
        };

        print("  Select Report Type:");
        print("    [1] Sales  [2] Financial  [3] HR  [4] KPI");
        String typeChoice = prompt("  Report Type");
        String reportType = switch (typeChoice.trim()) {
            case "1" -> "Sales";
            case "2" -> "Financial";
            case "3" -> "HR";
            case "4" -> "KPI";
            default  -> "General";
        };

        print(DLINE);
        print("  Generating report...");

        String configDesc = reportType + " report for " + department
                + " department from " + fromDate + " to " + toDate;
        ReportConfig config = new ReportConfig(configDesc);

        try {
            reportService.generateReport(config);

            // Report Preview Area
            print("\n  ┌─────────────────────────────────────────────────────┐");
            print("  │              REPORT PREVIEW AREA                    │");
            print("  │                                                     │");
            print("  │  Report Type : " + padRight(reportType, 37) + "│");
            print("  │  Department  : " + padRight(department, 37) + "│");
            print("  │  Date Range  : " + padRight(fromDate + " to " + toDate, 37) + "│");
            print("  │  Generated   : " + padRight(java.time.LocalDateTime.now().toString().substring(0, 19), 37) + "│");
            print("  │                                                     │");

            // Show data preview from repository
            List<Dataset> datasets = pipeline.getRepository().index();
            int rowCount = 0;
            for (Dataset ds : datasets) {
                if (ds.getId().toLowerCase().contains(reportType.toLowerCase()) ||
                    ds.getId().toLowerCase().contains(department.toLowerCase()) ||
                    department.equals("All")) {
                    print("  │  Dataset: " + padRight(ds.getId().substring(0, Math.min(ds.getId().length(), 41)), 41) + "│");
                    rowCount++;
                    if (rowCount >= 3) break;
                }
            }
            if (rowCount == 0) {
                print("  │  [Table/Report Preview]                             │");
            }
            print("  └─────────────────────────────────────────────────────┘");

            // Export options
            print("\n  Export options:");
            print("    [1] Download PDF");
            print("    [2] Download Excel");
            print("    [3] Skip");
            String exportChoice = prompt("  Select export");

            switch (exportChoice.trim()) {
                case "1" -> { reportService.exportPDF();   print("  PDF export complete."); }
                case "2" -> { reportService.exportExcel(); print("  Excel export complete."); }
                default  -> print("  Export skipped.");
            }

        } catch (ReportGenerationException e) {
            print("  [ERROR] Report generation failed: " + e.getMessage());
        }

        prompt("\n  Press Enter to return to menu");
    }

    // ─── SCREEN 4: KPI MONITORING ────────────────────────────────────────────

    private void showKPIMonitoring() {
        print(LINE);
        print("  BI MODULE - KPI MONITORING                               [BACK=0]");
        print(DLINE);

        List<KPI> kpis = buildSampleKPIs();
        TargetSet targets = new TargetSet("Q1 2026 Business Targets");

        try {
            kpiService.calculateKPI(kpis, targets);

            print("\n  ┌──────────────────────────────────────────────────────────────────┐");
            print("  │  KPI Name                   │ Target Value  │ Actual Value  │ Status       │");
            print("  ├──────────────────────────────────────────────────────────────────┤");

            for (KPI kpi : kpis) {
                double achievement = kpi.getTargetValue() == 0 ? 1.0 :
                        kpi.getActualValue() / kpi.getTargetValue();
                String status = achievement >= 1.0 ? "ACHIEVED" :
                        achievement >= 0.75 ? "PENDING" : "NOT ACHIEVED";

                System.out.printf("  │  %-27s│ %-13.2f │ %-13.2f │ %-12s │%n",
                        kpi.getKpiName(), kpi.getTargetValue(), kpi.getActualValue(), status);
            }
            print("  └──────────────────────────────────────────────────────────────────┘");

            print("\n  [ REFRESH KPIs ]");
            String refresh = prompt("  Press R to refresh or Enter to go back");
            if ("r".equalsIgnoreCase(refresh.trim())) {
                kpiService.evaluateTarget();
                print("  KPIs refreshed.");
            }

        } catch (KPIEvaluationException e) {
            print("  [ERROR] KPI evaluation failed: " + e.getMessage());
        }

        prompt("\n  Press Enter to return to menu");
    }

    // ─── QUERY SCREEN ────────────────────────────────────────────────────────

    private void showQueryScreen() {
        print(LINE);
        print("  BI MODULE - QUERY                                        [BACK=0]");
        print(DLINE);
        print("  Filter operators: EQUALS | CONTAINS | GREATER_THAN | LESS_THAN | ALL");
        print(DLINE);

        String field    = prompt("  Filter field (e.g. carModel, department, revenue, salary)");
        String operator = prompt("  Operator (EQUALS/CONTAINS/GREATER_THAN/LESS_THAN/ALL)").toUpperCase();
        String value    = "";

        if (!"ALL".equals(operator)) {
            value = prompt("  Value");
        }

        try {
            Dataset result;
            if ("ALL".equals(operator)) {
                result = queryService.executeQuery("ALL");
            } else {
                FilterSet filter = new FilterSet(field, value, operator);
                queryService.applyFilters(filter);
                result = queryService.executeQuery("Filter " + field + " " + operator + " " + value);
            }

            // Count results
            int count = 0;
            if (result != null && result.getData() instanceof List) {
                count = ((List<?>) result.getData()).size();
            }

            print("\n  Query executed. Records returned: " + count);
            print("  Dataset ID: " + (result != null ? result.getId() : "N/A"));

            if (count > 0 && result.getData() instanceof List<?> rows) {
                print("\n  Preview (first 3 records):");
                print(DLINE);
                int shown = 0;
                for (Object row : rows) {
                    if (shown >= 3) break;
                    print("  " + row.toString());
                    shown++;
                }
                print(DLINE);
            }

        } catch (InvalidQueryException e) {
            print("  [ERROR] Query failed: " + e.getMessage());
        }

        prompt("\n  Press Enter to return to menu");
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private List<KPI> buildSampleKPIs() {
        List<KPI> kpis = new ArrayList<>();
        kpis.add(new KPI(1, "Total Revenue",     17000000.0, 17100000.0, KPIStatus.PENDING));
        kpis.add(new KPI(2, "Monthly Sales",      1000000.0,   800000.0, KPIStatus.PENDING));
        kpis.add(new KPI(3, "Profit Margin %",         20.0,      22.0, KPIStatus.PENDING));
        kpis.add(new KPI(4, "Units Sold",              650.0,     500.0, KPIStatus.PENDING));
        kpis.add(new KPI(5, "Employee Headcount",      200.0,     210.0, KPIStatus.PENDING));
        return kpis;
    }

    private void printHeader(String title, String subtitle) {
        print(LINE);
        print("  " + title);
        print("  " + subtitle);
        print(LINE);
    }

    private void print(String msg) {
        System.out.println(msg);
    }

    private String prompt(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine();
    }

    private String promptPassword(String label) {
        System.out.print(label + ": ");
        // In a real terminal, System.console().readPassword() would hide input.
        // Scanner is used here for compatibility in all environments.
        return scanner.nextLine();
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
