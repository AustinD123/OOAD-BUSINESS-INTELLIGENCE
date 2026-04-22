package com.bi.mock;

import com.bi.interfaces.IReportService;
import com.bi.util.ReportConfig;

/**
 * Mock implementation of IReportService.
 * Simulates report generation and export with console output.
 */
public class ReportServiceMock implements IReportService {

    private String lastGeneratedReport = "";

    @Override
    public void generateReport(ReportConfig config) {
        lastGeneratedReport = "BI Report | Config: " + config.getDescription()
                + " | Generated: " + java.time.LocalDateTime.now();
        System.out.println("[ReportServiceMock] " + lastGeneratedReport);
    }

    @Override
    public void exportPDF() {
        System.out.println("[ReportServiceMock] Exporting PDF: " + lastGeneratedReport);
    }

    @Override
    public void exportExcel() {
        System.out.println("[ReportServiceMock] Exporting Excel: " + lastGeneratedReport);
    }

    /** Returns the last generated report string — used by ReportController for UI display. */
    public String getLastGeneratedReport() {
        return lastGeneratedReport;
    }
}
