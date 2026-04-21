package com.bi;

import com.bi.enums.KPIStatus;
import com.bi.exceptions.AuthenticationFailedException;
import com.bi.exceptions.KPIEvaluationException;
import com.bi.exceptions.ReportGenerationException;
import com.bi.exceptions.UnauthorizedAccessException;
import com.bi.kpi.KPIServiceImpl;
import com.bi.models.KPI;
import com.bi.report.ReportServiceImpl;
import com.bi.security.SecurityServiceImpl;
import com.bi.util.ReportConfig;
import com.bi.util.TargetSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase4Runner demonstrates the Phase 4 services: ReportService, KPIService, SecurityService.
 * It wires together all three implementations and exercises each service method,
 * with DB persistence where the database is reachable.
 */
public class Phase4Runner {

    private final ReportServiceImpl reportService;
    private final KPIServiceImpl kpiService;
    private final SecurityServiceImpl securityService;

    /**
     * Constructs and wires all Phase 4 services.
     */
    public Phase4Runner() {
        this.reportService  = new ReportServiceImpl();
        this.kpiService     = new KPIServiceImpl();
        this.securityService = new SecurityServiceImpl();
    }

    /**
     * Demonstrates report generation and export flows.
     */
    public void demonstrateReporting() {
        System.out.println("\n========== Phase 4: Report Service Demo ==========");

        // Generate a sales report
        try {
            ReportConfig salesConfig = new ReportConfig("Monthly Sales performance report Q1 2026");
            reportService.generateReport(salesConfig);
            reportService.exportPDF();
        } catch (ReportGenerationException e) {
            System.err.println("[Phase4Runner] Report error: " + e.getMessage());
        }

        // Generate a finance report and export as Excel
        try {
            ReportConfig financeConfig = new ReportConfig("Financial summary and budget analysis");
            reportService.generateReport(financeConfig);
            reportService.exportExcel();
        } catch (ReportGenerationException e) {
            System.err.println("[Phase4Runner] Report error: " + e.getMessage());
        }

        // Generate a KPI report
        try {
            ReportConfig kpiConfig = new ReportConfig("KPI tracking report for all departments");
            reportService.generateReport(kpiConfig);
            reportService.exportPDF();
        } catch (ReportGenerationException e) {
            System.err.println("[Phase4Runner] Report error: " + e.getMessage());
        }

        System.out.println("[Phase4Runner] Reports generated: " + reportService.getGeneratedReports().size());
    }

    /**
     * Demonstrates KPI calculation and target evaluation flows.
     */
    public void demonstrateKPI() {
        System.out.println("\n========== Phase 4: KPI Service Demo ==========");

        List<KPI> kpis = new ArrayList<>();
        kpis.add(new KPI(1, "Total Revenue",          17000000.0, 17100000.0, KPIStatus.PENDING));
        kpis.add(new KPI(2, "Units Sold",              650.0,       500.0,     KPIStatus.PENDING));
        kpis.add(new KPI(3, "Employee Headcount",      200.0,       210.0,     KPIStatus.PENDING));
        kpis.add(new KPI(4, "Average Salary",          75000.0,     80000.0,   KPIStatus.PENDING));
        kpis.add(new KPI(5, "Finance Transaction Vol", 5000000.0,   3000000.0, KPIStatus.PENDING));

        TargetSet targets = new TargetSet("Q1 2026 Business Targets");

        try {
            kpiService.calculateKPI(kpis, targets);
            kpiService.evaluateTarget();
        } catch (KPIEvaluationException e) {
            System.err.println("[Phase4Runner] KPI error: " + e.getMessage());
        }
    }

    /**
     * Demonstrates authentication, authorization, and permission retrieval flows.
     */
    public void demonstrateSecurity() {
        System.out.println("\n========== Phase 4: Security Service Demo ==========");

        // Authentication — valid fallback admin
        try {
            boolean authResult = securityService.authenticate("admin", "admin123");
            System.out.println("[Phase4Runner] Admin auth result: " + authResult);
        } catch (AuthenticationFailedException e) {
            System.err.println("[Phase4Runner] Auth failed: " + e.getMessage());
        }

        // Authentication — invalid credentials
        try {
            securityService.authenticate("hacker", "wrongpass");
        } catch (AuthenticationFailedException e) {
            System.out.println("[Phase4Runner] Expected auth failure caught: " + e.getMessage());
        }

        // Authorization — ADMIN role has GENERATE_REPORT
        try {
            boolean authz = securityService.authorize("ADMIN", "GENERATE_REPORT");
            System.out.println("[Phase4Runner] ADMIN -> GENERATE_REPORT: " + authz);
        } catch (UnauthorizedAccessException e) {
            System.err.println("[Phase4Runner] Authorization denied: " + e.getMessage());
        }

        // Authorization — VIEWER role does not have RUN_QUERY
        try {
            securityService.authorize("VIEWER", "RUN_QUERY");
        } catch (UnauthorizedAccessException e) {
            System.out.println("[Phase4Runner] Expected unauthorized caught: " + e.getMessage());
        }

        // Permissions for ANALYST
        List<String> perms = securityService.getPermissions("ANALYST");
        System.out.println("[Phase4Runner] ANALYST permissions: " + perms);
    }

    /**
     * Entry point for the full Phase 4 demonstration.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Phase4Runner runner = new Phase4Runner();
        runner.demonstrateReporting();
        runner.demonstrateKPI();
        runner.demonstrateSecurity();
        System.out.println("\n========== Phase 4 Complete ==========");
    }
}
