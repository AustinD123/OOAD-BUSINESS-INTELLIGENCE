package com.bi.controller;

import com.bi.mock.ReportServiceMock;
import com.bi.util.ReportConfig;

/**
 * Controller for the Report screen.
 * Delegates to ReportServiceMock (which implements IReportService).
 */
public class ReportController {

    private final ReportServiceMock reportService;

    public ReportController(ReportServiceMock reportService) {
        this.reportService = reportService;
    }

    /**
     * Generates a report with the given type and date range.
     *
     * @param reportType e.g. "Sales Report", "KPI Summary"
     * @param from       start date string
     * @param to         end date string
     * @return generated report description string for display
     */
    public String generateReport(String reportType, String from, String to) {
        if (reportType == null || reportType.isBlank()) return "Error: select a report type.";
        if (from.isBlank() || to.isBlank())             return "Error: enter a valid date range.";

        ReportConfig config = new ReportConfig(reportType + " | " + from + " to " + to);
        reportService.generateReport(config);
        return reportService.getLastGeneratedReport();
    }

    /** Triggers PDF export of the last generated report. */
    public String exportPDF() {
        reportService.exportPDF();
        return "PDF export triggered. Check console output.";
    }

    /** Triggers Excel export of the last generated report. */
    public String exportExcel() {
        reportService.exportExcel();
        return "Excel export triggered. Check console output.";
    }
}
