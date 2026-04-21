package com.bi.report;

import com.bi.db.ERPClient;
import com.bi.enums.ReportFormat;
import com.bi.exceptions.ReportGenerationException;
import com.bi.interfaces.IReportService;
import com.bi.models.Report;
import com.bi.util.Dataset;
import com.bi.util.ReportConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReportServiceImpl implements IReportService to generate, export, and persist reports.
 *
 * PDF export  → writes a formatted .txt file (named .pdf) to reports/
 * Excel export → writes a CSV file (named .csv) to reports/
 *
 * Database access via ERPClient (Integration team ERP SDK) — no direct JDBC.
 */
public class ReportServiceImpl implements IReportService {

    private static final String REPORTS_DIR = "reports";

    private final List<Report> generatedReports = new ArrayList<>();
    private int reportCounter = 0;
    private Report lastReport = null;
    private long lastReportRdsId = -1;

    // Extra context set by the UI before calling generateReport()
    private List<Dataset> reportDatasets = new ArrayList<>();
    private String reportFromDate = "";
    private String reportToDate   = "";
    private String reportDept     = "";

    /**
     * Called by the UI to pass the datasets and date range before generateReport().
     */
    public void setReportContext(List<Dataset> datasets, String fromDate,
                                  String toDate, String dept) {
        this.reportDatasets = datasets != null ? datasets : new ArrayList<>();
        this.reportFromDate = fromDate != null ? fromDate : "";
        this.reportToDate   = toDate   != null ? toDate   : "";
        this.reportDept     = dept     != null ? dept     : "";
    }

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

    @Override
    public void exportPDF() {
        if (lastReport == null) {
            throw new ReportGenerationException(
                    "No report available to export as PDF. Call generateReport() first.");
        }
        System.out.println("[ReportService] Exporting report '"
                + lastReport.getReportName() + "' as PDF.");

        String filePath = writeReportFile(lastReport, ReportFormat.PDF);
        updateReportFormat(lastReport, ReportFormat.PDF);

        System.out.println("[ReportService] PDF export complete → " + filePath);
    }

    @Override
    public void exportExcel() {
        if (lastReport == null) {
            throw new ReportGenerationException(
                    "No report available to export as Excel. Call generateReport() first.");
        }
        System.out.println("[ReportService] Exporting report '"
                + lastReport.getReportName() + "' as Excel.");

        String filePath = writeReportFile(lastReport, ReportFormat.EXCEL);
        updateReportFormat(lastReport, ReportFormat.EXCEL);

        System.out.println("[ReportService] Excel export complete → " + filePath);
    }

    public List<Report> getGeneratedReports() { return generatedReports; }
    public Report getLastReport()              { return lastReport; }

    // ─── file writing ─────────────────────────────────────────────────────────

    /**
     * Writes the report to disk.
     * PDF  → formatted text file at reports/<name>.pdf
     * Excel → CSV file at reports/<name>.csv
     *
     * @return the path of the written file
     */
    private String writeReportFile(Report report, ReportFormat format) {
        try {
            Path dir = Paths.get(REPORTS_DIR);
            Files.createDirectories(dir);

            String ext      = format == ReportFormat.EXCEL ? ".csv" : ".pdf";
            String fileName = report.getReportName()
                    .replace(" ", "_")
                    + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    + ext;
            Path filePath = dir.resolve(fileName);

            if (format == ReportFormat.EXCEL) {
                writeCsv(filePath, report);
            } else {
                writePdf(filePath, report);
            }

            return filePath.toAbsolutePath().toString();

        } catch (IOException e) {
            throw new ReportGenerationException(
                    "Failed to write report file: " + e.getMessage(), e);
        }
    }

    /** Writes a formatted text-based "PDF" report. */
    private void writePdf(Path path, Report report) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path.toFile()))) {
            String sep = "=".repeat(60);
            String dash = "-".repeat(60);

            pw.println(sep);
            pw.println("  BUSINESS INTELLIGENCE REPORT");
            pw.println("  Team OREO — Sub-system #17");
            pw.println(sep);
            pw.println("  Report Name : " + report.getReportName());
            pw.println("  Report Type : " + report.getReportType());
            pw.println("  Department  : " + reportDept);
            pw.println("  Date Range  : " + reportFromDate + " to " + reportToDate);
            pw.println("  Generated   : " + report.getGeneratedDate()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pw.println(sep);
            pw.println();

            if (reportDatasets.isEmpty()) {
                pw.println("  [No data available for this report]");
            } else {
                for (Dataset ds : reportDatasets) {
                    pw.println("  Dataset : " + ds.getId());
                    pw.println("  " + dash);
                    if (ds.getData() instanceof List<?> rows) {
                        for (Object row : rows) {
                            pw.println("    " + row.toString());
                        }
                    } else {
                        pw.println("    " + ds.getData());
                    }
                    pw.println();
                }
            }

            pw.println(sep);
            pw.println("  END OF REPORT");
            pw.println(sep);
        }
    }

    /** Writes a CSV-based Excel report. */
    private void writeCsv(Path path, Report report) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path.toFile()))) {
            pw.println("Report Name,Report Type,Department,Date From,Date To,Generated");
            pw.println(csv(report.getReportName()) + "," + csv(report.getReportType()) + ","
                    + csv(reportDept) + "," + csv(reportFromDate) + "," + csv(reportToDate) + ","
                    + csv(report.getGeneratedDate()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            pw.println();

            if (reportDatasets.isEmpty()) {
                pw.println("No data available");
            } else {
                pw.println("Dataset ID,Record");
                for (Dataset ds : reportDatasets) {
                    if (ds.getData() instanceof List<?> rows) {
                        for (Object row : rows) {
                            pw.println(csv(ds.getId()) + "," + csv(row.toString()));
                        }
                    } else {
                        pw.println(csv(ds.getId()) + "," + csv(String.valueOf(ds.getData())));
                    }
                }
            }
        }
    }

    private String csv(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    // ─── RDS helpers ──────────────────────────────────────────────────────────

    private void persistReport(Report report) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("report_name", report.getReportName());
            payload.put("report_type", report.getReportType());
            payload.put("title",       report.getReportName());
            payload.put("content",     String.valueOf(report.getReportData()));
            if (!reportFromDate.isBlank()) payload.put("start_date", reportFromDate);
            if (!reportToDate.isBlank())   payload.put("end_date",   reportToDate);

            lastReportRdsId = ERPClient.create("reports", payload);
            System.out.println("[ReportService] Report persisted to RDS (id="
                    + lastReportRdsId + "): " + report.getReportName());

        } catch (Exception e) {
            throw new ReportGenerationException(
                    "Failed to persist report to RDS: " + e.getMessage(), e);
        }
    }

    private void updateReportFormat(Report report, ReportFormat format) {
        if (lastReportRdsId < 0) {
            System.err.println("[ReportService] Warning: no RDS id; skipping format update.");
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
            System.err.println("[ReportService] Warning: could not update RDS: " + e.getMessage());
        }
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private String resolveReportType(String description) {
        String lower = description.toLowerCase();
        if (lower.contains("sales"))                          return "SALES";
        if (lower.contains("hr") || lower.contains("human")) return "HR";
        if (lower.contains("finance") || lower.contains("financial")) return "FINANCE";
        if (lower.contains("kpi"))                            return "KPI";
        return "GENERAL";
    }
}
