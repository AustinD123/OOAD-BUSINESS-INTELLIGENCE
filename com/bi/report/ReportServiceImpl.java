package com.bi.report;

import com.bi.db.ERPClient;
import com.bi.enums.ReportFormat;
import com.bi.exceptions.ReportGenerationException;
import com.bi.interfaces.IReportService;
import com.bi.models.Report;
import com.bi.util.ReportConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReportServiceImpl implements IReportService to generate, export, and persist reports.
 *
 * Database access is performed exclusively through the Integration team's ERP SDK
 * (via ERPClient) against the shared RDS instance — no direct JDBC.
 *
 * Canonical table used: reports
 *   Writable columns: report_name, report_type, title, content, start_date, end_date
 *   (generated_date / format / generated_by are either server-defaulted or optional)
 */
public class ReportServiceImpl implements IReportService {

    private final List<Report> generatedReports = new ArrayList<>();
    private int reportCounter = 0;
    private Report lastReport = null;

    // We track the RDS-generated primary key so we can update the same row on export.
    private long lastReportRdsId = -1;

    /**
     * Generates a report based on the provided configuration and persists it to the
     * shared RDS via the ERP SDK.
     *
     * @param config the report configuration describing type and content
     * @throws ReportGenerationException if config is null/blank or DB persistence fails
     */
    @Override
    public void generateReport(ReportConfig config) {
        if (config == null) {
            throw new ReportGenerationException("ReportConfig cannot be null.");
        }
        if (config.getDescription() == null || config.getDescription().isBlank()) {
            throw new ReportGenerationException("ReportConfig must have a non-empty description.");
        }

        reportCounter++;
        String reportName = "BI-Report-" + String.format("%03d", reportCounter);
        String reportType = resolveReportType(config.getDescription());

        Report report = new Report(
                reportCounter,
                reportName,
                reportType,
                LocalDateTime.now(),
                ReportFormat.PDF,
                config.getDescription()
        );

        generatedReports.add(report);
        lastReport = report;

        System.out.println("[ReportService] Generated report: " + reportName
                + " | Type: " + reportType
                + " | Config: " + config.getDescription());

        persistReport(report);
    }

    /**
     * Exports the last generated report as PDF.
     *
     * @throws ReportGenerationException if no report has been generated yet
     */
    @Override
    public void exportPDF() {
        if (lastReport == null) {
            throw new ReportGenerationException(
                    "No report available to export as PDF. Call generateReport() first.");
        }
        System.out.println("[ReportService] Exporting report '"
                + lastReport.getReportName() + "' as PDF.");
        updateReportFormat(lastReport, ReportFormat.PDF);
        System.out.println("[ReportService] PDF export complete for: " + lastReport.getReportName());
    }

    /**
     * Exports the last generated report as Excel.
     *
     * @throws ReportGenerationException if no report has been generated yet
     */
    @Override
    public void exportExcel() {
        if (lastReport == null) {
            throw new ReportGenerationException(
                    "No report available to export as Excel. Call generateReport() first.");
        }
        System.out.println("[ReportService] Exporting report '"
                + lastReport.getReportName() + "' as Excel.");
        updateReportFormat(lastReport, ReportFormat.EXCEL);
        System.out.println("[ReportService] Excel export complete for: " + lastReport.getReportName());
    }

    /** Returns all generated reports so far. */
    public List<Report> getGeneratedReports() {
        return generatedReports;
    }

    /** Returns the most recently generated report, or null if none. */
    public Report getLastReport() {
        return lastReport;
    }

    // ─── private helpers ───────────────────────────────────────────────────────

    private String resolveReportType(String description) {
        String lower = description.toLowerCase();
        if (lower.contains("sales"))                     return "SALES";
        if (lower.contains("hr") || lower.contains("human")) return "HR";
        if (lower.contains("finance") || lower.contains("financial")) return "FINANCE";
        if (lower.contains("kpi"))                       return "KPI";
        return "GENERAL";
    }

    /**
     * Persists a newly generated report to the shared RDS via the ERP SDK.
     * Uses the canonical `reports` table.
     *
     * Required writable columns: report_name, report_type, title, content
     */
    private void persistReport(Report report) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("report_name",  report.getReportName());
            payload.put("report_type",  report.getReportType());
            payload.put("title",        report.getReportName());  // title maps to report_name
            payload.put("content",      String.valueOf(report.getReportData()));

            lastReportRdsId = ERPClient.create("reports", payload);

            System.out.println("[ReportService] Report persisted to RDS (id=" + lastReportRdsId
                    + "): " + report.getReportName());

        } catch (Exception e) {
            throw new ReportGenerationException(
                    "Failed to persist report to RDS: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the content/title of the persisted report to reflect the chosen export format.
     * Since the canonical schema does not have a mutable format_code column accessible to BI,
     * we update the `content` field with a note about the chosen export format.
     */
    private void updateReportFormat(Report report, ReportFormat format) {
        if (lastReportRdsId < 0) {
            // Report was never persisted (e.g. DB was unavailable) — log and continue.
            System.err.println("[ReportService] Warning: no RDS id for last report; skipping format update.");
            return;
        }
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("content", String.valueOf(report.getReportData())
                    + " [Exported as " + format.name() + "]");

            ERPClient.update("reports", "report_id", lastReportRdsId, payload);

            System.out.println("[ReportService] Report format updated in RDS to "
                    + format.name() + " for: " + report.getReportName());

        } catch (Exception e) {
            // Non-fatal — export still happened in-process.
            System.err.println("[ReportService] Warning: could not update report format in RDS: "
                    + e.getMessage());
        }
    }
}
